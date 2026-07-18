import { createFileRoute, useNavigate, useSearch } from "@tanstack/react-router";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";
import { ArrowLeft, ArrowRight, Flame, Lock } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { resetPassword } from "@/lib/api/auth";
import { apiErrorMessage } from "@/lib/api/client";

const schema = z.object({
  newPassword: z.string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
    .regex(/[a-z]/, "Password must contain at least one lowercase letter")
    .regex(/\d/, "Password must contain at least one number")
    .regex(/[@$!%*?&]/, "Password must contain at least one special character (@$!%*?&)"),
  confirmPassword: z.string()
    .min(8, "Password must be at least 8 characters"),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: "Passwords do not match",
  path: ["confirmPassword"],
});
type FormValues = z.infer<typeof schema>;

export const Route = createFileRoute("/reset-password")({
  validateSearch: z.object({
    token: z.string().optional(),
  }),
  head: () => ({ meta: [{ title: "Reset Password — Operion" }] }),
  component: ResetPasswordPage,
});

function ResetPasswordPage() {
  const navigate = useNavigate();
  const search = useSearch({ from: "/reset-password" });
  const token = search.token;

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { newPassword: "", confirmPassword: "" },
  });

  const mutation = useMutation({
    mutationFn: (values: FormValues) => resetPassword({ token: token || "", newPassword: values.newPassword }),
    onSuccess: () => {
      toast.success("Password reset successfully");
      navigate({ to: "/login" });
    },
    onError: (err) => toast.error(apiErrorMessage(err, "Failed to reset password")),
  });

  if (!token) {
    return (
      <div className="relative min-h-screen overflow-hidden bg-background">
        <div className="pointer-events-none absolute inset-0 bg-ember-radial opacity-70" aria-hidden />
        <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_50%_120%,white,transparent_60%)]" aria-hidden />

        <div className="relative flex min-h-screen items-center justify-center px-5 py-10 sm:px-8">
          <div className="w-full max-w-md">
            <div className="rounded-2xl border border-border/60 bg-card/90 p-8 shadow-xl backdrop-blur">
              <div className="mb-7 space-y-1.5">
                <h2 className="text-2xl font-semibold tracking-tight">Invalid reset link</h2>
                <p className="text-sm text-muted-foreground">
                  This password reset link is invalid or has expired.
                </p>
              </div>
              <Button
                type="button"
                variant="ghost"
                size="sm"
                className="w-full"
                onClick={() => navigate({ to: "/forgot-password" })}
              >
                Request a new reset link
              </Button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="relative min-h-screen overflow-hidden bg-background">
      <div className="pointer-events-none absolute inset-0 bg-ember-radial opacity-70" aria-hidden />
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_50%_120%,white,transparent_60%)]" aria-hidden />

      <div className="relative flex min-h-screen items-center justify-center px-5 py-10 sm:px-8">
        <div className="w-full max-w-md">
          <div className="mb-8 flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-sunset-gradient text-white shadow-ember">
              <Flame className="h-5 w-5" />
            </div>
            <div>
              <div className="text-lg font-bold">Operion</div>
              <div className="text-xs text-muted-foreground">Workforce Operations</div>
            </div>
          </div>

          <div className="rounded-2xl border border-border/60 bg-card/90 p-8 shadow-xl backdrop-blur">
            <div className="mb-7 space-y-1.5">
              <h2 className="text-2xl font-semibold tracking-tight">Reset password</h2>
              <p className="text-sm text-muted-foreground">
                Enter your new password below.
              </p>
            </div>

            <form
              className="space-y-5"
              onSubmit={form.handleSubmit((values) => mutation.mutate(values))}
              noValidate
            >
              <div className="space-y-1.5">
                <Label htmlFor="newPassword" className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                  New password
                </Label>
                <Input
                  id="newPassword"
                  type="password"
                  autoComplete="new-password"
                  placeholder="••••••••"
                  className="h-11"
                  {...form.register("newPassword")}
                />
                {form.formState.errors.newPassword ? (
                  <p className="text-xs text-destructive">
                    {form.formState.errors.newPassword.message}
                  </p>
                ) : null}
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="confirmPassword" className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                  Confirm password
                </Label>
                <Input
                  id="confirmPassword"
                  type="password"
                  autoComplete="new-password"
                  placeholder="••••••••"
                  className="h-11"
                  {...form.register("confirmPassword")}
                />
                {form.formState.errors.confirmPassword ? (
                  <p className="text-xs text-destructive">
                    {form.formState.errors.confirmPassword.message}
                  </p>
                ) : null}
              </div>

              <Button
                type="submit"
                size="lg"
                className="group h-11 w-full bg-sunset-gradient text-white shadow-ember hover:brightness-110"
                disabled={mutation.isPending}
              >
                {mutation.isPending ? "Resetting…" : "Reset password"}
                <ArrowRight className="ml-1 h-4 w-4 transition-transform group-hover:translate-x-0.5" />
              </Button>
            </form>

            <Button
              type="button"
              variant="ghost"
              size="sm"
              className="mt-6 w-full"
              onClick={() => navigate({ to: "/login" })}
            >
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back to sign in
            </Button>
          </div>

          <p className="mt-6 text-center text-[11px] text-muted-foreground">
            © {new Date().getFullYear()} Operion · All rights reserved
          </p>
        </div>
      </div>
    </div>
  );
}
