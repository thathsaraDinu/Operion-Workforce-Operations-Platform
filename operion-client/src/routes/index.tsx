import { createFileRoute, redirect } from "@tanstack/react-router";
import { useAuthStore } from "@/stores/auth-store";

export const Route = createFileRoute("/")({
  beforeLoad: () => {
    const token = useAuthStore.getState().token;
    throw redirect({ to: token ? "/dashboard" : "/login" });
  },
});
