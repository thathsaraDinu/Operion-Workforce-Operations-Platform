import { apiClient } from "./client";
import type { Page, PageParams, Task, TaskPriority, TaskStatus } from "./types";

function qp(p?: PageParams) {
  return { params: { page: p?.page ?? 0, size: p?.size ?? 10, sort: p?.sort } };
}

export interface TaskCreatePayload {
  title: string;
  priority: TaskPriority;
  description?: string;
  status: TaskStatus;
  projectId: number;
  assignedEmployeeId: number;
}

export const tasksApi = {
  create: (payload: TaskCreatePayload) =>
    apiClient.post<Task>("/api/tasks", payload).then((r) => r.data),
  list: (p?: PageParams) => apiClient.get<Page<Task>>("/api/tasks", qp(p)).then((r) => r.data),
  get: (id: number) => apiClient.get<Task>(`/api/tasks/${id}`).then((r) => r.data),
  update: (id: number, payload: Partial<Omit<TaskCreatePayload, "projectId" | "assignedEmployeeId">>) =>
    apiClient.put<Task>(`/api/tasks/${id}`, payload).then((r) => r.data),
  remove: (id: number) => apiClient.delete<void>(`/api/tasks/${id}`).then((r) => r.data),
  byProject: (projectId: number, p?: PageParams) =>
    apiClient
      .get<Page<Task>>(`/api/tasks/project/${projectId}`, qp(p))
      .then((r) => r.data),
  byEmployee: (employeeId: number, p?: PageParams) =>
    apiClient
      .get<Page<Task>>(`/api/tasks/employee/${employeeId}`, qp(p))
      .then((r) => r.data),
  byStatus: (status: TaskStatus, p?: PageParams) =>
    apiClient.get<Page<Task>>(`/api/tasks/status/${status}`, qp(p)).then((r) => r.data),
};
