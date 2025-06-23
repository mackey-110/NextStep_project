// 사용자 관련 타입
export interface User {
  id: number;
  email: string;
  username: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string;
}

export enum UserRole {
  GUEST = "GUEST",
  USER = "USER",
  PREMIUM = "PREMIUM",
  MENTOR = "MENTOR",
  ADMIN = "ADMIN",
  OPERATOR = "OPERATOR",
}

// 로드맵 관련 타입
export interface RoadMap {
  id: number;
  title: string;
  description: string;
  difficulty: Difficulty;
  estimatedTime: number;
  courses: Course[];
  createdBy: User;
  createdAt: string;
  updatedAt: string;
}

export interface Course {
  id: number;
  title: string;
  description: string;
  content: string;
  order: number;
  estimatedTime: number;
  roadMapId: number;
}

export enum Difficulty {
  BEGINNER = "BEGINNER",
  INTERMEDIATE = "INTERMEDIATE",
  ADVANCED = "ADVANCED",
}

// 학습 진도 관련 타입
export interface UserProgress {
  id: number;
  userId: number;
  courseId: number;
  completed: boolean;
  completedAt?: string;
  progress: number; // 0-100
}

// 설문 관련 타입
export interface Survey {
  id: number;
  userId: number;
  responses: SurveyResponse[];
  completedAt?: string;
}

export interface SurveyResponse {
  questionId: number;
  answer: string | string[];
}

// API 응답 형식
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode?: string;
}

// 인증 관련 타입
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: User;
}
