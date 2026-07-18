import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { PageHeader } from "@/components/common/page-header";
import { Card, CardContent } from "@/components/ui/card";
import { StatusBadge } from "@/components/common/status-badge";
import { useAuthStore } from "@/stores/auth-store";
import { attendanceApi } from "@/lib/api/attendance";
import { leavesApi } from "@/lib/api/leaves";
import { initials, fmtDate, fmtTime } from "@/lib/format";

export const Route = createFileRoute("/_app/profile")({
  head: () => ({ meta: [{ title: "Profile — Operion" }] }),
  component: ProfilePage,
});

function ProfilePage() {
  const user = useAuthStore((s) => s.user);
  const attendance = useQuery({
    queryKey: ["attendance", "me", "profile"],
    queryFn: () => attendanceApi.mine({ size: 5, sort: "date,desc" }),
  });
  const leaves = useQuery({
    queryKey: ["leaves", "me", "profile"],
    queryFn: () => leavesApi.mine({ size: 5, sort: "createdAt,desc" }),
  });

  if (!user) return null;

  return (
    <div className="space-y-6">
      <PageHeader title="My profile" description="Your account details and recent activity." />

      <Card className="border-border/60">
        <CardContent className="flex items-center gap-4 p-6">
          <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary text-lg font-semibold text-primary-foreground">
            {initials(user.firstName, user.lastName)}
          </div>
          <div className="min-w-0 flex-1">
            <div className="text-xl font-semibold">
              {user.firstName} {user.lastName}
            </div>
            <div className="text-sm text-muted-foreground">{user.email}</div>
          </div>
          <StatusBadge value={user.role} />
        </CardContent>
      </Card>

      <div className="grid gap-4 lg:grid-cols-2">
        <Card className="border-border/60">
          <CardContent className="p-5">
            <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
              Recent attendance
            </h3>
            <div className="space-y-2">
              {(attendance.data?.content ?? []).length === 0 ? (
                <p className="text-sm text-muted-foreground">No records.</p>
              ) : (
                attendance.data!.content.map((a) => (
                  <div key={a.id} className="flex items-center justify-between text-sm">
                    <div>
                      <div className="font-medium">{fmtDate(a.date)}</div>
                      <div className="text-xs text-muted-foreground">
                        {a.clockIn ? fmtTime(a.clockIn) : "—"} ·{" "}
                        {a.clockOut ? fmtTime(a.clockOut) : "—"}
                      </div>
                    </div>
                    <StatusBadge value={a.status} />
                  </div>
                ))
              )}
            </div>
          </CardContent>
        </Card>

        <Card className="border-border/60">
          <CardContent className="p-5">
            <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
              Recent leave
            </h3>
            <div className="space-y-2">
              {(leaves.data?.content ?? []).length === 0 ? (
                <p className="text-sm text-muted-foreground">No records.</p>
              ) : (
                leaves.data!.content.map((l) => (
                  <div key={l.id} className="flex items-center justify-between text-sm">
                    <div>
                      <div className="font-medium">{l.leaveType}</div>
                      <div className="text-xs text-muted-foreground">
                        {fmtDate(l.startDate)} → {fmtDate(l.endDate)}
                      </div>
                    </div>
                    <StatusBadge value={l.status} />
                  </div>
                ))
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
