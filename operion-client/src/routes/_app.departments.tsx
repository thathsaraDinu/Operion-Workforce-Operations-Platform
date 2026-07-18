import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";
import { Plus, Pencil, Trash2, MoreHorizontal } from "lucide-react";

import { PageHeader } from "@/components/common/page-header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Card } from "@/components/ui/card";
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
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { PaginationBar } from "@/components/common/pagination-bar";
import { LoadingBlock, EmptyState, ErrorState } from "@/components/common/states";
import { RoleGate, useHasRole } from "@/components/common/role-gate";
import { Field } from "@/routes/_app.employees";
import { departmentsApi, type DepartmentPayload } from "@/lib/api/departments";
import { apiErrorMessage } from "@/lib/api/client";
import type { Department } from "@/lib/api/types";

const schema = z.object({
  name: z.string().trim().min(1),
  code: z.string().trim().min(1),
  description: z.string().optional(),
});
type FormValues = z.infer<typeof schema>;

export const Route = createFileRoute("/_app/departments")({
  head: () => ({ meta: [{ title: "Departments — Operion" }] }),
  component: DepartmentsPage,
});

function DepartmentsPage() {
  const [page, setPage] = useState(0);
  const [openCreate, setOpenCreate] = useState(false);
  const [editing, setEditing] = useState<Department | null>(null);
  const [deleting, setDeleting] = useState<Department | null>(null);
  const canManage = useHasRole("ADMIN", "HR");
  const canDelete = useHasRole("ADMIN");

  const query = useQuery({
    queryKey: ["departments", { page }],
    queryFn: () => departmentsApi.list({ page, size: 10, sort: "name,asc" }),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        title="Departments"
        description="Structure your organization into functional units."
        actions={
          <RoleGate roles={["ADMIN", "HR"]}>
            <Button onClick={() => setOpenCreate(true)}>
              <Plus className="h-4 w-4" /> New department
            </Button>
          </RoleGate>
        }
      />

      {query.isLoading ? (
        <LoadingBlock />
      ) : query.isError ? (
        <ErrorState message={apiErrorMessage(query.error)} />
      ) : (query.data?.content.length ?? 0) === 0 ? (
        <EmptyState title="No departments" description="Create a department to organize employees." />
      ) : (
        <Card className="overflow-hidden border-border/60 p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Code</TableHead>
                <TableHead>Description</TableHead>
                <TableHead className="text-right">Employees</TableHead>
                {canManage ? <TableHead className="w-10" /> : null}
              </TableRow>
            </TableHeader>
            <TableBody>
              {query.data!.content.map((d) => (
                <TableRow key={d.id}>
                  <TableCell className="font-medium">{d.name}</TableCell>
                  <TableCell className="font-mono text-xs text-muted-foreground">{d.code}</TableCell>
                  <TableCell className="max-w-md truncate text-muted-foreground">
                    {d.description ?? "—"}
                  </TableCell>
                  <TableCell className="text-right">{d.employeeCount}</TableCell>
                  {canManage ? (
                    <TableCell>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => setEditing(d)}>
                            <Pencil className="h-4 w-4" /> Edit
                          </DropdownMenuItem>
                          {canDelete ? (
                            <DropdownMenuItem
                              className="text-destructive focus:text-destructive"
                              onClick={() => setDeleting(d)}
                            >
                              <Trash2 className="h-4 w-4" /> Delete
                            </DropdownMenuItem>
                          ) : null}
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  ) : null}
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
      )}

      <FormDialog
        open={openCreate}
        onOpenChange={setOpenCreate}
        title="New department"
        submitLabel="Create"
      />
      <FormDialog
        open={!!editing}
        onOpenChange={(v) => !v && setEditing(null)}
        title="Edit department"
        submitLabel="Save"
        initial={editing ?? undefined}
      />

      <Dialog open={!!deleting} onOpenChange={(v) => !v && setDeleting(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete department</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            Delete <span className="font-medium text-foreground">{deleting?.name}</span>? This
            cannot be undone.
          </p>
          <DeleteFooter department={deleting} onDone={() => setDeleting(null)} />
        </DialogContent>
      </Dialog>
    </div>
  );
}

function DeleteFooter({
  department,
  onDone,
}: {
  department: Department | null;
  onDone: () => void;
}) {
  const qc = useQueryClient();
  const mutation = useMutation({
    mutationFn: () => departmentsApi.remove(department!.id),
    onSuccess: () => {
      toast.success("Department deleted");
      qc.invalidateQueries({ queryKey: ["departments"] });
      onDone();
    },
    onError: (e) => toast.error(apiErrorMessage(e, "Delete failed")),
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
        {mutation.isPending ? "Deleting…" : "Delete"}
      </Button>
    </DialogFooter>
  );
}

function FormDialog({
  open,
  onOpenChange,
  title,
  submitLabel,
  initial,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  title: string;
  submitLabel: string;
  initial?: Department;
}) {
  const qc = useQueryClient();
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    values: initial
      ? { name: initial.name, code: initial.code, description: initial.description ?? "" }
      : undefined,
    defaultValues: { name: "", code: "", description: "" },
  });

  const mutation = useMutation({
    mutationFn: async (values: FormValues) => {
      const payload: DepartmentPayload = {
        name: values.name,
        code: values.code,
        description: values.description || undefined,
      };
      if (initial) return departmentsApi.update(initial.id, payload);
      return departmentsApi.create(payload);
    },
    onSuccess: () => {
      toast.success(initial ? "Department updated" : "Department created");
      qc.invalidateQueries({ queryKey: ["departments"] });
      form.reset({ name: "", code: "", description: "" });
      onOpenChange(false);
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
          <Field label="Code" error={form.formState.errors.code?.message}>
            <Input {...form.register("code")} />
          </Field>
          <Field label="Description">
            <Textarea rows={3} {...form.register("description")} />
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
              {mutation.isPending ? "Saving…" : submitLabel}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
