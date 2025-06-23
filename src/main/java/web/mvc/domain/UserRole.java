package web.mvc.domain;

import lombok.Getter;

/**
 * 사용자 등급 열거형
 * 권한 레벨: GUEST < USER < PREMIUM < MENTOR < ADMIN < OPERATOR
 */
@Getter
public enum UserRole {

    /**
     * 게스트 - 비회원 (미리보기만)
     */
    GUEST("게스트", 0),

    /**
     * 일반 회원 - 기본 무료 (제한적 기능)
     */
    USER("일반 회원", 1),

    /**
     * 프리미엄 회원 - 유료 구독 (₩19,900/월)
     */
    PREMIUM("프리미엄 회원", 2),

    /**
     * 멘토 - 로드맵 제작, 멘토링 서비스 (수익 모델)
     */
    MENTOR("멘토", 3),

    /**
     * 관리자 - 콘텐츠 및 사용자 관리
     */
    ADMIN("관리자", 4),

    /**
     * 운영자 - 시스템 전체 관리
     */
    OPERATOR("운영자", 5);

    private final String description;
    private final int level;

    UserRole(String description, int level) {
        this.description = description;
        this.level = level;
    }

    /**
     * 현재 역할이 대상 역할보다 높은 권한을 가지는지 확인
     */
    public boolean hasHigherAuthorityThan(UserRole targetRole) {
        return this.level > targetRole.level;
    }

    /**
     * 현재 역할이 대상 역할과 같거나 높은 권한을 가지는지 확인
     */
    public boolean hasAuthorityOf(UserRole targetRole) {
        return this.level >= targetRole.level;
    }
}
