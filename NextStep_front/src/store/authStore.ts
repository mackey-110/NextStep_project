import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import type { User } from "../types";

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

interface AuthActions {
  login: (user: User, token: string) => void;
  logout: () => void;
  setUser: (user: User) => void;
  setLoading: (loading: boolean) => void;
}

type AuthStore = AuthState & AuthActions;

export const useAuthStore = create<AuthStore>()(
  devtools(
    persist(
      (set) => ({
        // State
        user: null,
        token: null,
        isAuthenticated: false,
        isLoading: false,

        // Actions
        login: (user: User, token: string) => {
          localStorage.setItem("accessToken", token);
          set({ user, token, isAuthenticated: true }, false, "login");
        },

        logout: () => {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          set(
            { user: null, token: null, isAuthenticated: false },
            false,
            "logout"
          );
        },

        setUser: (user: User) => {
          set({ user }, false, "setUser");
        },

        setLoading: (loading: boolean) => {
          set({ isLoading: loading }, false, "setLoading");
        },
      }),
      {
        name: "auth-storage",
        partialize: (state) => ({
          user: state.user,
          token: state.token,
          isAuthenticated: state.isAuthenticated,
        }),
      }
    ),
    { name: "AuthStore" }
  )
);
