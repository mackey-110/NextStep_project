package web.mvc.domain;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * AI 채팅 메시지 엔티티
 * AI와 사용자 간의 개별 메시지를 저장
 */
@Entity
@Table(name = "ai_chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AiChatSession session;

    // ===== 메시지 내용 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String content;

    // ===== AI 메시지 메타데이터 =====

    @Column(name = "ai_model", length = 50)
    private String aiModel;

    @Column(name = "tokens_used")
    @PositiveOrZero
    private Integer tokensUsed;

    @Column(name = "response_time_ms")
    @PositiveOrZero
    private Integer responseTimeMs;

    // ===== 사용자 피드백 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "user_rating")
    private UserRating userRating;

    @Column(name = "user_feedback", columnDefinition = "TEXT")
    private String userFeedback;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 사용자 메시지 생성
     */
    public static AiChatMessage createUserMessage(AiChatSession session, String content) {
        return AiChatMessage.builder()
                .session(session)
                .messageType(MessageType.USER)
                .content(content)
                .build();
    }

    /**
     * AI 메시지 생성
     */
    public static AiChatMessage createAiMessage(AiChatSession session, String content,
            String aiModel, Integer tokensUsed, Integer responseTimeMs) {
        return AiChatMessage.builder()
                .session(session)
                .messageType(MessageType.AI)
                .content(content)
                .aiModel(aiModel)
                .tokensUsed(tokensUsed)
                .responseTimeMs(responseTimeMs)
                .build();
    }

    /**
     * 사용자 피드백 설정
     */
    public void setUserFeedback(UserRating rating, String feedback) {
        this.userRating = rating;
        this.userFeedback = feedback;
    }

    /**
     * AI 메시지 여부 확인
     */
    public boolean isAiMessage() {
        return MessageType.AI.equals(this.messageType);
    }

    /**
     * 사용자 메시지 여부 확인
     */
    public boolean isUserMessage() {
        return MessageType.USER.equals(this.messageType);
    }

    /**
     * 피드백 존재 여부 확인
     */
    public boolean hasFeedback() {
        return this.userRating != null;
    }

    /**
     * 긍정적 피드백 여부 확인
     */
    public boolean isPositiveFeedback() {
        return UserRating.HELPFUL.equals(this.userRating);
    }

    /**
     * 메시지 길이 반환
     */
    public int getContentLength() {
        return this.content != null ? this.content.length() : 0;
    }

    /**
     * 메시지 타입 열거형
     */
    public enum MessageType {
        USER("사용자"),
        AI("AI");

        private final String description;

        MessageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 사용자 피드백 평가 열거형
     */
    public enum UserRating {
        HELPFUL("도움됨"),
        NOT_HELPFUL("도움안됨");

        private final String description;

        UserRating(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
