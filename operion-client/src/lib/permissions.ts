import type { Role } from "@/lib/api/types";
import { useAuthStore } from "@/stores/auth-store";

/**
 * Permission matrix mirroring the Operion API role requirements.
 * Each permission maps to the roles allowed to perform the action.
 */
export const PERMISSIONS = {
  // Employees
  "employees:read": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "employees:create": ["ADMIN", "HR"],
  "employees:update": ["ADMIN", "HR"],
  "employees:delete": ["ADMIN", "HR"],
  "employees:assignDepartment": ["ADMIN", "HR"],

  // Departments
  "departments:read": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "departments:create": ["ADMIN", "HR"],
  "departments:update": ["ADMIN", "HR"],
  "departments:delete": ["ADMIN"],
  "departments:readEmployees": ["ADMIN", "HR", "MANAGER"],

  // Attendance
  "attendance:clock": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "attendance:readSelf": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "attendance:readAll": ["ADMIN", "HR", "MANAGER"],
  "attendance:update": ["ADMIN", "HR"],

  // Leaves
  "leaves:create": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "leaves:readSelf": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "leaves:readAll": ["ADMIN", "HR", "MANAGER"],
  "leaves:approve": ["ADMIN", "HR", "MANAGER"],
  "leaves:updateOwn": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "leaves:deleteOwn": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],

  // Projects
  "projects:read": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "projects:create": ["ADMIN", "HR", "MANAGER"],
  "projects:update": ["ADMIN", "HR", "MANAGER"],
  "projects:delete": ["ADMIN", "HR"],
  "projects:readMembers": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "projects:manageMembers": ["ADMIN", "HR", "MANAGER"],

  // Tasks
  "tasks:readAll": ["ADMIN", "HR", "MANAGER"],
  "tasks:readByProject": ["ADMIN", "HR", "MANAGER", "EMPLOYEE"],
  "tasks:readByEmployee": ["ADMIN", "HR", "MANAGER"],
  "tasks:create": ["ADMIN", "HR", "MANAGER"],
  "tasks:update": ["ADMIN", "HR", "MANAGER"],
  "tasks:delete": ["ADMIN", "HR", "MANAGER"],
} as const satisfies Record<string, Role[]>;

export type Permission = keyof typeof PERMISSIONS;

export function roleCan(role: Role | undefined | null, permission: Permission): boolean {
  if (!role) return false;
  return (PERMISSIONS[permission] as readonly Role[]).includes(role);
}

export function useCan(permission: Permission): boolean {
  const role = useAuthStore((s) => s.user?.role);
  return roleCan(role, permission);
}

export function useCanAny(...perms: Permission[]): boolean {
  const role = useAuthStore((s) => s.user?.role);
  return perms.some((p) => roleCan(role, p));
}

export const ROLE_LABEL: Record<Role, string> = {
  ADMIN: "Administrator",
  HR: "Human Resources",
  MANAGER: "Manager",
  EMPLOYEE: "Employee",
};

export const ROLE_ACCENT: Record<Role, string> = {
  ADMIN: "from-[oklch(0.62_0.22_25)] to-[oklch(0.72_0.19_50)]",
  HR: "from-[oklch(0.72_0.19_50)] to-[oklch(0.82_0.16_80)]",
  MANAGER: "from-[oklch(0.55_0.16_300)] to-[oklch(0.62_0.22_25)]",
  EMPLOYEE: "from-[oklch(0.66_0.14_145)] to-[oklch(0.72_0.19_50)]",
};
