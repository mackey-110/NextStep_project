package web.mvc.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI 채팅 세션 엔티티
 * 사용자와 AI 코치 간의 대화 세션을 관리
 */
@Entity
@Table(name = "ai_chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_uuid", unique = true, nullable = false, length = 36)
    private String sessionUuid;

    @Column(name = "title", length = 200)
    private String title;

    // ===== 컨텍스트 정보 =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_roadmap_id")
    private UserRoadMap contextRoadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_step_id")
    private RoadMapStep contextStep;

    // ===== 세션 상태 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "total_messages")
    @PositiveOrZero
    @Builder.Default
    private Integer totalMessages = 0;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 연관관계 =====

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AiChatMessage> messages = new ArrayList<>();

    // ===== 생성자 및 빌더 설정 =====

    @PrePersist
    public void prePersist() {
        if (this.sessionUuid == null) {
            this.sessionUuid = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 세션 제목 자동 생성 (첫 번째 사용자 메시지 기반)
     */
    public void generateTitleFromFirstMessage(String firstUserMessage) {
        if (this.title == null && firstUserMessage != null && !firstUserMessage.trim().isEmpty()) {
            String truncated = firstUserMessage.length() > 50
                    ? firstUserMessage.substring(0, 47) + "..."
                    : firstUserMessage;
            this.title = truncated;
        }
    }

    /**
     * 메시지 추가
     */
    public void addMessage(AiChatMessage message) {
        this.messages.add(message);
        message.setSession(this);
        this.totalMessages = this.messages.size();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 세션 종료
     */
    public void closeSession() {
        this.status = SessionStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 세션 활성화 여부 확인
     */
    public boolean isActive() {
        return SessionStatus.ACTIVE.equals(this.status);
    }

    /**
     * 컨텍스트 설정 (로드맵 기반)
     */
    public void setRoadmapContext(UserRoadMap roadmap) {
        this.contextRoadmap = roadmap;
        this.contextStep = null; // 로드맵 전체 컨텍스트로 설정
    }

    /**
     * 컨텍스트 설정 (단계 기반)
     */
    public void setStepContext(UserRoadMap roadmap, RoadMapStep step) {
        this.contextRoadmap = roadmap;
        this.contextStep = step;
    }

    /**
     * 컨텍스트 정보 확인
     */
    public boolean hasContext() {
        return this.contextRoadmap != null || this.contextStep != null;
    }

    /**
     * 컨텍스트 설명 반환
     */
    public String getContextDescription() {
        if (this.contextStep != null) {
            return String.format("로드맵: %s, 단계: %s",
                    this.contextRoadmap.getRoadmapTemplate().getTitle(),
                    this.contextStep.getTitle());
        } else if (this.contextRoadmap != null) {
            return String.format("로드맵: %s",
                    this.contextRoadmap.getRoadmapTemplate().getTitle());
        }
        return "일반 상담";
    }

    /**
     * 세션 상태 열거형
     */
    public enum SessionStatus {
        ACTIVE("활성"),
        CLOSED("종료");

        private final String description;

        SessionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
