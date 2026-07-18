import { createFileRoute, Link } from "@tanstack/react-router";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";
import { ArrowLeft, UserPlus, Trash2 } from "lucide-react";

import { PageHeader } from "@/components/common/page-header";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { StatusBadge } from "@/components/common/status-badge";
import { LoadingBlock, EmptyState, ErrorState } from "@/components/common/states";
import { RoleGate, useHasRole } from "@/components/common/role-gate";
import { Field } from "@/routes/_app.employees";
import { projectsApi } from "@/lib/api/projects";
import { employeesApi } from "@/lib/api/employees";
import { tasksApi } from "@/lib/api/tasks";
import { apiErrorMessage } from "@/lib/api/client";
import { fmtDate } from "@/lib/format";
import type { ProjectRole } from "@/lib/api/types";

const projectRoles: ProjectRole[] = [
  "PROJECT_MANAGER",
  "BACKEND_DEVELOPER",
  "FRONTEND_DEVELOPER",
  "QA_ENGINEER",
];

const addMemberSchema = z.object({
  employeeId: z.string().min(1, "Required"),
  projectRole: z.enum([
    "PROJECT_MANAGER",
    "BACKEND_DEVELOPER",
    "FRONTEND_DEVELOPER",
    "QA_ENGINEER",
  ]),
});
type AddMemberValues = z.infer<typeof addMemberSchema>;

export const Route = createFileRoute("/_app/projects/$projectId")({
  head: () => ({ meta: [{ title: "Project — Operion" }] }),
  component: ProjectDetailPage,
});

