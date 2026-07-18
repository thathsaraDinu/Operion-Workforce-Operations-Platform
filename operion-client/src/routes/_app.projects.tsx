import { createFileRoute, Link } from "@tanstack/react-router";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";
import { Plus, MoreHorizontal, Pencil, Trash2, ArrowRight } from "lucide-react";

import { PageHeader } from "@/components/common/page-header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent } from "@/components/ui/card";
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
import { StatusBadge } from "@/components/common/status-badge";
import { PaginationBar } from "@/components/common/pagination-bar";
import { LoadingBlock, EmptyState, ErrorState } from "@/components/common/states";
import { RoleGate, useHasRole } from "@/components/common/role-gate";
import { Field } from "@/routes/_app.employees";
import { projectsApi, type ProjectCreatePayload } from "@/lib/api/projects";
import { apiErrorMessage } from "@/lib/api/client";
import { fmtDate } from "@/lib/format";
import type { Project, ProjectStatus } from "@/lib/api/types";

const statuses: ProjectStatus[] = ["PLANNING", "ACTIVE", "COMPLETED", "CANCELLED"];

const schema = z.object({
  name: z.string().trim().min(1),
  description: z.string().optional(),
  startDate: z.string().min(1, "Required"),
  endDate: z.string().optional(),
  status: z.enum(["PLANNING", "ACTIVE", "COMPLETED", "CANCELLED"]),
});
type FormValues = z.infer<typeof schema>;

export const Route = createFileRoute("/_app/projects")({
  head: () => ({ meta: [{ title: "Projects — Operion" }] }),
  component: ProjectsPage,
});

