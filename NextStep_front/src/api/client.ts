import axios, { AxiosResponse } from "axios";
import {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User,
} from "../types";

// API 기본 설정
const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터: JWT 토큰 자동 첨부
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터: 토큰 만료 처리
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // 토큰 만료 시 로그아웃 처리
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// API 함수들
export const authApi = {
  // 로그인
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response: AxiosResponse<ApiResponse<AuthResponse>> =
      await apiClient.post("/auth/login", data);
    return response.data.data;
  },

  // 회원가입
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response: AxiosResponse<ApiResponse<AuthResponse>> =
      await apiClient.post("/auth/register", data);
    return response.data.data;
  },

  // 프로필 조회
  getProfile: async (): Promise<User> => {
    const response: AxiosResponse<ApiResponse<User>> = await apiClient.get(
      "/users/profile"
    );
    return response.data.data;
  },
};

export const healthApi = {
  // 헬스체크
  check: async () => {
    const response: AxiosResponse<ApiResponse<any>> = await apiClient.get(
      "/health"
    );
    return response.data.data;
  },

  // 버전 정보
  version: async () => {
    const response: AxiosResponse<ApiResponse<any>> = await apiClient.get(
      "/health/version"
    );
    return response.data.data;
  },
};

export default apiClient;
