import { apiClient } from "./client";
import type { Role } from "./types";

export interface LoginPayload {
  email: string;
  password: string;
}

export interface ForgotPasswordPayload {
  email: string;
}

export interface ResetPasswordPayload {
  token: string;
  newPassword: string;
}

export interface ChangePasswordPayload {
  currentPassword: string;
  newPassword: string;
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

export async function forgotPassword(payload: ForgotPasswordPayload): Promise<void> {
  await apiClient.post("/api/auth/forgot-password", payload);
}

export async function resetPassword(payload: ResetPasswordPayload): Promise<void> {
  await apiClient.post("/api/auth/reset-password", payload);
}

export async function changePassword(payload: ChangePasswordPayload): Promise<void> {
  await apiClient.post("/api/auth/change-password", payload);
}
