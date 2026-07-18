import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  Users,
  Building2,
  FolderKanban,
  ListChecks,
  CalendarOff,
  CalendarClock,
  TrendingUp,
  Sparkles,
  ArrowRight,
  Clock,
  ShieldCheck,
} from "lucide-react";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { StatusBadge } from "@/components/common/status-badge";
import { Button } from "@/components/ui/button";
import { employeesApi } from "@/lib/api/employees";
import { departmentsApi } from "@/lib/api/departments";
import { projectsApi } from "@/lib/api/projects";
import { tasksApi } from "@/lib/api/tasks";
import { leavesApi } from "@/lib/api/leaves";
import { attendanceApi } from "@/lib/api/attendance";
import { useAuthStore } from "@/stores/auth-store";
import { fmtDate } from "@/lib/format";
import { ROLE_LABEL, useCan } from "@/lib/permissions";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/_app/dashboard")({
  head: () => ({ meta: [{ title: "Dashboard — Operion" }] }),
  component: DashboardPage,
});

type Tone = "ember" | "sunset" | "amber" | "plum" | "success";

const toneStyles: Record<Tone, { bg: string; ring: string; icon: string }> = {
  ember: {
    bg: "bg-gradient-to-br from-[oklch(0.62_0.22_25)] to-[oklch(0.55_0.24_20)]",
    ring: "ring-[oklch(0.62_0.22_25)]/25",
    icon: "bg-white/20 text-white",
  },
  sunset: {
    bg: "bg-gradient-to-br from-[oklch(0.72_0.19_50)] to-[oklch(0.66_0.22_38)]",
    ring: "ring-[oklch(0.72_0.19_50)]/25",
    icon: "bg-white/20 text-white",
  },
  amber: {
    bg: "bg-gradient-to-br from-[oklch(0.82_0.16_80)] to-[oklch(0.74_0.18_65)]",
    ring: "ring-[oklch(0.82_0.16_80)]/30",
    icon: "bg-white/25 text-white",
  },
  plum: {
    bg: "bg-gradient-to-br from-[oklch(0.45_0.14_320)] to-[oklch(0.55_0.16_300)]",
    ring: "ring-[oklch(0.55_0.16_300)]/25",
    icon: "bg-white/20 text-white",
  },
  success: {
    bg: "bg-gradient-to-br from-[oklch(0.55_0.14_150)] to-[oklch(0.66_0.14_145)]",
    ring: "ring-[oklch(0.66_0.14_145)]/25",
    icon: "bg-white/20 text-white",
  },
};

function StatCard({
  label,
  value,
  icon: Icon,
  hint,
  tone,
  loading,
}: {
  label: string;
  value: string | number;
  icon: React.ComponentType<{ className?: string }>;
  hint?: string;
  tone: Tone;
  loading?: boolean;
}) {
  const s = toneStyles[tone];
  return (
    <div
      className={cn(
        "relative overflow-hidden rounded-2xl p-5 text-white shadow-lg ring-1",
        s.bg,
        s.ring,
      )}
    >
      <div className="pointer-events-none absolute -right-6 -top-6 h-24 w-24 rounded-full bg-white/10 blur-2xl" />
      <div className="pointer-events-none absolute -bottom-8 -left-4 h-24 w-24 rounded-full bg-black/10 blur-2xl" />
      <div className="relative flex items-start justify-between">
        <div>
          <div className="text-[11px] font-semibold uppercase tracking-widest text-white/80">
            {label}
          </div>
          <div className="mt-2 text-3xl font-bold tracking-tight">
            {loading ? "…" : value}
          </div>
          {hint ? <div className="mt-1 text-xs text-white/70">{hint}</div> : null}
        </div>
        <div className={cn("flex h-11 w-11 items-center justify-center rounded-xl", s.icon)}>
          <Icon className="h-5 w-5" />
        </div>
      </div>
    </div>
  );
}

