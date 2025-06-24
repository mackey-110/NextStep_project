-- ===================================================================
-- NextStep 프로젝트 데이터베이스 설계
-- 개발자 진로 학습 네비게이션 플랫폼
-- 생성일: 2025-06-19
-- ===================================================================

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS nextstep_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nextstep_db;

-- ===================================================================
-- 1. 사용자 관련 테이블
-- ===================================================================

-- 사용자 기본 정보
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    profile_image_url VARCHAR(500),
    user_role ENUM('GUEST', 'FREE_MEMBER', 'PREMIUM_MEMBER', 'MENTOR', 'ADMIN', 'SUPER_ADMIN') DEFAULT 'FREE_MEMBER',
    subscription_type ENUM('FREE', 'PREMIUM', 'PRO') DEFAULT 'FREE',
    subscription_start_date DATETIME,
    subscription_end_date DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at DATETIME,
    
    INDEX idx_email (email),
    INDEX idx_user_role (user_role),
    INDEX idx_subscription_type (subscription_type)
);

-- 사용자 프로필 (온보딩 설문 결과)
CREATE TABLE user_profiles (
    profile_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    
    -- 온보딩 설문 결과
    programming_languages JSON, -- ["Python", "JavaScript", "Java"]
    interest_fields JSON,       -- ["backend", "frontend", "data_science"]
    current_level ENUM('COMPLETE_BEGINNER', 'BASIC_COMPLETED', 'PROJECT_EXPERIENCE', 'WORK_EXPERIENCE'),
    learning_goals JSON,        -- ["job_preparation", "career_change", "side_project"]
    learning_style JSON,        -- {"theory_first": true, "video_preferred": true}
    
    -- 학습 통계
    total_study_hours INT DEFAULT 0,
    current_streak_days INT DEFAULT 0,
    max_streak_days INT DEFAULT 0,
    completed_roadmaps_count INT DEFAULT 0,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_current_level (current_level)
);

-- 멘토 정보
CREATE TABLE mentor_profiles (
    mentor_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    
    -- 멘토 자격 정보
    expertise_areas JSON,       -- ["backend", "frontend", "devops"]
    years_of_experience INT,
    company VARCHAR(100),
    position VARCHAR(100),
    portfolio_url VARCHAR(500),
    linkedin_url VARCHAR(500),
    github_url VARCHAR(500),
    
    -- 멘토링 정보
    hourly_rate DECIMAL(10,2),
    is_available BOOLEAN DEFAULT TRUE,
    mentor_rating DECIMAL(3,2) DEFAULT 0.00,
    total_mentoring_hours INT DEFAULT 0,
    total_reviews INT DEFAULT 0,
    
    -- 승인 정보
    approval_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    approved_at DATETIME,
    approved_by BIGINT,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_expertise_areas (expertise_areas),
    INDEX idx_approval_status (approval_status)
);

-- ===================================================================
-- 2. 로드맵 관련 테이블
-- ===================================================================

-- 로드맵 템플릿
CREATE TABLE roadmap_templates (
    template_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    programming_language VARCHAR(50),
    field_category VARCHAR(50),      -- "backend", "frontend", "data_science"
    difficulty_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'),
    estimated_hours INT,
    thumbnail_url VARCHAR(500),
    
    -- 메타데이터
    tags JSON,                       -- ["python", "django", "api"]
    prerequisites JSON,              -- ["basic_python", "html_css"]
    learning_outcomes JSON,          -- ["can_build_api", "understand_mvc"]
    
    -- 통계
    usage_count INT DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    total_reviews INT DEFAULT 0,
    
    created_by BIGINT,
    is_official BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    INDEX idx_language_field (programming_language, field_category),
    INDEX idx_difficulty (difficulty_level),
    INDEX idx_tags (tags),
    FULLTEXT idx_search (title, description)
);

-- 로드맵 단계
CREATE TABLE roadmap_steps (
    step_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    
    title VARCHAR(200) NOT NULL,
    description TEXT,
    estimated_hours INT,
    difficulty_level ENUM('EASY', 'MEDIUM', 'HARD'),
    
    -- 학습 자료
    learning_resources JSON,         -- [{"type": "video", "url": "...", "title": "..."}]
    practice_projects JSON,          -- [{"title": "...", "description": "...", "github_url": "..."}]
    
    -- 전제조건
    prerequisite_steps JSON,         -- [step_id, step_id]
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (template_id) REFERENCES roadmap_templates(template_id) ON DELETE CASCADE,
    INDEX idx_template_order (template_id, step_order),
    FULLTEXT idx_search (title, description)
);

