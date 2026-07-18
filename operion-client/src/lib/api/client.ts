import axios, { AxiosError } from "axios";

export const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? "http://localhost:8080";

export const TOKEN_STORAGE_KEY = "operion.token";

/**
 * Standard envelope returned by the Operion API.
 *   { success, message, data, timestamp }
 * The response interceptor unwraps `data` so services can stay clean.
 */
export interface ApiEnvelope<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

apiClient.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const token = window.localStorage.getItem(TOKEN_STORAGE_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

function isEnvelope(v: unknown): v is ApiEnvelope<unknown> {
  return (
    typeof v === "object" &&
    v !== null &&
    "success" in v &&
    "data" in v &&
    typeof (v as { success: unknown }).success === "boolean"
  );
}

apiClient.interceptors.response.use(
  (response) => {
    // Unwrap the ApiResponseBody envelope so callers get the payload directly.
    // Endpoints returning 204/No Content or non-envelope shapes pass through unchanged.
    const body = response.data;
    if (isEnvelope(body)) {
      if (body.success === false) {
        // Server reported failure with a 2xx — surface as an axios error.
        return Promise.reject(
          new AxiosError(
            body.message || "Request failed",
            "ERR_API",
            response.config,
            response.request,
            response,
          ),
        );
      }
      response.data = body.data;
    }
    return response;
  },
  (error: AxiosError<unknown>) => {
    if (error.response?.status === 401 && typeof window !== "undefined") {
      window.localStorage.removeItem(TOKEN_STORAGE_KEY);
      if (!window.location.pathname.startsWith("/login")) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  },
);

export function apiErrorMessage(err: unknown, fallback = "Something went wrong") {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as
      | { message?: string; error?: string }
      | undefined;
    return data?.message ?? data?.error ?? err.message ?? fallback;
  }
  return fallback;
}
