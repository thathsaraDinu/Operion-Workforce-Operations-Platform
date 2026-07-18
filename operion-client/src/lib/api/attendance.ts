import { apiClient } from "./client";
import type { Attendance, Page, PageParams } from "./types";

function qp(p?: PageParams) {
  return { params: { page: p?.page ?? 0, size: p?.size ?? 10, sort: p?.sort } };
}

export const attendanceApi = {
  clockIn: () => apiClient.post<Attendance>("/api/attendance/clock-in").then((r) => r.data),
  clockOut: () => apiClient.patch<Attendance>("/api/attendance/clock-out").then((r) => r.data),
  mine: (p?: PageParams) =>
    apiClient.get<Page<Attendance>>("/api/attendance/me", qp(p)).then((r) => r.data),
  all: (p?: PageParams) =>
    apiClient.get<Page<Attendance>>("/api/attendance", qp(p)).then((r) => r.data),
  forEmployee: (employeeId: number, p?: PageParams) =>
    apiClient
      .get<Page<Attendance>>(`/api/attendance/employee/${employeeId}`, qp(p))
      .then((r) => r.data),
  update: (id: number, payload: { clockIn?: string; clockOut?: string }) =>
    apiClient.put<Attendance>(`/api/attendance/${id}`, payload).then((r) => r.data),
};
