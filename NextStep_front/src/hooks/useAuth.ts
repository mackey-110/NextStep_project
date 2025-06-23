import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { authApi, healthApi } from "../api/client";
import { useAuthStore } from "../store/authStore";
import type { LoginRequest, RegisterRequest } from "../types";

// 헬스체크 훅
export const useHealthCheck = () => {
  return useQuery({
    queryKey: ["health"],
    queryFn: healthApi.check,
    refetchInterval: 30000, // 30초마다 체크
  });
};

// 로그인 훅
export const useLogin = () => {
  const { login } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authApi.login,
    onSuccess: (data) => {
      login(data.user, data.token);
      queryClient.invalidateQueries({ queryKey: ["user"] });
    },
  });
};

// 회원가입 훅
export const useRegister = () => {
  const { login } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authApi.register,
    onSuccess: (data) => {
      login(data.user, data.token);
      queryClient.invalidateQueries({ queryKey: ["user"] });
    },
  });
};

// 프로필 조회 훅
export const useProfile = () => {
  const { isAuthenticated } = useAuthStore();

  return useQuery({
    queryKey: ["user", "profile"],
    queryFn: authApi.getProfile,
    enabled: isAuthenticated,
  });
};

// 로그아웃 훅
export const useLogout = () => {
  const { logout } = useAuthStore();
  const queryClient = useQueryClient();

  return () => {
    logout();
    queryClient.clear();
  };
};
