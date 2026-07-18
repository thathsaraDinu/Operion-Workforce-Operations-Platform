import { Button } from "@/components/ui/button";
import { ChevronLeft, ChevronRight } from "lucide-react";

interface PaginationBarProps {
  page: number;
  totalPages: number;
  totalElements: number;
  onPageChange: (page: number) => void;
}

export function PaginationBar({
  page,
  totalPages,
  totalElements,
  onPageChange,
}: PaginationBarProps) {
  return (
    <div className="flex items-center justify-between border-t border-border/60 px-4 py-3 text-sm text-muted-foreground">
      <div>
        {totalElements} {totalElements === 1 ? "record" : "records"}
      </div>
      <div className="flex items-center gap-2">
        <span>
          Page {Math.min(page + 1, Math.max(totalPages, 1))} of {Math.max(totalPages, 1)}
        </span>
        <Button
          variant="outline"
          size="icon"
          className="h-8 w-8"
          disabled={page <= 0}
          onClick={() => onPageChange(page - 1)}
        >
          <ChevronLeft className="h-4 w-4" />
        </Button>
        <Button
          variant="outline"
          size="icon"
          className="h-8 w-8"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
        >
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>
    </div>
  );
}