function ProjectDetailPage() {
  const { projectId } = Route.useParams();
  const id = Number(projectId);
  const canManage = useHasRole("ADMIN", "HR", "MANAGER");
  const [addOpen, setAddOpen] = useState(false);

  const project = useQuery({
    queryKey: ["projects", id],
    queryFn: () => projectsApi.get(id),
  });

  const members = useQuery({
    queryKey: ["projects", id, "members"],
    queryFn: () => projectsApi.members(id, { size: 100 }),
  });

  const tasks = useQuery({
    queryKey: ["tasks", "project", id],
    queryFn: () => tasksApi.byProject(id, { size: 100, sort: "createdAt,desc" }),
  });

  if (project.isLoading) return <LoadingBlock />;
  if (project.isError) return <ErrorState message={apiErrorMessage(project.error)} />;
  if (!project.data) return null;

  return (
    <div className="space-y-6">
      <Link
        to="/projects"
        className="inline-flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground"
      >
        <ArrowLeft className="h-4 w-4" /> All projects
      </Link>

      <PageHeader
        title={project.data.name}
        description={project.data.description ?? "No description"}
        actions={<StatusBadge value={project.data.status} />}
      />

      <div className="grid gap-4 sm:grid-cols-3">
        <InfoCard label="Start date" value={fmtDate(project.data.startDate)} />
        <InfoCard
          label="End date"
          value={project.data.endDate ? fmtDate(project.data.endDate) : "Ongoing"}
        />
        <InfoCard label="Members" value={String(members.data?.totalElements ?? 0)} />
      </div>

      <section className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Team members</h2>
          <RoleGate roles={["ADMIN", "HR", "MANAGER"]}>
            <Button size="sm" onClick={() => setAddOpen(true)}>
              <UserPlus className="h-4 w-4" /> Add member
            </Button>
          </RoleGate>
        </div>
        {members.isLoading ? (
          <LoadingBlock />
        ) : (members.data?.content.length ?? 0) === 0 ? (
          <EmptyState title="No members yet" />
        ) : (
          <Card className="overflow-hidden border-border/60 p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Employee</TableHead>
                  <TableHead>Role</TableHead>
                  <TableHead>Assigned</TableHead>
                  {canManage ? <TableHead className="w-16" /> : null}
                </TableRow>
              </TableHeader>
              <TableBody>
                {members.data!.content.map((m) => (
                  <MemberRow key={m.id} member={m} projectId={id} canManage={canManage} />
                ))}
              </TableBody>
            </Table>
          </Card>
        )}
      </section>

      <section className="space-y-3">
        <h2 className="text-lg font-semibold">Tasks</h2>
        {tasks.isLoading ? (
          <LoadingBlock />
        ) : (tasks.data?.content.length ?? 0) === 0 ? (
          <EmptyState title="No tasks in this project" />
        ) : (
          <Card className="overflow-hidden border-border/60 p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Task</TableHead>
                  <TableHead>Assignee</TableHead>
                  <TableHead>Priority</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {tasks.data!.content.map((t) => (
                  <TableRow key={t.id}>
                    <TableCell className="font-medium">{t.title}</TableCell>
                    <TableCell>{t.assignedEmployeeName}</TableCell>
                    <TableCell>
                      <StatusBadge value={t.priority} />
                    </TableCell>
                    <TableCell>
                      <StatusBadge value={t.status} />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Card>
        )}
      </section>

      <AddMemberDialog open={addOpen} onOpenChange={setAddOpen} projectId={id} />
    </div>
  );
}

function InfoCard({ label, value }: { label: string; value: string }) {
  return (
    <Card className="border-border/60">
      <CardContent className="p-4">
        <div className="text-xs uppercase tracking-wide text-muted-foreground">{label}</div>
        <div className="mt-1 text-lg font-semibold">{value}</div>
      </CardContent>
    </Card>
  );
}

function MemberRow({
  member,
  projectId,
  canManage,
}: {
  member: import("@/lib/api/types").ProjectMember;
  projectId: number;
  canManage: boolean;
}) {
  const qc = useQueryClient();
  const remove = useMutation({
    mutationFn: () => projectsApi.removeMember(projectId, member.id),
    onSuccess: () => {
      toast.success("Member removed");
      qc.invalidateQueries({ queryKey: ["projects", projectId, "members"] });
    },
    onError: (e) => toast.error(apiErrorMessage(e)),
  });

  return (
    <TableRow>
      <TableCell className="font-medium">{member.employeeName}</TableCell>
      <TableCell>
        <StatusBadge value={member.projectRole} />
      </TableCell>
      <TableCell>{fmtDate(member.assignedDate)}</TableCell>
      {canManage ? (
        <TableCell>
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8 text-destructive"
            onClick={() => remove.mutate()}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </TableCell>
      ) : null}
    </TableRow>
  );
}

function AddMemberDialog({
  open,
  onOpenChange,
  projectId,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  projectId: number;
}) {
  const qc = useQueryClient();
  const employees = useQuery({
    queryKey: ["employees", "all"],
    queryFn: () => employeesApi.list({ size: 200, sort: "firstName,asc" }),
    staleTime: 60_000,
  });

  const form = useForm<AddMemberValues>({
    resolver: zodResolver(addMemberSchema),
    defaultValues: { employeeId: "", projectRole: "BACKEND_DEVELOPER" },
  });

  const mutation = useMutation({
    mutationFn: (v: AddMemberValues) =>
      projectsApi.addMember(projectId, {
        employeeId: Number(v.employeeId),
        projectRole: v.projectRole,
      }),
    onSuccess: () => {
      toast.success("Member added");
      qc.invalidateQueries({ queryKey: ["projects", projectId, "members"] });
      form.reset({ employeeId: "", projectRole: "BACKEND_DEVELOPER" });
      onOpenChange(false);
    },
    onError: (e) => toast.error(apiErrorMessage(e)),
  });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add member</DialogTitle>
        </DialogHeader>
        <form onSubmit={form.handleSubmit((v) => mutation.mutate(v))} className="space-y-4">
          <Field label="Employee" error={form.formState.errors.employeeId?.message}>
            <Select
              value={form.watch("employeeId")}
              onValueChange={(v) => form.setValue("employeeId", v)}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select employee" />
              </SelectTrigger>
              <SelectContent>
                {(employees.data?.content ?? []).map((e) => (
                  <SelectItem key={e.id} value={String(e.id)}>
                    {e.firstName} {e.lastName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Project role">
            <Select
              value={form.watch("projectRole")}
              onValueChange={(v) => form.setValue("projectRole", v as ProjectRole)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {projectRoles.map((r) => (
                  <SelectItem key={r} value={r}>
                    {r.replace(/_/g, " ")}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={mutation.isPending}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? "Adding…" : "Add"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
