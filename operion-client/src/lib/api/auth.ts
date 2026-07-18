import { apiClient } from "./client";
import type { Role } from "./types";

export interface LoginPayload {
  email: string;
  password: string;
}

/**
 * The API's login endpoint now returns identity claims alongside the JWT,
 * so the client no longer has to decode the token to build the session.
 */
export interface LoginResponse {
  token: string;
  id: number;
  email: string;
  role: Role;
}

export async function login(payload: LoginPayload): Promise<LoginResponse> {
  const { data } = await apiClient.post<LoginResponse>("/api/auth/login", payload);
  return data;
}
