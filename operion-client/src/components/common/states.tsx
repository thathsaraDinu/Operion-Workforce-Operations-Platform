import { Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";

export function LoadingBlock({ label, className }: { label?: string; className?: string }) {
  return (
    <div
      className={cn(
        "flex min-h-[220px] items-center justify-center rounded-xl border border-dashed border-border/70 bg-card/40 text-sm text-muted-foreground",
        className,
      )}
    >
      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
      {label ?? "Loading…"}
    </div>
  );
}

export function EmptyState({
  title,
  description,
  action,
}: {
  title: string;
  description?: string;
  action?: React.ReactNode;
}) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 rounded-xl border border-dashed border-border/70 bg-card/40 px-6 py-14 text-center">
      <div className="text-base font-medium text-foreground">{title}</div>
      {description ? (
        <p className="max-w-sm text-sm text-muted-foreground">{description}</p>
      ) : null}
      {action}
    </div>
  );
}

export function ErrorState({ message }: { message: string }) {
  return (
    <div className="rounded-xl border border-destructive/30 bg-destructive/5 px-4 py-6 text-sm text-destructive">
      {message}
    </div>
  );
}
