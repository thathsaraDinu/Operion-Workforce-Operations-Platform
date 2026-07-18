import { format, parseISO } from "date-fns";

export function fmtDate(d?: string | null, pattern = "MMM d, yyyy") {
  if (!d) return "—";
  try {
    return format(parseISO(d), pattern);
  } catch {
    return d;
  }
}

export function fmtDateTime(d?: string | null) {
  return fmtDate(d, "MMM d, yyyy · h:mm a");
}

export function fmtTime(d?: string | null) {
  return fmtDate(d, "h:mm a");
}

export function initials(first?: string, last?: string) {
  return `${first?.[0] ?? ""}${last?.[0] ?? ""}`.toUpperCase() || "?";
}