function DashboardPage() {
  const user = useAuthStore((s) => s.user);
  const canReadEmployees = useCan("employees:read");
  const canReadAllLeaves = useCan("leaves:readAll");
  const canReadAllTasks = useCan("tasks:readAll");

  const employees = useQuery({
    queryKey: ["employees", { size: 1 }],
    queryFn: () => employeesApi.list({ size: 1 }),
    enabled: canReadEmployees,
  });
  const departments = useQuery({
    queryKey: ["departments", { size: 1 }],
    queryFn: () => departmentsApi.list({ size: 1 }),
  });
  const projects = useQuery({
    queryKey: ["projects", { size: 1 }],
    queryFn: () => projectsApi.list({ size: 1 }),
  });
  const tasks = useQuery({
    queryKey: ["tasks", { size: 1 }],
    queryFn: () => tasksApi.list({ size: 1 }),
    enabled: canReadAllTasks,
  });
  const pendingLeaves = useQuery({
    queryKey: ["leaves", "PENDING", { size: 5 }],
    queryFn: () => leavesApi.byStatus("PENDING", { size: 5 }),
    enabled: canReadAllLeaves,
  });
  const myAttendance = useQuery({
    queryKey: ["attendance", "me", { size: 5 }],
    queryFn: () => attendanceApi.mine({ size: 5, sort: "date,desc" }),
  });
  const myLeaves = useQuery({
    queryKey: ["leaves", "me", { size: 5 }],
    queryFn: () => leavesApi.mine({ size: 5, sort: "createdAt,desc" }),
  });

  return (
    <div className="space-y-8">
      {/* Hero header */}
      <section className="relative overflow-hidden rounded-3xl border border-border/50 bg-card p-6 sm:p-8">
        <div className="pointer-events-none absolute inset-0 bg-ember-radial opacity-40" aria-hidden />
        <div className="relative flex flex-wrap items-end justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 rounded-full border border-primary/25 bg-primary/10 px-3 py-1 text-xs font-semibold text-primary">
              <Sparkles className="h-3.5 w-3.5" />
              {user ? ROLE_LABEL[user.role] : "Signed in"}
            </div>
            <h1
              className="text-3xl font-semibold tracking-tight sm:text-4xl"
              style={{ fontFamily: '"Fraunces", ui-serif, Georgia, serif' }}
            >
              Welcome back,{" "}
              <span className="text-sunset-gradient">{user?.firstName ?? "there"}</span>
            </h1>
            <p className="max-w-lg text-sm text-muted-foreground">
              Here's the pulse of your organization today — attendance, requests, projects
              and tasks.
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <Button asChild variant="outline" size="sm" className="border-primary/30">
              <Link to="/attendance">
                <Clock className="h-4 w-4" /> Attendance
              </Link>
            </Button>
            <Button asChild size="sm" className="bg-sunset-gradient text-white shadow-ember">
              <Link to="/leaves">
                <CalendarOff className="h-4 w-4" /> Request leave
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Stat grid */}
      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {canReadEmployees ? (
          <StatCard
            label="Employees"
            value={employees.data?.totalElements ?? "—"}
            icon={Users}
            tone="ember"
            hint="Total workforce"
            loading={employees.isLoading}
          />
        ) : null}
        <StatCard
          label="Departments"
          value={departments.data?.totalElements ?? "—"}
          icon={Building2}
          tone="sunset"
          hint="Organizational units"
          loading={departments.isLoading}
        />
        <StatCard
          label="Projects"
          value={projects.data?.totalElements ?? "—"}
          icon={FolderKanban}
          tone="amber"
          hint="Active initiatives"
          loading={projects.isLoading}
        />
        {canReadAllTasks ? (
          <StatCard
            label="Tasks"
            value={tasks.data?.totalElements ?? "—"}
            icon={ListChecks}
            tone="plum"
            hint="Across all projects"
            loading={tasks.isLoading}
          />
        ) : (
          <StatCard
            label="My leaves"
            value={myLeaves.data?.totalElements ?? "—"}
            icon={CalendarOff}
            tone="plum"
            hint="Submitted this year"
            loading={myLeaves.isLoading}
          />
        )}
      </section>

      {/* Two-column detail */}
      <section className="grid gap-4 lg:grid-cols-2">
        <Card className="border-border/60">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="flex items-center gap-2 text-base">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/10 text-primary">
                <CalendarClock className="h-4 w-4" />
              </div>
              Recent attendance
            </CardTitle>
            <Link
              to="/attendance"
              className="inline-flex items-center gap-1 text-xs font-medium text-primary hover:underline"
            >
              View all <ArrowRight className="h-3 w-3" />
            </Link>
          </CardHeader>
          <CardContent className="space-y-2.5">
            {(myAttendance.data?.content ?? []).length === 0 ? (
              <p className="rounded-lg border border-dashed border-border/60 p-6 text-center text-sm text-muted-foreground">
                No attendance records yet. Clock in from the Attendance page.
              </p>
            ) : (
              myAttendance.data!.content.map((a) => (
                <div
                  key={a.id}
                  className="group flex items-center justify-between rounded-xl border border-border/60 bg-card px-3.5 py-3 transition hover:border-primary/40 hover:shadow-sm"
                >
                  <div className="flex items-center gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-accent text-accent-foreground">
                      <CalendarClock className="h-4 w-4" />
                    </div>
                    <div className="text-sm">
                      <div className="font-semibold">{fmtDate(a.date)}</div>
                      <div className="text-xs text-muted-foreground">
                        In {a.clockIn ? fmtDate(a.clockIn, "h:mm a") : "—"} · Out{" "}
                        {a.clockOut ? fmtDate(a.clockOut, "h:mm a") : "—"}
                      </div>
                    </div>
                  </div>
                  <StatusBadge value={a.status} />
                </div>
              ))
            )}
          </CardContent>
        </Card>

        <Card className="border-border/60">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="flex items-center gap-2 text-base">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/10 text-primary">
                <CalendarOff className="h-4 w-4" />
              </div>
              {canReadAllLeaves ? "Pending leave approvals" : "My recent leave requests"}
            </CardTitle>
            <Link
              to="/leaves"
              className="inline-flex items-center gap-1 text-xs font-medium text-primary hover:underline"
            >
              Manage <ArrowRight className="h-3 w-3" />
            </Link>
          </CardHeader>
          <CardContent className="space-y-2.5">
            {(() => {
              const rows =
                (canReadAllLeaves ? pendingLeaves.data?.content : myLeaves.data?.content) ?? [];
              if (rows.length === 0)
                return (
                  <p className="rounded-lg border border-dashed border-border/60 p-6 text-center text-sm text-muted-foreground">
                    Nothing to show.
                  </p>
                );
              return rows.map((l) => (
                <div
                  key={l.id}
                  className="flex items-center justify-between rounded-xl border border-border/60 bg-card px-3.5 py-3 transition hover:border-primary/40 hover:shadow-sm"
                >
                  <div className="flex items-center gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-sunset-gradient text-white">
                      <CalendarOff className="h-4 w-4" />
                    </div>
                    <div className="text-sm">
                      <div className="font-semibold">{l.employeeName}</div>
                      <div className="text-xs text-muted-foreground">
                        {l.leaveType} · {fmtDate(l.startDate)} → {fmtDate(l.endDate)}
                      </div>
                    </div>
                  </div>
                  <StatusBadge value={l.status} />
                </div>
              ));
            })()}
          </CardContent>
        </Card>
      </section>

      {/* Role callout */}
      {user ? (
        <section className="grid gap-4 md:grid-cols-3">
          <div className="md:col-span-2 rounded-2xl border border-border/60 bg-card p-6">
            <div className="flex items-start gap-4">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-sunset-gradient text-white shadow-ember">
                <ShieldCheck className="h-5 w-5" />
              </div>
              <div>
                <div className="text-sm font-semibold">
                  Signed in as {ROLE_LABEL[user.role]}
                </div>
                <p className="mt-1 text-sm text-muted-foreground">
                  Your role controls what you can see and change. Actions you're not
                  allowed to perform are hidden automatically.
                </p>
              </div>
            </div>
          </div>
          <div className="rounded-2xl border border-border/60 bg-card p-6">
            <div className="flex items-center gap-2 text-sm font-semibold">
              <TrendingUp className="h-4 w-4 text-primary" /> Quick jump
            </div>
            <div className="mt-3 grid grid-cols-2 gap-2">
              <Link
                to="/projects"
                className="rounded-lg border border-border/60 px-3 py-2 text-xs font-medium hover:border-primary/40 hover:bg-accent"
              >
                Projects
              </Link>
              <Link
                to="/tasks"
                className="rounded-lg border border-border/60 px-3 py-2 text-xs font-medium hover:border-primary/40 hover:bg-accent"
              >
                Tasks
              </Link>
              <Link
                to="/departments"
                className="rounded-lg border border-border/60 px-3 py-2 text-xs font-medium hover:border-primary/40 hover:bg-accent"
              >
                Departments
              </Link>
              <Link
                to="/profile"
                className="rounded-lg border border-border/60 px-3 py-2 text-xs font-medium hover:border-primary/40 hover:bg-accent"
              >
                My profile
              </Link>
            </div>
          </div>
        </section>
      ) : null}
    </div>
  );
}
