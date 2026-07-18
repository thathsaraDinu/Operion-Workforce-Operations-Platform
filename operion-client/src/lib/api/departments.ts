import { apiClient } from "./client";
import type { Department, Employee, Page, PageParams } from "./types";

function qp(p?: PageParams) {
  return { params: { page: p?.page ?? 0, size: p?.size ?? 10, sort: p?.sort } };
}

export interface DepartmentPayload {
  name: string;
  code: string;
  description?: string;
}

export const departmentsApi = {
  list: (p?: PageParams) =>
    apiClient.get<Page<Department>>("/api/departments", qp(p)).then((r) => r.data),
  get: (id: number) => apiClient.get<Department>(`/api/departments/${id}`).then((r) => r.data),
  create: (payload: DepartmentPayload) =>
    apiClient.post<Department>("/api/departments", payload).then((r) => r.data),
  update: (id: number, payload: Partial<DepartmentPayload>) =>
    apiClient.put<Department>(`/api/departments/${id}`, payload).then((r) => r.data),
  remove: (id: number) => apiClient.delete<void>(`/api/departments/${id}`).then((r) => r.data),
  employees: (id: number, p?: PageParams) =>
    apiClient
      .get<Page<Employee>>(`/api/departments/${id}/employees`, qp(p))
      .then((r) => r.data),
};