-- 사용자별 개인 로드맵
CREATE TABLE user_roadmaps (
    user_roadmap_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    
    title VARCHAR(200) NOT NULL,     -- 사용자가 커스터마이징한 제목
    custom_description TEXT,         -- 사용자 추가 설명
    
    -- 진행 상태
    status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'PAUSED') DEFAULT 'NOT_STARTED',
    progress_percentage DECIMAL(5,2) DEFAULT 0.00,
    started_at DATETIME,
    completed_at DATETIME,
    estimated_completion_date DATETIME,
    
    -- 개인화 설정
    custom_steps JSON,               -- 사용자가 추가/수정한 단계들
    daily_study_goal_hours INT DEFAULT 1,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES roadmap_templates(template_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

-- 사용자 학습 진도
CREATE TABLE user_step_progress (
    progress_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_roadmap_id BIGINT NOT NULL,
    step_id BIGINT NOT NULL,
    
    status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'NOT_STARTED',
    progress_percentage DECIMAL(5,2) DEFAULT 0.00,
    
    started_at DATETIME,
    completed_at DATETIME,
    study_hours DECIMAL(5,2) DEFAULT 0.00,
    
    -- 사용자 노트
    user_notes TEXT,
    completed_projects JSON,         -- 완료한 프로젝트 정보
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_roadmap_id) REFERENCES user_roadmaps(user_roadmap_id) ON DELETE CASCADE,
    FOREIGN KEY (step_id) REFERENCES roadmap_steps(step_id),
    UNIQUE KEY unique_user_step (user_roadmap_id, step_id),
    INDEX idx_status (status)
);

-- ===================================================================
-- 3. 검색 및 콘텐츠 테이블
-- ===================================================================

-- 학습 콘텐츠 (강의, 아티클, 도구 등)
CREATE TABLE learning_contents (
    content_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    content_type ENUM('COURSE', 'ARTICLE', 'TOOL', 'PROJECT', 'BOOK'),
    content_url VARCHAR(1000),
    
    -- 분류
    programming_language VARCHAR(50),
    category VARCHAR(100),
    tags JSON,
    difficulty_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'),
    
    -- 메타데이터
    author VARCHAR(100),
    duration_minutes INT,           -- 강의 시간 (분)
    price DECIMAL(10,2) DEFAULT 0.00,
    is_free BOOLEAN DEFAULT TRUE,
    language VARCHAR(10) DEFAULT 'ko', -- 'ko', 'en'
    
    -- 통계
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    bookmark_count INT DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    review_count INT DEFAULT 0,
    
    -- 관리
    created_by BIGINT,
    is_approved BOOLEAN DEFAULT FALSE,
    approved_by BIGINT,
    approved_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    FOREIGN KEY (approved_by) REFERENCES users(user_id),
    INDEX idx_type_category (content_type, category),
    INDEX idx_language_difficulty (programming_language, difficulty_level),
    INDEX idx_tags (tags),
    FULLTEXT idx_search (title, description, author)
);

-- 검색 로그
CREATE TABLE search_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    search_query VARCHAR(500) NOT NULL,
    search_filters JSON,            -- 적용된 필터 정보
    results_count INT,
    clicked_result_id BIGINT,       -- 클릭한 결과
    search_session_id VARCHAR(100), -- 세션별 검색 그룹핑
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_query (user_id, search_query),
    INDEX idx_search_session (search_session_id),
    INDEX idx_created_at (created_at)
);

-- ===================================================================
-- 4. AI 코치 관련 테이블
-- ===================================================================

