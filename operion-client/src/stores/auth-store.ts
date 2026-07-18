import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";
import { TOKEN_STORAGE_KEY } from "@/lib/api/client";
import type { AuthUser, Role } from "@/lib/api/types";
import type { LoginResponse } from "@/lib/api/auth";

interface JwtPayload {
  sub?: string;
  email?: string;
  role?: Role;
  firstName?: string;
  lastName?: string;
  id?: number;
  userId?: number;
  exp?: number;
  [k: string]: unknown;
}

function decodeJwt(token: string): JwtPayload | null {
  try {
    const [, payload] = token.split(".");
    if (!payload) return null;
    const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
    const padded = normalized + "===".slice((normalized.length + 3) % 4);
    const json =
      typeof atob === "function"
        ? atob(padded)
        : Buffer.from(padded, "base64").toString("utf8");
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
}

function userFromToken(token: string): AuthUser | null {
  const p = decodeJwt(token);
  if (!p) return null;
  const email = p.email ?? p.sub ?? "";
  const role = (p.role as Role) ?? "EMPLOYEE";
  const id = Number(p.id ?? p.userId ?? 0);
  return {
    id,
    email,
    firstName: p.firstName ?? email.split("@")[0] ?? "User",
    lastName: p.lastName ?? "",
    role,
  };
}

interface AuthState {
  token: string | null;
  user: AuthUser | null;
  /** Persist the JWT alone; identity is derived from JWT claims (fallback path). */
  setToken: (token: string) => void;
  /** Preferred: persist the JWT together with the identity returned by /auth/login. */
  setSession: (resp: LoginResponse) => void;
  logout: () => void;
  isAuthenticated: () => boolean;
  hasRole: (...roles: Role[]) => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      user: null,
      setToken: (token) => {
        if (typeof window !== "undefined") {
          window.localStorage.setItem(TOKEN_STORAGE_KEY, token);
        }
        set({ token, user: userFromToken(token) });
      },
      setSession: (resp) => {
        if (typeof window !== "undefined") {
          window.localStorage.setItem(TOKEN_STORAGE_KEY, resp.token);
        }
        // Use server-supplied identity when present; fall back to JWT claims for name fields.
        const claims = decodeJwt(resp.token);
        const emailLocal = resp.email.split("@")[0] ?? "User";
        const user: AuthUser = {
          id: resp.id,
          email: resp.email,
          role: resp.role,
          firstName: claims?.firstName ?? emailLocal,
          lastName: claims?.lastName ?? "",
        };
        set({ token: resp.token, user });
      },
      logout: () => {
        if (typeof window !== "undefined") {
          window.localStorage.removeItem(TOKEN_STORAGE_KEY);
        }
        set({ token: null, user: null });
      },
      isAuthenticated: () => !!get().token,
      hasRole: (...roles) => {
        const u = get().user;
        return !!u && roles.includes(u.role);
      },
    }),
    {
      name: "operion.auth",
      storage: createJSONStorage(() =>
        typeof window !== "undefined"
          ? window.localStorage
          : {
              getItem: () => null,
              setItem: () => {},
              removeItem: () => {},
            },
      ),
      partialize: (s) => ({ token: s.token, user: s.user }),
    },
  ),
);
