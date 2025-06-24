package web.mvc.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 검색 로그 엔티티
 * 사용자의 검색 활동을 추적하여 개인화 및 분석에 활용
 */
@Entity
@Table(name = "search_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "search_query", nullable = false, length = 500)
    @NotBlank
    private String searchQuery;

    /**
     * 적용된 검색 필터 정보 (JSON)
     * {"category": "backend", "language": "java", "difficulty": "beginner"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "search_filters", columnDefinition = "JSON")
    private Map<String, Object> searchFilters;

    @Column(name = "results_count")
    @PositiveOrZero
    private Integer resultsCount;

    @Column(name = "clicked_result_id")
    private Long clickedResultId;

    @Column(name = "search_session_id", length = 100)
    private String searchSessionId;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.searchSessionId == null) {
            this.searchSessionId = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 사용자 검색 로그 생성
     */
    public static SearchLog createUserSearch(User user, String query, Map<String, Object> filters,
            int resultCount, String sessionId) {
        return SearchLog.builder()
                .user(user)
                .searchQuery(query)
                .searchFilters(filters)
                .resultsCount(resultCount)
                .searchSessionId(sessionId)
                .build();
    }

    /**
     * 익명 검색 로그 생성 (비회원)
     */
    public static SearchLog createAnonymousSearch(String query, Map<String, Object> filters,
            int resultCount, String sessionId) {
        return SearchLog.builder()
                .user(null) // 익명 사용자
                .searchQuery(query)
                .searchFilters(filters)
                .resultsCount(resultCount)
                .searchSessionId(sessionId)
                .build();
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 검색 결과 클릭 기록
     */
    public void recordClick(Long resultId) {
        this.clickedResultId = resultId;
    }

    /**
     * 검색 결과를 클릭했는지 확인
     */
    public boolean hasClickedResult() {
        return this.clickedResultId != null;
    }

    /**
     * 익명 검색인지 확인
     */
    public boolean isAnonymousSearch() {
        return this.user == null;
    }

    /**
     * 검색 결과가 있는지 확인
     */
    public boolean hasResults() {
        return this.resultsCount != null && this.resultsCount > 0;
    }

    /**
     * 같은 세션의 검색인지 확인
     */
    public boolean isSameSession(String sessionId) {
        return this.searchSessionId != null && this.searchSessionId.equals(sessionId);
    }

    /**
     * 오늘의 검색인지 확인
     */
    public boolean isToday() {
        return this.createdAt.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * 검색 필터 JSON 변환 유틸리티
     */
    public void setSearchFiltersFromJson(String json) {
        if (json != null && !json.trim().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.searchFilters = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON format for search filters", e);
            }
        }
    }

    public String getSearchFiltersAsJson() {
        if (this.searchFilters == null)
            return null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this.searchFilters);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 특정 필터 값 가져오기
     */
    public Object getFilterValue(String filterKey) {
        if (this.searchFilters == null)
            return null;
        return this.searchFilters.get(filterKey);
    }

    /**
     * 필터 값 설정
     */
    public void setFilterValue(String filterKey, Object value) {
        if (this.searchFilters == null) {
            this.searchFilters = Map.of(filterKey, value);
        } else {
            this.searchFilters.put(filterKey, value);
        }
    }

    /**
     * 검색 쿼리 정규화 (분석용)
     */
    public String getNormalizedQuery() {
        if (this.searchQuery == null)
            return "";
        return this.searchQuery.toLowerCase().trim();
    }

    /**
     * 검색 효율성 점수 계산
     * 결과 수와 클릭 여부를 기반으로 계산
     */
    public double getSearchEfficiencyScore() {
        if (this.resultsCount == null || this.resultsCount == 0) {
            return 0.0; // 결과 없음
        }

        double baseScore = Math.min(1.0, 10.0 / this.resultsCount); // 결과가 적을수록 높은 점수

        if (hasClickedResult()) {
            baseScore += 0.5; // 클릭 시 보너스
        }

        return Math.min(1.0, baseScore);
    }

    /**
     * 검색 카테고리 추출
     */
    public String getSearchCategory() {
        if (this.searchFilters == null)
            return "전체";

        Object category = this.searchFilters.get("category");
        return category != null ? category.toString() : "전체";
    }

    /**
     * 검색 언어 추출
     */
    public String getSearchLanguage() {
        if (this.searchFilters == null)
            return "전체";

        Object language = this.searchFilters.get("language");
        return language != null ? language.toString() : "전체";
    }

    /**
     * 검색 난이도 추출
     */
    public String getSearchDifficulty() {
        if (this.searchFilters == null)
            return "전체";

        Object difficulty = this.searchFilters.get("difficulty");
        return difficulty != null ? difficulty.toString() : "전체";
    }

    /**
     * 검색 요약 정보 반환
     */
    public String getSearchSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("검색어: ").append(this.searchQuery);

        if (this.resultsCount != null) {
            summary.append(", 결과: ").append(this.resultsCount).append("개");
        }

        if (hasClickedResult()) {
            summary.append(", 클릭함");
        }

        if (!getSearchCategory().equals("전체")) {
            summary.append(", 카테고리: ").append(getSearchCategory());
        }

        return summary.toString();
    }
}