-- AI 대화 세션
CREATE TABLE ai_chat_sessions (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_uuid VARCHAR(36) UNIQUE NOT NULL,
    
    title VARCHAR(200),             -- 대화 주제
    context_roadmap_id BIGINT,      -- 관련 로드맵
    context_step_id BIGINT,         -- 관련 단계
    
    status ENUM('ACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
    total_messages INT DEFAULT 0,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (context_roadmap_id) REFERENCES user_roadmaps(user_roadmap_id),
    FOREIGN KEY (context_step_id) REFERENCES roadmap_steps(step_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

-- AI 대화 메시지
CREATE TABLE ai_chat_messages (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    
    message_type ENUM('USER', 'AI') NOT NULL,
    content TEXT NOT NULL,
    
    -- AI 메시지 메타데이터
    ai_model VARCHAR(50),           -- "gpt-4", "gpt-3.5-turbo"
    tokens_used INT,
    response_time_ms INT,
    
    -- 사용자 피드백
    user_rating ENUM('HELPFUL', 'NOT_HELPFUL'),
    user_feedback TEXT,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (session_id) REFERENCES ai_chat_sessions(session_id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at)
);

-- AI 이용 제한 추적
CREATE TABLE ai_usage_limits (
    usage_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    
    usage_date DATE NOT NULL,
    message_count INT DEFAULT 0,
    tokens_used INT DEFAULT 0,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, usage_date),
    INDEX idx_usage_date (usage_date)
);

-- ===================================================================
-- 5. 대시보드 및 통계 테이블
-- ===================================================================

-- 학습 활동 로그
CREATE TABLE learning_activities (
    activity_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    
    activity_type ENUM('ROADMAP_START', 'STEP_COMPLETE', 'STUDY_SESSION', 'AI_QUESTION', 'SEARCH'),
    target_id BIGINT,              -- 관련 객체 ID (로드맵, 단계 등)
    target_type VARCHAR(50),       -- 'roadmap', 'step', 'content'
    
    duration_minutes INT,          -- 활동 시간
    metadata JSON,                 -- 추가 정보
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_activity (user_id, activity_type),
    INDEX idx_created_at (created_at),
    INDEX idx_target (target_type, target_id)
);

-- 일별 학습 통계
CREATE TABLE daily_study_stats (
    stat_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    study_date DATE NOT NULL,
    
    total_study_minutes INT DEFAULT 0,
    completed_steps INT DEFAULT 0,
    ai_questions_asked INT DEFAULT 0,
    searches_performed INT DEFAULT 0,
    
    streak_day_number INT DEFAULT 0, -- 연속 학습 일수
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, study_date),
    INDEX idx_study_date (study_date)
);

-- 실시간 알림
CREATE TABLE notifications (
    notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    
    type ENUM('STEP_COMPLETE', 'GOAL_ACHIEVED', 'STREAK_MILESTONE', 'NEW_CONTENT', 'MENTORING_MATCH'),
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    
    action_url VARCHAR(500),       -- 클릭 시 이동할 URL
    is_read BOOLEAN DEFAULT FALSE,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    read_at DATETIME,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_unread (user_id, is_read),
    INDEX idx_created_at (created_at)
);

-- ===================================================================
-- 6. 결제 및 수익 테이블
-- ===================================================================

-- 구독 결제 이력
CREATE TABLE subscription_payments (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    
    subscription_type ENUM('PREMIUM', 'PRO'),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'KRW',
    
    payment_method VARCHAR(50),    -- 'card', 'account', 'kakao_pay'
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'),
    
    billing_period_start DATE,
    billing_period_end DATE,
    
    -- 외부 결제 시스템 연동
    external_payment_id VARCHAR(100),
    payment_gateway VARCHAR(50),   -- 'toss', 'iamport', 'stripe'
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_billing_period (billing_period_start, billing_period_end)
);

-- 멘토 수익 관리
CREATE TABLE mentor_earnings (
    earning_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mentor_id BIGINT NOT NULL,
    
    earning_type ENUM('MENTORING', 'ROADMAP_SALE', 'LIVE_SESSION'),
    amount DECIMAL(10,2) NOT NULL,
    commission_rate DECIMAL(5,2),  -- 플랫폼 수수료율
    net_amount DECIMAL(10,2),      -- 실제 지급액
    
    source_id BIGINT,              -- 멘토링 세션 ID 등
    source_type VARCHAR(50),
    
    payout_status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
    payout_date DATE,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (mentor_id) REFERENCES mentor_profiles(mentor_id),
    INDEX idx_mentor_id (mentor_id),
    INDEX idx_payout_status (payout_status),
    INDEX idx_created_at (created_at)
);

-- ===================================================================
-- 7. 성능 최적화를 위한 추가 인덱스
-- ===================================================================

-- 복합 인덱스 생성
CREATE INDEX idx_users_role_subscription ON users(user_role, subscription_type);
CREATE INDEX idx_contents_approved_active ON learning_contents(is_approved, is_active);
CREATE INDEX idx_activities_user_date ON learning_activities(user_id, created_at);
CREATE INDEX idx_roadmaps_user_status ON user_roadmaps(user_id, status);

-- ===================================================================
-- 8. 샘플 데이터 삽입
-- ===================================================================

-- 관리자 계정 생성
INSERT INTO users (email, password, nickname, full_name, user_role, email_verified) VALUES 
('admin@nextstep.io', '$2a$10$encrypted_password', 'admin', '관리자', 'SUPER_ADMIN', TRUE);

-- 기본 로드맵 템플릿 생성
INSERT INTO roadmap_templates (title, description, programming_language, field_category, difficulty_level, estimated_hours, is_official, created_by) VALUES 
('Python 백엔드 개발자 로드맵', 'Python Django를 활용한 백엔드 개발자가 되기 위한 완전 가이드', 'Python', 'backend', 'BEGINNER', 120, TRUE, 1),
('JavaScript 프론트엔드 로드맵', 'React를 활용한 모던 프론트엔드 개발자 로드맵', 'JavaScript', 'frontend', 'BEGINNER', 100, TRUE, 1),
('데이터 사이언스 입문 로드맵', 'Python을 활용한 데이터 분석 및 머신러닝 입문', 'Python', 'data_science', 'INTERMEDIATE', 150, TRUE, 1);

-- ===================================================================
-- 데이터베이스 설계 완료
-- 
-- 주요 특징:
-- 1. 사용자 등급별 권한 시스템 (GUEST ~ SUPER_ADMIN)
-- 2. 개인화된 로드맵 추천 및 진도 관리
-- 3. AI 코치 대화 시스템
-- 4. 실시간 대시보드 및 통계
-- 5. 검색 최적화 (Elasticsearch 연동 준비)
-- 6. 멘토링 및 수익 시스템
-- 7. 확장 가능한 JSON 필드 활용
-- ===================================================================