function ProjectsPage() {
  const [page, setPage] = useState(0);
  const [openCreate, setOpenCreate] = useState(false);
  const [editing, setEditing] = useState<Project | null>(null);
  const [deleting, setDeleting] = useState<Project | null>(null);
  const canManage = useHasRole("ADMIN", "HR", "MANAGER");
  const canDelete = useHasRole("ADMIN", "HR");

  const query = useQuery({
    queryKey: ["projects", { page }],
    queryFn: () => projectsApi.list({ page, size: 12, sort: "id,desc" }),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        title="Projects"
        description="Track initiatives across the organization."
        actions={
          <RoleGate roles={["ADMIN", "HR", "MANAGER"]}>
            <Button onClick={() => setOpenCreate(true)}>
              <Plus className="h-4 w-4" /> New project
            </Button>
          </RoleGate>
        }
      />

      {query.isLoading ? (
        <LoadingBlock />
      ) : query.isError ? (
        <ErrorState message={apiErrorMessage(query.error)} />
      ) : (query.data?.content.length ?? 0) === 0 ? (
        <EmptyState title="No projects yet" />
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {query.data!.content.map((p) => (
              <Card key={p.id} className="border-border/60 transition-shadow hover:shadow-sm">
                <CardContent className="space-y-3 p-5">
                  <div className="flex items-start justify-between gap-2">
                    <div className="min-w-0">
                      <Link
                        to="/projects/$projectId"
                        params={{ projectId: String(p.id) }}
                        className="text-base font-semibold text-foreground hover:text-primary"
                      >
                        {p.name}
                      </Link>
                      <p className="mt-1 line-clamp-2 text-sm text-muted-foreground">
                        {p.description ?? "No description"}
                      </p>
                    </div>
                    {canManage ? (
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => setEditing(p)}>
                            <Pencil className="h-4 w-4" /> Edit
                          </DropdownMenuItem>
                          {canDelete ? (
                            <DropdownMenuItem
                              className="text-destructive focus:text-destructive"
                              onClick={() => setDeleting(p)}
                            >
                              <Trash2 className="h-4 w-4" /> Delete
                            </DropdownMenuItem>
                          ) : null}
                        </DropdownMenuContent>
                      </DropdownMenu>
                    ) : null}
                  </div>
                  <div className="flex items-center justify-between text-xs text-muted-foreground">
                    <div>
                      {fmtDate(p.startDate)} → {p.endDate ? fmtDate(p.endDate) : "ongoing"}
                    </div>
                    <StatusBadge value={p.status} />
                  </div>
                  <Link
                    to="/projects/$projectId"
                    params={{ projectId: String(p.id) }}
                    className="inline-flex items-center gap-1 text-xs font-medium text-primary hover:underline"
                  >
                    View details <ArrowRight className="h-3 w-3" />
                  </Link>
                </CardContent>
              </Card>
            ))}
          </div>
          <div className="rounded-lg border border-border/60 bg-card">
            <PaginationBar
              page={page}
              totalPages={query.data!.totalPages}
              totalElements={query.data!.totalElements}
              onPageChange={setPage}
            />
          </div>
        </>
      )}

      <FormDialog open={openCreate} onOpenChange={setOpenCreate} title="New project" submit="Create" />
      <FormDialog
        open={!!editing}
        onOpenChange={(v) => !v && setEditing(null)}
        title="Edit project"
        submit="Save"
        initial={editing ?? undefined}
      />
      <Dialog open={!!deleting} onOpenChange={(v) => !v && setDeleting(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete project</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            Delete <span className="font-medium text-foreground">{deleting?.name}</span>?
          </p>
          <DeleteFooter project={deleting} onDone={() => setDeleting(null)} />
        </DialogContent>
      </Dialog>
    </div>
  );
}

function DeleteFooter({ project, onDone }: { project: Project | null; onDone: () => void }) {
  const qc = useQueryClient();
  const mutation = useMutation({
    mutationFn: () => projectsApi.remove(project!.id),
    onSuccess: () => {
      toast.success("Project deleted");
      qc.invalidateQueries({ queryKey: ["projects"] });
      onDone();
    },
    onError: (e) => toast.error(apiErrorMessage(e)),
  });
  return (
    <DialogFooter>
      <Button variant="outline" onClick={onDone}>
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
  );
}

function FormDialog({
  open,
  onOpenChange,
  title,
  submit,
  initial,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  title: string;
  submit: string;
  initial?: Project;
}) {
  const qc = useQueryClient();
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    values: initial
      ? {
          name: initial.name,
          description: initial.description ?? "",
          startDate: initial.startDate,
          endDate: initial.endDate ?? "",
          status: initial.status,
        }
      : undefined,
    defaultValues: {
      name: "",
      description: "",
      startDate: "",
      endDate: "",
      status: "PLANNING",
    },
  });

  const mutation = useMutation({
    mutationFn: (values: FormValues) => {
      const payload: ProjectCreatePayload = {
        name: values.name,
        status: values.status,
        startDate: values.startDate,
        description: values.description || undefined,
        endDate: values.endDate || undefined,
      };
      return initial ? projectsApi.update(initial.id, payload) : projectsApi.create(payload);
    },
    onSuccess: () => {
      toast.success(initial ? "Project updated" : "Project created");
      qc.invalidateQueries({ queryKey: ["projects"] });
      onOpenChange(false);
      form.reset();
    },
    onError: (e) => toast.error(apiErrorMessage(e)),
  });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
        </DialogHeader>
        <form onSubmit={form.handleSubmit((v) => mutation.mutate(v))} className="space-y-4">
          <Field label="Name" error={form.formState.errors.name?.message}>
            <Input {...form.register("name")} />
          </Field>
          <Field label="Description">
            <Textarea rows={3} {...form.register("description")} />
          </Field>
          <div className="grid grid-cols-2 gap-4">
            <Field label="Start date" error={form.formState.errors.startDate?.message}>
              <Input type="date" {...form.register("startDate")} />
            </Field>
            <Field label="End date">
              <Input type="date" {...form.register("endDate")} />
            </Field>
          </div>
          <Field label="Status">
            <Select
              value={form.watch("status")}
              onValueChange={(v) => form.setValue("status", v as ProjectStatus)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {statuses.map((s) => (
                  <SelectItem key={s} value={s}>
                    {s}
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
              {mutation.isPending ? "Saving…" : submit}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
