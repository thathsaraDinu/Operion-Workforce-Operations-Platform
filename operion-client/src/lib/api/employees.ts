import { apiClient } from "./client";
import type { Employee, Page, PageParams, Role } from "./types";

function qp(p?: PageParams) {
  return { params: { page: p?.page ?? 0, size: p?.size ?? 10, sort: p?.sort } };
}

export interface EmployeeCreatePayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: Role;
  departmentId?: number;
  phone?: string;
  address?: string;
  position?: string;
  joiningDate?: string;
}

export type EmployeeUpdatePayload = Partial<Omit<EmployeeCreatePayload, "departmentId">>;

export const employeesApi = {
  list: (p?: PageParams) =>
    apiClient.get<Page<Employee>>("/api/employees", qp(p)).then((r) => r.data),
  get: (id: number) => apiClient.get<Employee>(`/api/employees/${id}`).then((r) => r.data),
  create: (payload: EmployeeCreatePayload) =>
    apiClient.post<Employee>("/api/employees", payload).then((r) => r.data),
  update: (id: number, payload: EmployeeUpdatePayload) =>
    apiClient.patch<Employee>(`/api/employees/${id}`, payload).then((r) => r.data),
  remove: (id: number) => apiClient.delete<void>(`/api/employees/${id}`).then((r) => r.data),
  assignDepartment: (employeeId: number, departmentId: number) =>
    apiClient
      .patch<Employee>(`/api/employees/${employeeId}/department/${departmentId}`)
      .then((r) => r.data),
};
