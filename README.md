# 🚀 NextStep - 개발자 진로 학습 네비게이션 플랫폼

<div align="center">

![NextStep Logo](https://img.shields.io/badge/NextStep-v1.0.0-blue?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen?style=for-the-badge&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)

**"프로그래밍 기초 이후, 이제 뭘 해야 하지?"**  
초급 개발자들의 진로 고민을 해결하는 AI 기반 학습 네비게이션 플랫폼

[🌐 데모 사이트](#) | [📖 API 문서](#) | [📋 기능 명세서](#)

</div>

---

## 📋 프로젝트 개요

### 🎯 목적

프로그래밍 기초를 완료한 초급 개발자들이 겪는 **"이제 뭘 해야 하지?"** 라는 고민을 해결하기 위한 맞춤형 학습 경로 제공 플랫폼입니다.

### 🎪 핵심 가치

- **🎯 개인화된 학습 경로**: AI 기반 맞춤형 로드맵 생성
- **🔍 스마트 검색**: Elasticsearch 기반 의미론적 검색
- **🤖 AI 학습 코치**: OpenAI API를 활용한 실시간 학습 지원
- **📊 실시간 대시보드**: WebSocket 기반 학습 진도 시각화

---

## 🛠 기술 스택

### 프론트엔드 (Frontend)

```
⚛️ React & Build    React 19.1 + TypeScript + Vite 6
🎨 Styling          Styled Components (CSS-in-JS)
🗺️ Routing          React Router v7
📊 State Mgmt       Zustand + React Query (TanStack)
🌐 HTTP Client      Axios
📋 Forms & Valid    React Hook Form + Zod
🔔 Notifications    React Hot Toast
� Charts           Chart.js + React Chart.js 2
🔍 Icons            Lucide React
📱 PWA              Vite PWA Plugin
```

### 백엔드 (Backend)

```
🔧 Framework     Spring Boot 3.5.3
🗄️ Database      MySQL 8.0 + JPA/Hibernate
🔐 Security      Spring Security + JWT
🔍 Search        Elasticsearch (예정)
🤖 AI            OpenAI API (예정)
⚡ Cache         Redis (예정)
🔌 Real-time     WebSocket
📖 API Docs      SpringDoc OpenAPI (Swagger)
```

### 개발 도구 (Development)

```
☕ Backend       Java 17 + Maven
� Frontend      Node.js 18+ + pnpm
🐳 Container     Docker + Docker Compose (예정)
🧪 Testing       JUnit 5, MockMvc (BE) + Vitest, RTL (FE)
📊 Monitoring    Spring Actuator (예정)
🎨 Code Quality  ESLint + Prettier + Husky
📋 API Client    OpenAPI Generator (자동 생성)
```

---

## 👥 사용자 등급 시스템

| 등급            | 설명          | 권한 레벨 | 특징                       |
| --------------- | ------------- | --------- | -------------------------- |
| 🌟 **OPERATOR** | 운영자        | 5         | 시스템 전체 관리           |
| 🛡️ **ADMIN**    | 관리자        | 4         | 콘텐츠 및 사용자 관리      |
| 👨‍🏫 **MENTOR**   | 멘토          | 3         | 로드맵 제작, 멘토링 서비스 |
| 💎 **PREMIUM**  | 프리미엄 회원 | 2         | 유료 구독 (₩19,900/월)     |
| 👤 **USER**     | 일반 회원     | 1         | 기본 무료 (제한적 기능)    |
| 👋 **GUEST**    | 게스트        | 0         | 비회원 (미리보기만)        |

---

## ⭐ 4대 핵심 기능

### 1️⃣ 🎯 추천 알고리즘

```
• 온보딩 설문 시스템 (언어, 분야, 수준, 목표, 학습 스타일)
• 개인화된 로드맵 생성 엔진
• 협업 필터링 기반 추천 시스템
• 학습 패턴 분석 및 최적화
```

### 2️⃣ 🔍 스마트 검색

```
• 전체 텍스트 검색 및 고급 필터링
• Elasticsearch 기반 의미론적 검색
• 언어별/분야별/난이도별 분류
• 개인화된 검색 결과 제공
```

### 3️⃣ 🤖 AI 학습 코치

```
• 컨텍스트 인식 Q&A 시스템
• 코드 리뷰 및 피드백
• 개인화된 학습 분석 및 코칭
• 동기부여 및 학습 습관 형성 지원
```

### 4️⃣ 📊 실시간 대시보드

```
• 학습 진도 시각화 (진도율, 목표 대비 성과)
• 스트릭 및 성취도 추적
• 또래 비교 및 랭킹 시스템
• WebSocket 기반 실시간 알림
```

---

## 🏗️ 프로젝트 구조

### 백엔드 (Backend)

```
NextStep_project/
├── 📁 src/main/java/web/mvc/
│   ├── 📁 config/              # 설정 클래스
│   │   ├── SwaggerConfig.java  # API 문서화 설정
│   │   ├── WebConfig.java      # CORS 및 웹 설정
│   │   └── JwtProperties.java  # JWT 설정
│   ├── 📁 controller/          # REST API 컨트롤러
│   ├── 📁 service/             # 비즈니스 로직
│   ├── 📁 repository/          # 데이터 액세스 레이어
│   ├── 📁 domain/              # 엔티티 및 도메인 모델
│   ├── 📁 dto/                 # 요청/응답 DTO
│   ├── 📁 security/            # 보안 관련
│   └── 📁 exception/           # 예외 처리
├── 📁 src/main/resources/
│   ├── application.yml         # 기본 설정
│   ├── application-dev.yml     # 개발환경 설정
│   └── application-prod.yml    # 운영환경 설정
└── 📄 pom.xml                  # Maven 의존성 관리
```

### 프론트엔드 (Frontend)

```
NextStep_front/
├── 📁 src/
│   ├── 📁 components/          # React 컴포넌트
│   │   ├── ui/                # 기본 UI 컴포넌트
│   │   ├── layout/            # 레이아웃 컴포넌트
│   │   └── features/          # 기능별 컴포넌트
│   ├── 📁 pages/              # 페이지 컴포넌트
│   ├── 📁 hooks/              # 커스텀 훅
│   ├── 📁 store/              # Zustand 상태 관리
│   ├── 📁 api/                # Axios API 클라이언트
│   ├── 📁 types/              # TypeScript 타입 정의
│   ├── 📁 utils/              # 유틸리티 함수
│   └── 📁 styles/             # 스타일 관련
├── 📄 package.json            # npm 의존성 관리
├── 📄 vite.config.ts          # Vite 설정
└── 📄 tsconfig.json           # TypeScript 설정
```

---

## 🚀 시작하기

### 📋 사전 요구사항

```bash
# 백엔드
☕ Java 17+
🗄️ MySQL 8.0+
🔧 Maven 3.6+

# 프론트엔드
🌐 Node.js 18+
� npm 또는 pnpm

# 개발 도구
�🛠️ IDE (IntelliJ IDEA, VS Code 권장)
```

### 🔧 설치 및 실행

#### 1️⃣ **레포지토리 클론**

```bash
git clone https://github.com/your-username/NextStep_project.git
cd NextStep_project
```

#### 2️⃣ **백엔드 설정**

```sql
# MySQL 데이터베이스 생성
CREATE DATABASE nextstep_dev;
CREATE USER 'nextstep_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON nextstep_dev.* TO 'nextstep_user'@'localhost';
```

```yaml
# src/main/resources/application-dev.yml 수정
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nextstep_dev
    username: your_username
    password: your_password
```

```bash
# 백엔드 실행
mvn spring-boot:run
```

#### 3️⃣ **프론트엔드 설정**

```bash
# 프론트엔드 폴더로 이동
cd NextStep_front

# 의존성 설치
npm install
# 또는
pnpm install

# 개발 서버 실행
npm run dev
# 또는
pnpm dev
```

#### 4️⃣ **접속 확인**

```
# 백엔드 API
🌐 Swagger UI: http://localhost:8080/swagger-ui.html
📋 API Docs: http://localhost:8080/v3/api-docs
💚 Health Check: http://localhost:8080/api/health

# 프론트엔드
🖥️ Web App: http://localhost:5173
📱 모바일뷰: http://localhost:5173 (반응형)
```

---

## 📊 프로젝트 진행 상황 (2024.06.24 업데이트)

### ✅ 1단계 완료 (백엔드 기초 설정) - 2024.06.19

- ✅ Spring Boot 프로젝트 초기 설정
- ✅ 데이터베이스 연결 및 JPA 설정
- ✅ 기본 패키지 구조 설계
- ✅ 환경별 설정 파일 구성 (dev, prod)
- ✅ JWT, Swagger, Validation 등 의존성 추가
- ✅ 기본 설정 클래스 및 예외 처리 구현

### ✅ 2단계 완료 (엔티티 & Repository 구현) - 2024.06.24

#### ✅ 완료된 엔티티 (Entity)

**핵심 엔티티 (6개)**

- ✅ **User** - 사용자 기본 정보
- ✅ **UserProfile** - 사용자 프로필 및 온보딩 정보
- ✅ **RoadMapTemplate** - 로드맵 템플릿
- ✅ **RoadMapStep** - 로드맵 단계
- ✅ **UserRoadMap** - 사용자별 로드맵 진행상황
- ✅ **UserStepProgress** - 사용자별 단계 진행상황

**확장 엔티티 (9개)**

- ✅ **MentorProfile** - 멘토 프로필 및 자격 정보
- ✅ **AiChatSession** - AI 채팅 세션
- ✅ **AiChatMessage** - AI 채팅 메시지
- ✅ **AiUsageLimit** - AI 사용량 제한 추적
- ✅ **LearningContent** - 학습 콘텐츠 (강의, 아티클 등)
- ✅ **LearningActivity** - 학습 활동 로그
- ✅ **DailyStudyStat** - 일별 학습 통계
- ✅ **SearchLog** - 검색 로그
- ✅ **Notification** - 실시간 알림

**결제 엔티티 (2개)**

- ✅ **SubscriptionPayment** - 구독 결제 이력
- ✅ **MentorEarning** - 멘토 수익 관리

**Enum 클래스 (3개)**

- ✅ **UserRole** - 사용자 역할 (GUEST ~ SUPER_ADMIN)
- ✅ **SubscriptionType** - 구독 타입 (FREE, PREMIUM, PRO)
- ✅ **CommonEnums** - 공통 열거형 모음

#### ✅ 완료된 Repository 인터페이스 (11개)

- ✅ **UserRepository** - 사용자 관련 쿼리 (23개 메서드)
- ✅ **RoadMapTemplateRepository** - 로드맵 템플릿 쿼리 (20개 메서드)
- ✅ **UserRoadMapRepository** - 사용자 로드맵 쿼리 (15개 메서드)
- ✅ **MentorProfileRepository** - 멘토 프로필 쿼리 (18개 메서드)
- ✅ **AiChatSessionRepository** - AI 세션 쿼리 (20개 메서드)
- ✅ **AiChatMessageRepository** - AI 메시지 쿼리 (25개 메서드)
- ✅ **LearningContentRepository** - 학습 콘텐츠 쿼리 (22개 메서드)
- ✅ **NotificationRepository** - 알림 쿼리 (18개 메서드)
- ✅ **SubscriptionPaymentRepository** - 결제 쿼리 (20개 메서드)
- ✅ **MentorEarningRepository** - 멘토 수익 쿼리 (22개 메서드)
- ✅ **기타 Repository** - 사용량, 통계, 검색 관련 Repository

### 🔄 3단계 진행 예정 (Service & Controller 구현)

#### 📋 다음 작업 목록

1. **Service 계층 구현**

   - UserService, AuthService (인증/인가)
   - RoadMapService, ProgressService (로드맵/진도관리)
   - AiChatService, ContentService (AI/콘텐츠)
   - PaymentService, NotificationService (결제/알림)

2. **Controller 계층 구현**

   - REST API 엔드포인트 설계
   - DTO 클래스 생성 및 매핑
   - API 문서화 (Swagger)

3. **Security 구현**

   - JWT 인증/인가 구현
   - 역할 기반 접근 제어 (RBAC)
   - API 보안 설정

4. **테스트 코드 작성**
   - Unit Test (JUnit 5 + Mockito)
   - Integration Test
   - API 테스트 (TestContainers)

### 📋 4단계 예정 (고급 기능 구현)

- [ ] AI 채팅 시스템 구현 (OpenAI API)
- [ ] 실시간 알림 (WebSocket)
- [ ] 검색 시스템 (Elasticsearch)
- [ ] 결제 시스템 연동 (Toss Payments)
- [ ] 배포 및 CI/CD 파이프라인

---

## 📚 API 명세서

### 🏥 헬스체크

```http
GET /api/health              # 서버 상태 확인
GET /api/health/version      # API 버전 정보
```

### 👤 사용자 관리 (예정)

```http
POST /api/auth/register      # 회원가입
POST /api/auth/login         # 로그인
GET  /api/users/profile      # 프로필 조회
PUT  /api/users/profile      # 프로필 수정
```

### 🗺️ 로드맵 관리 (예정)

```http
GET  /api/roadmaps           # 로드맵 목록 조회
POST /api/roadmaps           # 로드맵 생성
GET  /api/roadmaps/{id}      # 로드맵 상세 조회
PUT  /api/roadmaps/{id}      # 로드맵 수정
```

### 📊 학습 진도 (예정)

```http
GET  /api/progress           # 학습 진도 조회
POST /api/progress           # 진도 업데이트
GET  /api/dashboard          # 대시보드 데이터
```

---

## 🤝 기여하기

### 🔀 브랜치 전략

```
main        # 운영 환경 배포 브랜치
develop     # 개발 통합 브랜치
feature/*   # 기능 개발 브랜치
hotfix/*    # 긴급 버그 수정 브랜치
```

### 📝 커밋 컨벤션

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드
chore: 빌드 설정 변경
```

---

## 📞 연락처

- **📧 Email**: contact@nextstep.io
- **🌐 Website**: https://nextstep.io
- **📋 Issues**: [GitHub Issues](https://github.com/your-username/NextStep_project/issues)

---

## 📄 라이선스

이 프로젝트는 [MIT License](LICENSE) 하에 배포됩니다.

---

<div align="center">

**⭐ 이 프로젝트가 도움이 되셨다면 스타를 눌러주세요! ⭐**

Made with ❤️ by NextStep Team

</div>
