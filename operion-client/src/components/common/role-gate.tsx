import { useAuthStore } from "@/stores/auth-store";
import type { Role } from "@/lib/api/types";
import type { ReactNode } from "react";

export function RoleGate({
  roles,
  children,
  fallback = null,
}: {
  roles: Role[];
  children: ReactNode;
  fallback?: ReactNode;
}) {
  const user = useAuthStore((s) => s.user);
  if (!user || !roles.includes(user.role)) return <>{fallback}</>;
  return <>{children}</>;
}

export function useHasRole(...roles: Role[]) {
  return useAuthStore((s) => (s.user ? roles.includes(s.user.role) : false));
}
