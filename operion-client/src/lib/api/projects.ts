import { apiClient } from "./client";
import type {
  Page,
  PageParams,
  Project,
  ProjectMember,
  ProjectRole,
  ProjectStatus,
} from "./types";

function qp(p?: PageParams) {
  return { params: { page: p?.page ?? 0, size: p?.size ?? 10, sort: p?.sort } };
}

export interface ProjectCreatePayload {
  name: string;
  startDate: string;
  status: ProjectStatus;
  description?: string;
  endDate?: string;
}

export const projectsApi = {
  create: (payload: ProjectCreatePayload) =>
    apiClient.post<Project>("/api/projects", payload).then((r) => r.data),
  list: (p?: PageParams) =>
    apiClient.get<Page<Project>>("/api/projects", qp(p)).then((r) => r.data),
  get: (id: number) => apiClient.get<Project>(`/api/projects/${id}`).then((r) => r.data),
  update: (id: number, payload: Partial<ProjectCreatePayload>) =>
    apiClient.put<Project>(`/api/projects/${id}`, payload).then((r) => r.data),
  remove: (id: number) => apiClient.delete<void>(`/api/projects/${id}`).then((r) => r.data),
  members: (projectId: number, p?: PageParams) =>
    apiClient
      .get<Page<ProjectMember>>(`/api/projects/${projectId}/members`, qp(p))
      .then((r) => r.data),
  addMember: (projectId: number, payload: { employeeId: number; projectRole: ProjectRole }) =>
    apiClient
      .post<ProjectMember>(`/api/projects/${projectId}/members`, payload)
      .then((r) => r.data),
  removeMember: (projectId: number, memberId: number) =>
    apiClient
      .delete<void>(`/api/projects/${projectId}/members/${memberId}`)
      .then((r) => r.data),
};
