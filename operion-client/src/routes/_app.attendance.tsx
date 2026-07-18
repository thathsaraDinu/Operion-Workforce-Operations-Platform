import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { LogIn, LogOut } from "lucide-react";

import { PageHeader } from "@/components/common/page-header";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "@/components/ui/tabs";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { StatusBadge } from "@/components/common/status-badge";
import { PaginationBar } from "@/components/common/pagination-bar";
import { LoadingBlock, EmptyState, ErrorState } from "@/components/common/states";
import { attendanceApi } from "@/lib/api/attendance";
import { apiErrorMessage } from "@/lib/api/client";
import { fmtDate, fmtTime } from "@/lib/format";
import { useHasRole } from "@/components/common/role-gate";

export const Route = createFileRoute("/_app/attendance")({
  head: () => ({ meta: [{ title: "Attendance — Operion" }] }),
  component: AttendancePage,
});

function AttendancePage() {
  const canSeeAll = useHasRole("ADMIN", "HR", "MANAGER");

  return (
    <div className="space-y-6">
      <PageHeader
        title="Attendance"
        description="Clock in and out, and review attendance records."
      />

      <ClockCard />

      {canSeeAll ? (
        <Tabs defaultValue="me">
          <TabsList>
            <TabsTrigger value="me">My records</TabsTrigger>
            <TabsTrigger value="all">All records</TabsTrigger>
          </TabsList>
          <TabsContent value="me" className="mt-4">
            <AttendanceTable mode="me" />
          </TabsContent>
          <TabsContent value="all" className="mt-4">
            <AttendanceTable mode="all" />
          </TabsContent>
        </Tabs>
      ) : (
        <AttendanceTable mode="me" />
      )}
    </div>
  );
}

function ClockCard() {
  const qc = useQueryClient();
  const inMut = useMutation({
    mutationFn: attendanceApi.clockIn,
    onSuccess: () => {
      toast.success("Clocked in");
      qc.invalidateQueries({ queryKey: ["attendance"] });
    },
    onError: (e) => toast.error(apiErrorMessage(e, "Clock-in failed")),
  });
  const outMut = useMutation({
    mutationFn: attendanceApi.clockOut,
    onSuccess: () => {
      toast.success("Clocked out");
      qc.invalidateQueries({ queryKey: ["attendance"] });
    },
    onError: (e) => toast.error(apiErrorMessage(e, "Clock-out failed")),
  });

  const today = useQuery({
    queryKey: ["attendance", "me", "today"],
    queryFn: () => attendanceApi.mine({ size: 1, sort: "date,desc" }),
  });
  const latest = today.data?.content[0];
  const isToday = latest && latest.date === new Date().toISOString().slice(0, 10);
  const clockedIn = isToday && !!latest.clockIn && !latest.clockOut;

  return (
    <Card className="border-border/60">
      <CardContent className="flex flex-col items-start justify-between gap-4 p-5 sm:flex-row sm:items-center">
        <div>
          <div className="text-xs uppercase tracking-wide text-muted-foreground">Today</div>
          <div className="text-lg font-semibold">
            {fmtDate(new Date().toISOString(), "EEEE, MMM d")}
          </div>
          <div className="mt-1 text-sm text-muted-foreground">
            {isToday
              ? `In ${fmtTime(latest?.clockIn)} · Out ${latest?.clockOut ? fmtTime(latest.clockOut) : "—"}`
              : "Not clocked in yet"}
          </div>
        </div>
        <div className="flex gap-2">
          <Button
            onClick={() => inMut.mutate()}
            disabled={inMut.isPending || clockedIn}
            variant={clockedIn ? "outline" : "default"}
          >
            <LogIn className="h-4 w-4" />
            Clock in
          </Button>
          <Button
            onClick={() => outMut.mutate()}
            disabled={outMut.isPending || !clockedIn}
            variant="outline"
          >
            <LogOut className="h-4 w-4" />
            Clock out
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

function AttendanceTable({ mode }: { mode: "me" | "all" }) {
  const [page, setPage] = useState(0);
  const query = useQuery({
    queryKey: ["attendance", mode, { page }],
    queryFn: () =>
      mode === "me"
        ? attendanceApi.mine({ page, size: 10, sort: "date,desc" })
        : attendanceApi.all({ page, size: 10, sort: "date,desc" }),
  });

  if (query.isLoading) return <LoadingBlock />;
  if (query.isError) return <ErrorState message={apiErrorMessage(query.error)} />;
  if ((query.data?.content.length ?? 0) === 0)
    return <EmptyState title="No attendance records" />;

  return (
    <Card className="overflow-hidden border-border/60 p-0">
      <Table>
        <TableHeader>
          <TableRow>
            {mode === "all" ? <TableHead>Employee</TableHead> : null}
            <TableHead>Date</TableHead>
            <TableHead>Clock in</TableHead>
            <TableHead>Clock out</TableHead>
            <TableHead>Status</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {query.data!.content.map((a) => (
            <TableRow key={a.id}>
              {mode === "all" ? (
                <TableCell className="font-medium">{a.employeeName}</TableCell>
              ) : null}
              <TableCell>{fmtDate(a.date)}</TableCell>
              <TableCell>{a.clockIn ? fmtTime(a.clockIn) : "—"}</TableCell>
              <TableCell>{a.clockOut ? fmtTime(a.clockOut) : "—"}</TableCell>
              <TableCell>
                <StatusBadge value={a.status} />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <PaginationBar
        page={page}
        totalPages={query.data!.totalPages}
        totalElements={query.data!.totalElements}
        onPageChange={setPage}
      />
    </Card>
  );
}
