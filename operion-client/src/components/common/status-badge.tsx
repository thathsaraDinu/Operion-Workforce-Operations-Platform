import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

type Tone = "default" | "success" | "warning" | "info" | "destructive" | "muted";

const toneClass: Record<Tone, string> = {
  default: "bg-primary/10 text-primary border-primary/20",
  success: "bg-success/15 text-success border-success/25",
  warning: "bg-warning/20 text-warning-foreground border-warning/40",
  info: "bg-info/15 text-info border-info/25",
  destructive: "bg-destructive/10 text-destructive border-destructive/25",
  muted: "bg-muted text-muted-foreground border-border",
};

const map: Record<string, Tone> = {
  ACTIVE: "success",
  INACTIVE: "muted",
  PRESENT: "success",
  ABSENT: "destructive",
  LATE: "warning",
  HALF_DAY: "info",
  PENDING: "warning",
  APPROVED: "success",
  REJECTED: "destructive",
  PLANNING: "info",
  COMPLETED: "success",
  CANCELLED: "muted",
  TODO: "muted",
  IN_PROGRESS: "info",
  DONE: "success",
  LOW: "muted",
  MEDIUM: "info",
  HIGH: "destructive",
  ADMIN: "destructive",
  HR: "info",
  MANAGER: "default",
  EMPLOYEE: "muted",
  ANNUAL: "info",
  SICK: "warning",
  CASUAL: "muted",
  MATERNITY: "default",
  UNPAID: "muted",
};

export function StatusBadge({ value }: { value?: string | null }) {
  if (!value) return <span className="text-muted-foreground">—</span>;
  const tone = map[value] ?? "default";
  return (
    <Badge
      variant="outline"
      className={cn("border font-medium capitalize", toneClass[tone])}
    >
      {value.replace(/_/g, " ").toLowerCase()}
    </Badge>
  );
}
