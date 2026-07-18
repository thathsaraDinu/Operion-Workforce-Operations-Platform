import { createFileRoute } from "@tanstack/react-router";
import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";
import {
  Plus,
  MoreHorizontal,
  Pencil,
  Trash2,
  Flag,
  User2,
  FolderKanban,
  CircleDashed,
  Circle,
  CheckCircle2,
  ListChecks,
  Filter,
} from "lucide-react";

import { PageHeader } from "@/components/common/page-header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { LoadingBlock, EmptyState, ErrorState } from "@/components/common/states";
import { Field } from "@/routes/_app.employees";
import { tasksApi, type TaskCreatePayload } from "@/lib/api/tasks";
import { projectsApi } from "@/lib/api/projects";
import { employeesApi } from "@/lib/api/employees";
import { apiErrorMessage } from "@/lib/api/client";
import { useAuthStore } from "@/stores/auth-store";
import { useCan } from "@/lib/permissions";
import type { Task, TaskPriority, TaskStatus } from "@/lib/api/types";
import { cn } from "@/lib/utils";

const priorities: TaskPriority[] = ["LOW", "MEDIUM", "HIGH"];
const statuses: TaskStatus[] = ["TODO", "IN_PROGRESS", "DONE"];

const createSchema = z.object({
  title: z.string().trim().min(1),
  description: z.string().optional(),
  priority: z.enum(["LOW", "MEDIUM", "HIGH"]),
  status: z.enum(["TODO", "IN_PROGRESS", "DONE"]),
  projectId: z.string().min(1, "Required"),
  assignedEmployeeId: z.string().min(1, "Required"),
});
type CreateValues = z.infer<typeof createSchema>;

const editSchema = z.object({
  title: z.string().optional(),
  description: z.string().optional(),
  priority: z.enum(["LOW", "MEDIUM", "HIGH"]).optional(),
  status: z.enum(["TODO", "IN_PROGRESS", "DONE"]).optional(),
});
type EditValues = z.infer<typeof editSchema>;

const statusMeta: Record<
  TaskStatus,
  { label: string; icon: React.ComponentType<{ className?: string }>; color: string; dot: string }
> = {
  TODO: {
    label: "To do",
    icon: CircleDashed,
    color: "from-[oklch(0.72_0.05_60)] to-[oklch(0.62_0.05_60)]",
    dot: "bg-muted-foreground",
  },
  IN_PROGRESS: {
    label: "In progress",
    icon: Circle,
    color: "from-[oklch(0.72_0.19_50)] to-[oklch(0.62_0.22_25)]",
    dot: "bg-[oklch(0.72_0.19_50)]",
  },
  DONE: {
    label: "Done",
    icon: CheckCircle2,
    color: "from-[oklch(0.55_0.14_150)] to-[oklch(0.66_0.14_145)]",
    dot: "bg-[oklch(0.66_0.14_145)]",
  },
};

const priorityMeta: Record<TaskPriority, { color: string; label: string }> = {
  LOW: { color: "bg-muted text-muted-foreground border-border", label: "Low" },
  MEDIUM: {
    color:
      "bg-[oklch(0.96_0.06_75)] text-[oklch(0.4_0.16_60)] border-[oklch(0.82_0.16_80)]/40",
    label: "Medium",
  },
  HIGH: {
    color: "bg-[oklch(0.95_0.05_25)] text-[oklch(0.5_0.22_25)] border-[oklch(0.62_0.22_25)]/40",
    label: "High",
  },
};

export const Route = createFileRoute("/_app/tasks")({
  head: () => ({ meta: [{ title: "Tasks — Operion" }] }),
  component: TasksPage,
});

