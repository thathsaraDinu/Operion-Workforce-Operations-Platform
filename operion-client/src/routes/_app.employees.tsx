import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";
import { Plus, Pencil, Trash2, MoreHorizontal } from "lucide-react";

import { PageHeader } from "@/components/common/page-header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
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
import {
  employeesApi,
  type EmployeeCreatePayload,
  type EmployeeUpdatePayload,
} from "@/lib/api/employees";
import { departmentsApi } from "@/lib/api/departments";
import { apiErrorMessage } from "@/lib/api/client";
import { fmtDate } from "@/lib/format";
import type { Employee, Role } from "@/lib/api/types";

const roleOptions: Role[] = ["ADMIN", "HR", "MANAGER", "EMPLOYEE"];

const createSchema = z.object({
  firstName: z.string().trim().min(1, "Required"),
  lastName: z.string().trim().min(1, "Required"),
  email: z.string().trim().email(),
  password: z.string().min(6, "At least 6 characters"),
  role: z.enum(["ADMIN", "HR", "MANAGER", "EMPLOYEE"]),
  departmentId: z.string().optional(),
  phone: z.string().optional(),
  address: z.string().optional(),
  position: z.string().optional(),
  joiningDate: z.string().optional(),
});
type CreateFormValues = z.infer<typeof createSchema>;

export const Route = createFileRoute("/_app/employees")({
  head: () => ({ meta: [{ title: "Employees — Operion" }] }),
  component: EmployeesPage,
});

function EmployeesPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [openCreate, setOpenCreate] = useState(false);
  const [editing, setEditing] = useState<Employee | null>(null);
  const [deleting, setDeleting] = useState<Employee | null>(null);
  const canManage = useHasRole("ADMIN", "HR");

  const query = useQuery({
    queryKey: ["employees", { page, size }],
    queryFn: () => employeesApi.list({ page, size, sort: "id,desc" }),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        title="Employees"
        description="Manage employee records across your organization."
        actions={
          <RoleGate roles={["ADMIN", "HR"]}>
            <Button onClick={() => setOpenCreate(true)}>
              <Plus className="h-4 w-4" /> New employee
            </Button>
          </RoleGate>
        }
      />

      {query.isLoading ? (
        <LoadingBlock />
      ) : query.isError ? (
        <ErrorState message={apiErrorMessage(query.error)} />
      ) : (query.data?.content.length ?? 0) === 0 ? (
        <EmptyState title="No employees yet" description="Create the first employee to get started." />
      ) : (
        <Card className="overflow-hidden border-border/60 p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>Role</TableHead>
                <TableHead>Department</TableHead>
                <TableHead>Position</TableHead>
                <TableHead>Joined</TableHead>
                <TableHead>Status</TableHead>
                {canManage ? <TableHead className="w-10" /> : null}
              </TableRow>
            </TableHeader>
            <TableBody>
              {query.data!.content.map((e) => (
                <TableRow key={e.id}>
                  <TableCell className="font-medium">
                    {e.firstName} {e.lastName}
                  </TableCell>
                  <TableCell className="text-muted-foreground">{e.email}</TableCell>
                  <TableCell>
                    <StatusBadge value={e.role} />
                  </TableCell>
                  <TableCell>{e.departmentName ?? "—"}</TableCell>
                  <TableCell>{e.position ?? "—"}</TableCell>
                  <TableCell>{fmtDate(e.joiningDate)}</TableCell>
                  <TableCell>
                    <StatusBadge value={e.status} />
                  </TableCell>
                  {canManage ? (
                    <TableCell>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => setEditing(e)}>
                            <Pencil className="h-4 w-4" /> Edit
                          </DropdownMenuItem>
                          <DropdownMenuItem
                            className="text-destructive focus:text-destructive"
                            onClick={() => setDeleting(e)}
                          >
                            <Trash2 className="h-4 w-4" /> Delete
                          </DropdownMenuItem>
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

      <CreateDialog open={openCreate} onOpenChange={setOpenCreate} />
      <EditDialog employee={editing} onOpenChange={(v) => !v && setEditing(null)} />
      <DeleteDialog employee={deleting} onOpenChange={(v) => !v && setDeleting(null)} />
    </div>
  );
}

function useDepartmentOptions() {
  return useQuery({
    queryKey: ["departments", "all"],
    queryFn: () => departmentsApi.list({ size: 200, sort: "name,asc" }),
    staleTime: 60_000,
  });
}

function CreateDialog({
  open,
  onOpenChange,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
}) {
  const qc = useQueryClient();
  const departments = useDepartmentOptions();
  const form = useForm<CreateFormValues>({
    resolver: zodResolver(createSchema),
    defaultValues: { role: "EMPLOYEE" },
  });

  const mutation = useMutation({
    mutationFn: (values: CreateFormValues) => {
      const payload: EmployeeCreatePayload = {
        firstName: values.firstName,
        lastName: values.lastName,
        email: values.email,
        password: values.password,
        role: values.role,
        departmentId: values.departmentId ? Number(values.departmentId) : undefined,
        phone: values.phone || undefined,
        address: values.address || undefined,
        position: values.position || undefined,
        joiningDate: values.joiningDate || undefined,
      };
      return employeesApi.create(payload);
    },
    onSuccess: () => {
      toast.success("Employee created");
      qc.invalidateQueries({ queryKey: ["employees"] });
      form.reset({ role: "EMPLOYEE" });
      onOpenChange(false);
    },
    onError: (e) => toast.error(apiErrorMessage(e, "Failed to create employee")),
  });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>New employee</DialogTitle>
        </DialogHeader>
        <form
          onSubmit={form.handleSubmit((v) => mutation.mutate(v))}
          className="grid grid-cols-1 gap-4 sm:grid-cols-2"
        >
          <Field label="First name" error={form.formState.errors.firstName?.message}>
            <Input {...form.register("firstName")} />
          </Field>
          <Field label="Last name" error={form.formState.errors.lastName?.message}>
            <Input {...form.register("lastName")} />
          </Field>
          <Field label="Email" error={form.formState.errors.email?.message}>
            <Input type="email" {...form.register("email")} />
          </Field>
          <Field label="Password" error={form.formState.errors.password?.message}>
            <Input type="password" {...form.register("password")} />
          </Field>
          <Field label="Role">
            <Select
              value={form.watch("role")}
              onValueChange={(v) => form.setValue("role", v as Role)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {roleOptions.map((r) => (
                  <SelectItem key={r} value={r}>
                    {r}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Department">
            <Select
              value={form.watch("departmentId") ?? ""}
              onValueChange={(v) => form.setValue("departmentId", v)}
            >
              <SelectTrigger>
                <SelectValue placeholder="None" />
              </SelectTrigger>
              <SelectContent>
                {(departments.data?.content ?? []).map((d) => (
                  <SelectItem key={d.id} value={String(d.id)}>
                    {d.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Position">
            <Input {...form.register("position")} />
          </Field>
          <Field label="Joining date">
            <Input type="date" {...form.register("joiningDate")} />
          </Field>
          <Field label="Phone">
            <Input {...form.register("phone")} />
          </Field>
          <Field label="Address">
            <Input {...form.register("address")} />
          </Field>

          <DialogFooter className="col-span-full">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={mutation.isPending}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? "Creating…" : "Create"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

const editSchema = z.object({
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  email: z.string().email().optional().or(z.literal("")),
  password: z.string().optional(),
  role: z.enum(["ADMIN", "HR", "MANAGER", "EMPLOYEE"]).optional(),
  phone: z.string().optional(),
  address: z.string().optional(),
  position: z.string().optional(),
  joiningDate: z.string().optional(),
  departmentId: z.string().optional(),
});
type EditFormValues = z.infer<typeof editSchema>;

function EditDialog({
  employee,
  onOpenChange,
}: {
  employee: Employee | null;
  onOpenChange: (v: boolean) => void;
}) {
  const qc = useQueryClient();
  const departments = useDepartmentOptions();

  const form = useForm<EditFormValues>({
    resolver: zodResolver(editSchema),
    values: employee
      ? {
          firstName: employee.firstName,
          lastName: employee.lastName,
          email: employee.email,
          role: employee.role,
          phone: employee.phone ?? "",
          address: employee.address ?? "",
          position: employee.position ?? "",
          joiningDate: employee.joiningDate ?? "",
          departmentId: employee.departmentId ? String(employee.departmentId) : "",
          password: "",
        }
      : undefined,
  });

  const update = useMutation({
    mutationFn: async (values: EditFormValues) => {
      if (!employee) return;
      const payload: EmployeeUpdatePayload = {};
      (
        ["firstName", "lastName", "email", "role", "phone", "address", "position", "joiningDate"] as const
      ).forEach((k) => {
        const v = values[k];
        if (v !== undefined && v !== "") (payload as Record<string, unknown>)[k] = v;
      });
      if (values.password) payload.password = values.password;
      await employeesApi.update(employee.id, payload);
      if (values.departmentId && Number(values.departmentId) !== employee.departmentId) {
        await employeesApi.assignDepartment(employee.id, Number(values.departmentId));
      }
    },
    onSuccess: () => {
      toast.success("Employee updated");
      qc.invalidateQueries({ queryKey: ["employees"] });
      onOpenChange(false);
    },
    onError: (e) => toast.error(apiErrorMessage(e, "Update failed")),
  });

  return (
    <Dialog open={!!employee} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>Edit employee</DialogTitle>
        </DialogHeader>
        <form
          onSubmit={form.handleSubmit((v) => update.mutate(v))}
          className="grid grid-cols-1 gap-4 sm:grid-cols-2"
        >
          <Field label="First name">
            <Input {...form.register("firstName")} />
          </Field>
          <Field label="Last name">
            <Input {...form.register("lastName")} />
          </Field>
          <Field label="Email">
            <Input type="email" {...form.register("email")} />
          </Field>
          <Field label="New password">
            <Input type="password" placeholder="Leave blank to keep" {...form.register("password")} />
          </Field>
          <Field label="Role">
            <Select
              value={form.watch("role")}
              onValueChange={(v) => form.setValue("role", v as Role)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {roleOptions.map((r) => (
                  <SelectItem key={r} value={r}>
                    {r}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Department">
            <Select
              value={form.watch("departmentId") || ""}
              onValueChange={(v) => form.setValue("departmentId", v)}
            >
              <SelectTrigger>
                <SelectValue placeholder="None" />
              </SelectTrigger>
              <SelectContent>
                {(departments.data?.content ?? []).map((d) => (
                  <SelectItem key={d.id} value={String(d.id)}>
                    {d.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Position">
            <Input {...form.register("position")} />
          </Field>
          <Field label="Joining date">
            <Input type="date" {...form.register("joiningDate")} />
          </Field>
          <Field label="Phone">
            <Input {...form.register("phone")} />
          </Field>
          <Field label="Address">
            <Input {...form.register("address")} />
          </Field>
          <DialogFooter className="col-span-full">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={update.isPending}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={update.isPending}>
              {update.isPending ? "Saving…" : "Save"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function DeleteDialog({
  employee,
  onOpenChange,
}: {
  employee: Employee | null;
  onOpenChange: (v: boolean) => void;
}) {
  const qc = useQueryClient();
  const mutation = useMutation({
    mutationFn: () => employeesApi.remove(employee!.id),
    onSuccess: () => {
      toast.success("Employee deleted");
      qc.invalidateQueries({ queryKey: ["employees"] });
      onOpenChange(false);
    },
    onError: (e) => toast.error(apiErrorMessage(e, "Delete failed")),
  });

  return (
    <Dialog open={!!employee} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Delete employee</DialogTitle>
        </DialogHeader>
        <p className="text-sm text-muted-foreground">
          Are you sure you want to delete{" "}
          <span className="font-medium text-foreground">
            {employee?.firstName} {employee?.lastName}
          </span>
          ? This action cannot be undone.
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
            {mutation.isPending ? "Deleting…" : "Delete"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

export function Field({
  label,
  error,
  children,
}: {
  label: string;
  error?: string;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-1.5">
      <Label className="text-xs font-medium text-muted-foreground">{label}</Label>
      {children}
      {error ? <p className="text-xs text-destructive">{error}</p> : null}
    </div>
  );
}
