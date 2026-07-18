import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";
import { ArrowLeft, ArrowRight, Flame, Mail } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { forgotPassword } from "@/lib/api/auth";
import { apiErrorMessage } from "@/lib/api/client";

const schema = z.object({
  email: z.string().trim().email("Enter a valid email"),
});
type FormValues = z.infer<typeof schema>;

export const Route = createFileRoute("/forgot-password")({
  head: () => ({ meta: [{ title: "Forgot Password — Operion" }] }),
  component: ForgotPasswordPage,
});

function ForgotPasswordPage() {
  const navigate = useNavigate();

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { email: "" },
  });

  const mutation = useMutation({
    mutationFn: forgotPassword,
    onSuccess: () => {
      toast.success("Password reset link sent to your email");
      form.reset();
    },
    onError: (err) => toast.error(apiErrorMessage(err, "Failed to send reset link")),
  });

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
              <h2 className="text-2xl font-semibold tracking-tight">Forgot password?</h2>
              <p className="text-sm text-muted-foreground">
                Enter your email and we'll send you a reset link.
              </p>
            </div>

            <form
              className="space-y-5"
              onSubmit={form.handleSubmit((values) => mutation.mutate(values))}
              noValidate
            >
              <div className="space-y-1.5">
                <Label htmlFor="email" className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                  Email address
                </Label>
                <Input
                  id="email"
                  type="email"
                  autoComplete="email"
                  placeholder="you@company.com"
                  className="h-11"
                  {...form.register("email")}
                />
                {form.formState.errors.email ? (
                  <p className="text-xs text-destructive">
                    {form.formState.errors.email.message}
                  </p>
                ) : null}
              </div>

              <Button
                type="submit"
                size="lg"
                className="group h-11 w-full bg-sunset-gradient text-white shadow-ember hover:brightness-110"
                disabled={mutation.isPending}
              >
                {mutation.isPending ? "Sending…" : "Send reset link"}
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