function TasksPage() {
  const user = useAuthStore((s) => s.user);
  const canReadAll = useCan("tasks:readAll");
  const canCreate = useCan("tasks:create");
  const canUpdate = useCan("tasks:update");
  const canDelete = useCan("tasks:delete");

  const [openCreate, setOpenCreate] = useState(false);
  const [editing, setEditing] = useState<Task | null>(null);
  const [deleting, setDeleting] = useState<Task | null>(null);
  const [projectFilter, setProjectFilter] = useState<string>("ALL");
  const [priorityFilter, setPriorityFilter] = useState<"ALL" | TaskPriority>("ALL");

  // Employees cannot list all tasks; they browse by project.
  // Managers/HR/Admin get the full board.
  const projectsQ = useQuery({
    queryKey: ["projects", "all"],
    queryFn: () => projectsApi.list({ size: 200, sort: "name,asc" }),
    staleTime: 60_000,
  });

  const allTasksQ = useQuery({
    queryKey: ["tasks", "all", "board"],
    queryFn: () => tasksApi.list({ size: 200, sort: "createdAt,desc" }),
    enabled: canReadAll && projectFilter === "ALL",
  });

  const projTasksQ = useQuery({
    queryKey: ["tasks", "byProject", projectFilter],
    queryFn: () => tasksApi.byProject(Number(projectFilter), { size: 200, sort: "createdAt,desc" }),
    enabled: projectFilter !== "ALL",
  });

  const isLoading =
    (canReadAll && projectFilter === "ALL" ? allTasksQ.isLoading : projTasksQ.isLoading) ||
    projectsQ.isLoading;
  const isError =
    (canReadAll && projectFilter === "ALL" ? allTasksQ.isError : projTasksQ.isError);
  const errorObj =
    (canReadAll && projectFilter === "ALL" ? allTasksQ.error : projTasksQ.error);

  const items =
    (projectFilter !== "ALL"
      ? projTasksQ.data?.content
      : canReadAll
        ? allTasksQ.data?.content
        : []) ?? [];

  const filtered = useMemo(
    () => (priorityFilter === "ALL" ? items : items.filter((t) => t.priority === priorityFilter)),
    [items, priorityFilter],
  );

  const byStatus = useMemo(() => {
    const groups: Record<TaskStatus, Task[]> = { TODO: [], IN_PROGRESS: [], DONE: [] };
    filtered.forEach((t) => groups[t.status].push(t));
    return groups;
  }, [filtered]);

  const needsProject = !canReadAll && projectFilter === "ALL";

  return (
    <div className="space-y-6">
      <PageHeader
        title="Tasks"
        description="Kanban board across projects — drag focus, ship faster."
        actions={
          <div className="flex flex-wrap items-center gap-2">
            <div className="flex items-center gap-2 rounded-lg border border-border/60 bg-card px-2.5 py-1.5">
              <Filter className="h-3.5 w-3.5 text-muted-foreground" />
              <Select value={projectFilter} onValueChange={setProjectFilter}>
                <SelectTrigger className="h-7 w-44 border-0 bg-transparent px-1 shadow-none focus:ring-0">
                  <SelectValue placeholder="Project" />
                </SelectTrigger>
                <SelectContent>
                  {canReadAll ? <SelectItem value="ALL">All projects</SelectItem> : null}
                  {(projectsQ.data?.content ?? []).map((p) => (
                    <SelectItem key={p.id} value={String(p.id)}>
                      {p.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <Select
              value={priorityFilter}
              onValueChange={(v) => setPriorityFilter(v as "ALL" | TaskPriority)}
            >
              <SelectTrigger className="w-36">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">All priorities</SelectItem>
                {priorities.map((p) => (
                  <SelectItem key={p} value={p}>
                    {p}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {canCreate ? (
              <Button
                onClick={() => setOpenCreate(true)}
                className="bg-sunset-gradient text-white shadow-ember hover:brightness-110"
              >
                <Plus className="h-4 w-4" /> New task
              </Button>
            ) : null}
          </div>
        }
      />

      {needsProject ? (
        <div className="rounded-2xl border border-dashed border-primary/30 bg-card/60 p-10 text-center">
          <div className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-sunset-gradient text-white shadow-ember">
            <FolderKanban className="h-5 w-5" />
          </div>
          <h3 className="text-lg font-semibold">Pick a project to see its tasks</h3>
          <p className="mx-auto mt-1 max-w-md text-sm text-muted-foreground">
            As {user?.role}, you view tasks scoped to a specific project. Choose one from
            the filter above to load its board.
          </p>
        </div>
      ) : isLoading ? (
        <LoadingBlock />
      ) : isError ? (
        <ErrorState message={apiErrorMessage(errorObj)} />
      ) : filtered.length === 0 ? (
        <EmptyState
          title="No tasks yet"
          description={canCreate ? "Create the first task to get moving." : "Nothing here yet."}
        />
      ) : (
        <div className="grid gap-4 md:grid-cols-3">
          {statuses.map((s) => {
            const meta = statusMeta[s];
            const list = byStatus[s];
            return (
              <div
                key={s}
                className="flex flex-col rounded-2xl border border-border/60 bg-card/60 backdrop-blur"
              >
                <div className={cn("rounded-t-2xl bg-gradient-to-r px-4 py-3 text-white", meta.color)}>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2 text-sm font-semibold">
                      <meta.icon className="h-4 w-4" />
                      {meta.label}
                    </div>
                    <span className="rounded-full bg-white/25 px-2 py-0.5 text-xs font-bold">
                      {list.length}
                    </span>
                  </div>
                </div>

                <div className="flex flex-1 flex-col gap-2.5 p-3">
                  {list.length === 0 ? (
                    <p className="rounded-xl border border-dashed border-border/60 py-8 text-center text-xs text-muted-foreground">
                      Nothing here
                    </p>
                  ) : (
                    list.map((t) => (
                      <div
                        key={t.id}
                        className="group relative rounded-xl border border-border/60 bg-card p-3.5 shadow-sm transition hover:-translate-y-0.5 hover:border-primary/40 hover:shadow-md"
                      >
                        <div className="flex items-start justify-between gap-2">
                          <h4 className="text-sm font-semibold leading-snug">{t.title}</h4>
                          {canUpdate || canDelete ? (
                            <DropdownMenu>
                              <DropdownMenuTrigger asChild>
                                <Button
                                  variant="ghost"
                                  size="icon"
                                  className="h-7 w-7 opacity-0 transition group-hover:opacity-100"
                                >
                                  <MoreHorizontal className="h-4 w-4" />
                                </Button>
                              </DropdownMenuTrigger>
                              <DropdownMenuContent align="end">
                                {canUpdate ? (
                                  <DropdownMenuItem onClick={() => setEditing(t)}>
                                    <Pencil className="h-4 w-4" /> Edit
                                  </DropdownMenuItem>
                                ) : null}
                                {canDelete ? (
                                  <DropdownMenuItem
                                    className="text-destructive focus:text-destructive"
                                    onClick={() => setDeleting(t)}
                                  >
                                    <Trash2 className="h-4 w-4" /> Delete
                                  </DropdownMenuItem>
                                ) : null}
                              </DropdownMenuContent>
                            </DropdownMenu>
                          ) : null}
                        </div>
                        {t.description ? (
                          <p className="mt-1.5 line-clamp-2 text-xs text-muted-foreground">
                            {t.description}
                          </p>
                        ) : null}
                        <div className="mt-3 flex flex-wrap items-center gap-2">
                          <span
                            className={cn(
                              "inline-flex items-center gap-1 rounded-full border px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wide",
                              priorityMeta[t.priority].color,
                            )}
                          >
                            <Flag className="h-3 w-3" />
                            {priorityMeta[t.priority].label}
                          </span>
                          <span className="inline-flex items-center gap-1 rounded-full bg-accent px-2 py-0.5 text-[10px] font-medium text-accent-foreground">
                            <FolderKanban className="h-3 w-3" />
                            {t.projectName}
                          </span>
                        </div>
                        <div className="mt-3 flex items-center gap-2 border-t border-border/60 pt-2.5">
                          <div className="flex h-6 w-6 items-center justify-center rounded-full bg-sunset-gradient text-[9px] font-bold text-white">
                            {initialsOf(t.assignedEmployeeName)}
                          </div>
                          <div className="min-w-0 truncate text-xs">
                            <span className="text-muted-foreground">Assigned to </span>
                            <span className="font-medium">{t.assignedEmployeeName}</span>
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Summary footer */}
      {!needsProject && filtered.length > 0 ? (
        <div className="flex flex-wrap items-center gap-4 rounded-xl border border-border/60 bg-card px-4 py-2.5 text-xs text-muted-foreground">
          <span className="inline-flex items-center gap-1.5">
            <ListChecks className="h-3.5 w-3.5" />
            {filtered.length} tasks
          </span>
          {(Object.keys(byStatus) as TaskStatus[]).map((s) => (
            <span key={s} className="inline-flex items-center gap-1.5">
              <span className={cn("h-1.5 w-1.5 rounded-full", statusMeta[s].dot)} />
              {statusMeta[s].label} · {byStatus[s].length}
            </span>
          ))}
          {user ? (
            <span className="ml-auto inline-flex items-center gap-1.5">
              <User2 className="h-3.5 w-3.5" /> Signed in as {user.role}
            </span>
          ) : null}
        </div>
      ) : null}

      {canCreate ? (
        <CreateDialog open={openCreate} onOpenChange={setOpenCreate} />
      ) : null}
      {canUpdate ? (
        <EditDialog task={editing} onOpenChange={(v) => !v && setEditing(null)} />
      ) : null}
      {canDelete ? (
        <DeleteDialog task={deleting} onOpenChange={(v) => !v && setDeleting(null)} />
      ) : null}
    </div>
  );
}

function initialsOf(name?: string) {
  if (!name) return "??";
  const parts = name.trim().split(/\s+/);
  return ((parts[0]?.[0] ?? "") + (parts[1]?.[0] ?? "")).toUpperCase() || "??";
}

function CreateDialog({
  open,
  onOpenChange,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
}) {
  const qc = useQueryClient();
  const projects = useQuery({
    queryKey: ["projects", "all"],
    queryFn: () => projectsApi.list({ size: 200, sort: "name,asc" }),
    staleTime: 60_000,
  });
  const employees = useQuery({
    queryKey: ["employees", "all"],
    queryFn: () => employeesApi.list({ size: 200, sort: "firstName,asc" }),
    staleTime: 60_000,
  });

  const form = useForm<CreateValues>({
    resolver: zodResolver(createSchema),
    defaultValues: {
      title: "",
      description: "",
      priority: "MEDIUM",
      status: "TODO",
      projectId: "",
      assignedEmployeeId: "",
    },
  });

  const mutation = useMutation({
    mutationFn: (v: CreateValues) => {
      const payload: TaskCreatePayload = {
        title: v.title,
        priority: v.priority,
        status: v.status,
        description: v.description || undefined,
        projectId: Number(v.projectId),
        assignedEmployeeId: Number(v.assignedEmployeeId),
      };
      return tasksApi.create(payload);
    },
    onSuccess: () => {
      toast.success("Task created");
      qc.invalidateQueries({ queryKey: ["tasks"] });
      form.reset();
      onOpenChange(false);
    },
    onError: (e) => toast.error(apiErrorMessage(e)),
  });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>New task</DialogTitle>
        </DialogHeader>
        <form onSubmit={form.handleSubmit((v) => mutation.mutate(v))} className="space-y-4">
          <Field label="Title" error={form.formState.errors.title?.message}>
            <Input {...form.register("title")} />
          </Field>
          <Field label="Description">
            <Textarea rows={3} {...form.register("description")} />
          </Field>
          <div className="grid grid-cols-2 gap-4">
            <Field label="Project" error={form.formState.errors.projectId?.message}>
              <Select
                value={form.watch("projectId")}
                onValueChange={(v) => form.setValue("projectId", v)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select" />
                </SelectTrigger>
                <SelectContent>
                  {(projects.data?.content ?? []).map((p) => (
                    <SelectItem key={p.id} value={String(p.id)}>
                      {p.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </Field>
            <Field label="Assignee" error={form.formState.errors.assignedEmployeeId?.message}>
              <Select
                value={form.watch("assignedEmployeeId")}
                onValueChange={(v) => form.setValue("assignedEmployeeId", v)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select" />
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
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Field label="Priority">
              <Select
                value={form.watch("priority")}
                onValueChange={(v) => form.setValue("priority", v as TaskPriority)}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {priorities.map((p) => (
                    <SelectItem key={p} value={p}>
                      {p}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </Field>
            <Field label="Status">
              <Select
                value={form.watch("status")}
                onValueChange={(v) => form.setValue("status", v as TaskStatus)}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {statuses.map((s) => (
                    <SelectItem key={s} value={s}>
                      {s.replace(/_/g, " ")}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </Field>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={mutation.isPending}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={mutation.isPending}
              className="bg-sunset-gradient text-white shadow-ember"
            >
              {mutation.isPending ? "Creating…" : "Create"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function EditDialog({
  task,
  onOpenChange,
}: {
  task: Task | null;
  onOpenChange: (v: boolean) => void;
}) {
  const qc = useQueryClient();
  const form = useForm<EditValues>({
    resolver: zodResolver(editSchema),
    values: task
      ? {
          title: task.title,
          description: task.description ?? "",
          priority: task.priority,
          status: task.status,
        }
      : undefined,
  });

  const mutation = useMutation({
    mutationFn: (v: EditValues) => tasksApi.update(task!.id, v),
    onSuccess: () => {
      toast.success("Task updated");
      qc.invalidateQueries({ queryKey: ["tasks"] });
      onOpenChange(false);
    },
    onError: (e) => toast.error(apiErrorMessage(e)),
  });

  return (
    <Dialog open={!!task} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit task</DialogTitle>
        </DialogHeader>
        <form onSubmit={form.handleSubmit((v) => mutation.mutate(v))} className="space-y-4">
          <Field label="Title">
            <Input {...form.register("title")} />
          </Field>
          <Field label="Description">
            <Textarea rows={3} {...form.register("description")} />
          </Field>
          <div className="grid grid-cols-2 gap-4">
            <Field label="Priority">
              <Select
                value={form.watch("priority")}
                onValueChange={(v) => form.setValue("priority", v as TaskPriority)}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {priorities.map((p) => (
                    <SelectItem key={p} value={p}>
                      {p}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </Field>
            <Field label="Status">
              <Select
                value={form.watch("status")}
                onValueChange={(v) => form.setValue("status", v as TaskStatus)}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {statuses.map((s) => (
                    <SelectItem key={s} value={s}>
                      {s.replace(/_/g, " ")}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </Field>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={mutation.isPending}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={mutation.isPending}
              className="bg-sunset-gradient text-white shadow-ember"
            >
              {mutation.isPending ? "Saving…" : "Save"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function DeleteDialog({
  task,
  onOpenChange,
}: {
  task: Task | null;
  onOpenChange: (v: boolean) => void;
}) {
  const qc = useQueryClient();
  const mutation = useMutation({
    mutationFn: () => tasksApi.remove(task!.id),
    onSuccess: () => {
      toast.success("Task deleted");
      qc.invalidateQueries({ queryKey: ["tasks"] });
      onOpenChange(false);
    },
    onError: (e) => toast.error(apiErrorMessage(e)),
  });
  return (
    <Dialog open={!!task} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Delete task</DialogTitle>
        </DialogHeader>
        <p className="text-sm text-muted-foreground">
          Delete <span className="font-medium text-foreground">{task?.title}</span>?
        </p>
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button
            variant="destructive"
            onClick={() => mutation.mutate()}
            disabled={mutation.isPending}
          >
            Delete
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
