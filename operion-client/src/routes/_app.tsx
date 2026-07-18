import { createFileRoute, Outlet, useNavigate, useRouterState } from "@tanstack/react-router";
import { useEffect } from "react";
import { AppSidebar } from "@/components/layout/app-sidebar";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { Separator } from "@/components/ui/separator";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/auth-store";
import { LogOut, ChevronRight } from "lucide-react";
import { ROLE_LABEL } from "@/lib/permissions";

export const Route = createFileRoute("/_app")({
  component: AppLayout,
});

function AppLayout() {
  const navigate = useNavigate();
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const token = useAuthStore((s) => s.token);
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);

  useEffect(() => {
    if (!token) void navigate({ to: "/login", replace: true });
  }, [token, navigate]);

  if (!token) return null;

  const segments = pathname.split("/").filter(Boolean);
  const crumb = segments[0] ?? "dashboard";

  return (
    <SidebarProvider>
      <div className="flex min-h-screen w-full bg-background">
        <AppSidebar />
        <div className="flex min-h-screen flex-1 flex-col">
          <header className="sticky top-0 z-30 flex h-14 items-center gap-2 border-b border-border/60 bg-background/85 px-3 backdrop-blur-md">
            <SidebarTrigger />
            <Separator orientation="vertical" className="mx-1 h-5" />
            <div className="flex items-center gap-1.5 text-sm">
              <span className="text-muted-foreground">Operion</span>
              <ChevronRight className="h-3.5 w-3.5 text-muted-foreground/60" />
              <span className="font-semibold capitalize">{crumb.replace(/-/g, " ")}</span>
            </div>
            <div className="ml-auto flex items-center gap-3 pr-1">
              {user ? (
                <div className="hidden items-center gap-2 rounded-full border border-border/60 bg-card/70 px-3 py-1 md:flex">
                  <span
                    className="inline-block h-2 w-2 rounded-full bg-sunset-gradient"
                    aria-hidden
                  />
                  <span className="text-xs font-medium text-foreground">
                    {user.firstName} {user.lastName}
                  </span>
                  <span className="text-[10px] font-semibold uppercase tracking-wider text-primary">
                    · {ROLE_LABEL[user.role]}
                  </span>
                </div>
              ) : null}
              <Button
                variant="ghost"
                size="sm"
                onClick={() => {
                  logout();
                  void navigate({ to: "/login", replace: true });
                }}
              >
                <LogOut className="h-4 w-4" />
                <span className="hidden sm:inline">Sign out</span>
              </Button>
            </div>
          </header>
          <main className="flex-1 px-4 py-6 sm:px-6 lg:px-8">
            <div className="mx-auto w-full max-w-7xl space-y-6">
              <Outlet />
            </div>
          </main>
        </div>
      </div>
    </SidebarProvider>
  );
}
