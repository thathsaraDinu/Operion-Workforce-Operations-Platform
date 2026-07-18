import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";
import {
  ArrowRight,
  CalendarClock,
  CalendarOff,
  Flame,
  FolderKanban,
  ListChecks,
  ShieldCheck,
  Sparkles,
  Users,
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { login } from "@/lib/api/auth";
import { apiErrorMessage } from "@/lib/api/client";
import { useAuthStore } from "@/stores/auth-store";

const schema = z.object({
  email: z.string().trim().email("Enter a valid email"),
  password: z.string().min(1, "Password is required"),
});
type FormValues = z.infer<typeof schema>;

export const Route = createFileRoute("/login")({
  head: () => ({ meta: [{ title: "Sign in — Operion" }] }),
  component: LoginPage,
});

const features = [
  { icon: Users, label: "People", copy: "Directory & departments" },
  { icon: CalendarClock, label: "Attendance", copy: "One-tap clock in & out" },
  { icon: CalendarOff, label: "Leave", copy: "Requests + approvals" },
  { icon: FolderKanban, label: "Projects", copy: "Teams & roles" },
  { icon: ListChecks, label: "Tasks", copy: "Priority & status board" },
];

function LoginPage() {
  const navigate = useNavigate();
  const setSession = useAuthStore((s) => s.setSession);
  const token = useAuthStore((s) => s.token);

  useEffect(() => {
    if (token) void navigate({ to: "/dashboard", replace: true });
  }, [token, navigate]);

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { email: "", password: "" },
  });

  const mutation = useMutation({
    mutationFn: login,
    onSuccess: (data) => {
      setSession(data);
      toast.success("Welcome back");
      void navigate({ to: "/dashboard", replace: true });
    },
    onError: (err) => toast.error(apiErrorMessage(err, "Sign in failed")),
  });

  return (
    <div className="relative min-h-screen overflow-hidden bg-background">
      {/* Ambient warm background */}
      <div className="pointer-events-none absolute inset-0 bg-ember-radial opacity-70" aria-hidden />
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_50%_120%,white,transparent_60%)]" aria-hidden />

      <div className="relative grid min-h-screen lg:grid-cols-[1.05fr_1fr]">
        {/* Hero panel */}
        <div className="hidden flex-col justify-between p-10 lg:flex xl:p-14">
          <div className="flex items-center gap-3">
            <div className="relative flex h-11 w-11 items-center justify-center rounded-xl bg-sunset-gradient text-white shadow-ember">
              <Flame className="h-5 w-5" strokeWidth={2.4} />
            </div>
            <div>
              <div className="text-xl font-bold tracking-tight">Operion</div>
              <div className="text-xs text-muted-foreground">Workforce Operations</div>
            </div>
          </div>

          <div className="space-y-8">
            <div className="inline-flex items-center gap-2 rounded-full border border-primary/20 bg-primary/5 px-3 py-1 text-xs font-medium text-primary">
              <Sparkles className="h-3.5 w-3.5" />
              Built for modern teams
            </div>
            <h1
              className="max-w-xl text-5xl font-semibold leading-[1.05] tracking-tight xl:text-6xl"
              style={{ fontFamily: '"Fraunces", ui-serif, Georgia, serif' }}
            >
              Run your{" "}
              <span className="text-sunset-gradient">workforce</span> with warmth &
              clarity.
            </h1>
            <p className="max-w-md text-base leading-relaxed text-muted-foreground">
              Employees, attendance, leave, projects and tasks — a single, beautifully
              designed control room for HR, managers and their teams.
            </p>

            <div className="grid max-w-lg grid-cols-1 gap-2 sm:grid-cols-2">
              {features.map((f) => (
                <div
                  key={f.label}
                  className="flex items-start gap-3 rounded-xl border border-border/60 bg-card/70 p-3 backdrop-blur transition hover:border-primary/40 hover:shadow-sm"
                >
                  <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-accent text-accent-foreground">
                    <f.icon className="h-4 w-4" />
                  </div>
                  <div>
                    <div className="text-sm font-semibold">{f.label}</div>
                    <div className="text-xs text-muted-foreground">{f.copy}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="flex items-center gap-2 text-xs text-muted-foreground">
            <ShieldCheck className="h-4 w-4 text-primary" />
            Secure JWT authentication · Role-based access
          </div>
        </div>

        {/* Auth card */}
        <div className="flex items-center justify-center px-5 py-10 sm:px-8">
          <div className="w-full max-w-md">
            <div className="mb-8 flex items-center gap-3 lg:hidden">
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
                <h2 className="text-2xl font-semibold tracking-tight">Welcome back</h2>
                <p className="text-sm text-muted-foreground">
                  Sign in to continue to your workspace.
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

                <div className="space-y-1.5">
                  <Label htmlFor="password" className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                    Password
                  </Label>
                  <Input
                    id="password"
                    type="password"
                    autoComplete="current-password"
                    placeholder="••••••••"
                    className="h-11"
                    {...form.register("password")}
                  />
                  {form.formState.errors.password ? (
                    <p className="text-xs text-destructive">
                      {form.formState.errors.password.message}
                    </p>
                  ) : null}
                </div>

                <Button
                  type="submit"
                  size="lg"
                  className="group h-11 w-full bg-sunset-gradient text-white shadow-ember hover:brightness-110"
                  disabled={mutation.isPending}
                >
                  {mutation.isPending ? "Signing in…" : "Sign in"}
                  <ArrowRight className="ml-1 h-4 w-4 transition-transform group-hover:translate-x-0.5" />
                </Button>
              </form>

              <div className="mt-6 text-center">
                <Link
                  to="/forgot-password"
                  className="text-xs text-muted-foreground hover:text-primary hover:underline"
                >
                  Forgot your password?
                </Link>
              </div>
            </div>

            <p className="mt-6 text-center text-[11px] text-muted-foreground">
              © {new Date().getFullYear()} Operion · All rights reserved
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
