import { apiClient } from "./client";
import type { LeaveRequest, LeaveStatus, LeaveType, Page, PageParams } from "./types";

function qp(p?: PageParams) {
  return { params: { page: p?.page ?? 0, size: p?.size ?? 10, sort: p?.sort } };
}

export interface LeaveCreatePayload {
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  reason: string;
}

export const leavesApi = {
  create: (payload: LeaveCreatePayload) =>
    apiClient.post<LeaveRequest>("/api/leaves", payload).then((r) => r.data),
  mine: (p?: PageParams) =>
    apiClient.get<Page<LeaveRequest>>("/api/leaves/me", qp(p)).then((r) => r.data),
  all: (p?: PageParams) =>
    apiClient.get<Page<LeaveRequest>>("/api/leaves", qp(p)).then((r) => r.data),
  get: (id: number) => apiClient.get<LeaveRequest>(`/api/leaves/${id}`).then((r) => r.data),
  byStatus: (status: LeaveStatus, p?: PageParams) =>
    apiClient
      .get<Page<LeaveRequest>>(`/api/leaves/status/${status}`, qp(p))
      .then((r) => r.data),
  update: (id: number, payload: Partial<LeaveCreatePayload>) =>
    apiClient.put<LeaveRequest>(`/api/leaves/${id}`, payload).then((r) => r.data),
  decide: (id: number, status: "APPROVED" | "REJECTED") =>
    apiClient
      .patch<LeaveRequest>(`/api/leaves/${id}/approval`, { status })
      .then((r) => r.data),
  remove: (id: number) => apiClient.delete<void>(`/api/leaves/${id}`).then((r) => r.data),
};
