import { Link, useRouterState } from "@tanstack/react-router";
import {
  LayoutDashboard,
  Users,
  Building2,
  CalendarClock,
  CalendarOff,
  FolderKanban,
  ListChecks,
  UserCircle,
  LogOut,
  Flame,
} from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/auth-store";
import { initials } from "@/lib/format";
import { PERMISSIONS, type Permission, ROLE_LABEL } from "@/lib/permissions";

interface NavItem {
  title: string;
  url: string;
  icon: React.ComponentType<{ className?: string }>;
  permission: Permission;
}

const main: NavItem[] = [
  { title: "Dashboard", url: "/dashboard", icon: LayoutDashboard, permission: "departments:read" },
  { title: "Employees", url: "/employees", icon: Users, permission: "employees:read" },
  { title: "Departments", url: "/departments", icon: Building2, permission: "departments:read" },
  { title: "Attendance", url: "/attendance", icon: CalendarClock, permission: "attendance:readSelf" },
  { title: "Leave", url: "/leaves", icon: CalendarOff, permission: "leaves:readSelf" },
  { title: "Projects", url: "/projects", icon: FolderKanban, permission: "projects:read" },
  { title: "Tasks", url: "/tasks", icon: ListChecks, permission: "tasks:readByProject" },
];

export function AppSidebar() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);

  const isActive = (url: string) =>
    url === "/dashboard" ? pathname === url : pathname === url || pathname.startsWith(url + "/");

  const visible = main.filter(
    (i) => !user || (PERMISSIONS[i.permission] as readonly string[]).includes(user.role),
  );

  return (
    <Sidebar collapsible="icon">
      <SidebarHeader className="border-b border-sidebar-border/60">
        <div className="flex items-center gap-2.5 px-2 py-2.5">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-sunset-gradient text-white shadow-ember">
            <Flame className="h-4 w-4" strokeWidth={2.4} />
          </div>
          <div className="flex flex-col leading-tight group-data-[collapsible=icon]:hidden">
            <span className="text-sm font-bold tracking-tight text-sidebar-foreground">
              Operion
            </span>
            <span className="text-[10px] font-medium uppercase tracking-wider text-muted-foreground">
              Workforce
            </span>
          </div>
        </div>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Workspace</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {visible.map((item) => {
                const active = isActive(item.url);
                return (
                  <SidebarMenuItem key={item.url}>
                    <SidebarMenuButton
                      asChild
                      isActive={active}
                      tooltip={item.title}
                      className={
                        active
                          ? "bg-sunset-gradient text-white shadow-ember hover:brightness-110 data-[active=true]:bg-sunset-gradient data-[active=true]:text-white"
                          : ""
                      }
                    >
                      <Link to={item.url}>
                        <item.icon className="h-4 w-4" />
                        <span>{item.title}</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup>
          <SidebarGroupLabel>Personal</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              <SidebarMenuItem>
                <SidebarMenuButton asChild isActive={isActive("/profile")} tooltip="Profile">
                  <Link to="/profile">
                    <UserCircle className="h-4 w-4" />
                    <span>Profile</span>
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter className="border-t border-sidebar-border/60">
        <div className="flex items-center gap-3 px-2 py-2">
          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-sunset-gradient text-xs font-bold text-white shadow-ember">
            {initials(user?.firstName, user?.lastName)}
          </div>
          <div className="min-w-0 flex-1 leading-tight group-data-[collapsible=icon]:hidden">
            <div className="truncate text-sm font-semibold text-sidebar-foreground">
              {user?.firstName} {user?.lastName}
            </div>
            <div className="truncate text-[10px] font-medium uppercase tracking-wide text-primary">
              {user ? ROLE_LABEL[user.role] : ""}
            </div>
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8 shrink-0 group-data-[collapsible=icon]:hidden"
            onClick={() => {
              logout();
              window.location.href = "/login";
            }}
            aria-label="Sign out"
          >
            <LogOut className="h-4 w-4" />
          </Button>
        </div>
      </SidebarFooter>
    </Sidebar>
  );
}
