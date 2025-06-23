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
☕ Language      Java 17
🏗️ Build Tool    Maven
🐳 Container     Docker (예정)
🧪 Testing       JUnit 5, MockMvc
📊 Monitoring    Spring Actuator (예정)
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

---

## 🚀 시작하기

### 📋 사전 요구사항

```bash
☕ Java 17+
🗄️ MySQL 8.0+
🔧 Maven 3.6+
🛠️ IDE (IntelliJ IDEA 권장)
```

### 🔧 설치 및 실행

1. **레포지토리 클론**

```bash
git clone https://github.com/your-username/NextStep_project.git
cd NextStep_project
```

2. **MySQL 데이터베이스 생성**

```sql
CREATE DATABASE nextstep_dev;
CREATE USER 'nextstep_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON nextstep_dev.* TO 'nextstep_user'@'localhost';
```

3. **설정 파일 수정**

```yaml
# src/main/resources/application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nextstep_dev
    username: your_username
    password: your_password
```

4. **애플리케이션 실행**

```bash
mvn spring-boot:run
```

5. **API 문서 확인**

```
🌐 Swagger UI: http://localhost:8080/swagger-ui.html
📋 API Docs: http://localhost:8080/v3/api-docs
💚 Health Check: http://localhost:8080/api/health
```

---

## 📅 개발 로드맵

### 🗓️ 4개월 개발 계획

| 월차        | 주요 기능                | 상태      |
| ----------- | ------------------------ | --------- |
| **Month 1** | 기본 플랫폼 구축         | 🔄 진행중 |
|             | • 회원가입/로그인 시스템 | ⏳        |
|             | • 로드맵 시스템 기초     | ⏳        |
|             | • 기본 CRUD API          | ⏳        |
| **Month 2** | 추천 알고리즘 구현       | ⏳ 대기   |
|             | • 사용자 선호도 수집     | ⏳        |
|             | • 협업 필터링            | ⏳        |
| **Month 3** | AI & 검색 시스템         | ⏳ 대기   |
|             | • OpenAI API 연동        | ⏳        |
|             | • Elasticsearch 셋업     | ⏳        |
| **Month 4** | 실시간 대시보드 & 마무리 | ⏳ 대기   |
|             | • WebSocket 실시간 기능  | ⏳        |
|             | • Chart.js 시각화        | ⏳        |
|             | • 배포 및 최적화         | ⏳        |

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
