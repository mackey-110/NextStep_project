package web.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.LearningContent;
import web.mvc.domain.LearningContent.ContentType;
import web.mvc.domain.LearningContent.DifficultyLevel;
import web.mvc.domain.User;

import java.util.List;

/**
 * 학습 콘텐츠 Repository
 */
@Repository
public interface LearningContentRepository extends JpaRepository<LearningContent, Long> {

    /**
     * 승인된 활성 콘텐츠 조회
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true")
    List<LearningContent> findApprovedActiveContents();

    /**
     * 승인된 활성 콘텐츠 조회 (페이징)
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true")
    Page<LearningContent> findApprovedActiveContents(Pageable pageable);

    /**
     * 콘텐츠 타입별 조회
     */
    Page<LearningContent> findByContentTypeAndIsApprovedAndIsActive(ContentType contentType,
            Boolean isApproved, Boolean isActive, Pageable pageable);

    /**
     * 프로그래밍 언어별 조회
     */
    Page<LearningContent> findByProgrammingLanguageAndIsApprovedAndIsActive(String programmingLanguage,
            Boolean isApproved, Boolean isActive, Pageable pageable);

    /**
     * 카테고리별 조회
     */
    Page<LearningContent> findByCategoryAndIsApprovedAndIsActive(String category,
            Boolean isApproved, Boolean isActive, Pageable pageable);

    /**
     * 난이도별 조회
     */
    Page<LearningContent> findByDifficultyLevelAndIsApprovedAndIsActive(DifficultyLevel difficultyLevel,
            Boolean isApproved, Boolean isActive, Pageable pageable);

    /**
     * 무료 콘텐츠 조회
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true AND c.isFree = true")
    Page<LearningContent> findFreeContents(Pageable pageable);

    /**
     * 유료 콘텐츠 조회
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true AND c.isFree = false")
    Page<LearningContent> findPaidContents(Pageable pageable);

    /**
     * 제목 또는 설명으로 검색
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true " +
            "AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<LearningContent> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 작성자별 조회
     */
    List<LearningContent> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    /**
     * 작성자별 조회 (페이징)
     */
    Page<LearningContent> findByCreatedByOrderByCreatedAtDesc(User createdBy, Pageable pageable);

    /**
     * 승인 대기 콘텐츠 조회
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = false ORDER BY c.createdAt ASC")
    Page<LearningContent> findPendingApproval(Pageable pageable);

    /**
     * 인기 콘텐츠 조회 (조회수 기준)
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true " +
            "ORDER BY c.viewCount DESC")
    Page<LearningContent> findPopularContents(Pageable pageable);

    /**
     * 최신 콘텐츠 조회
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    Page<LearningContent> findLatestContents(Pageable pageable);

    /**
     * 평점 높은 콘텐츠 조회
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true " +
            "AND c.reviewCount > 0 ORDER BY c.averageRating DESC")
    Page<LearningContent> findTopRatedContents(Pageable pageable);

    /**
     * 복합 검색 (필터 조건)
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true " +
            "AND (:contentType IS NULL OR c.contentType = :contentType) " +
            "AND (:language IS NULL OR c.programmingLanguage = :language) " +
            "AND (:category IS NULL OR c.category = :category) " +
            "AND (:difficulty IS NULL OR c.difficultyLevel = :difficulty) " +
            "AND (:isFree IS NULL OR c.isFree = :isFree) " +
            "AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<LearningContent> searchWithFilters(@Param("contentType") ContentType contentType,
            @Param("language") String language,
            @Param("category") String category,
            @Param("difficulty") DifficultyLevel difficulty,
            @Param("isFree") Boolean isFree,
            @Param("keyword") String keyword,
            Pageable pageable);

    /**
     * 태그로 검색
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true " +
            "AND JSON_CONTAINS(c.tags, :tag)")
    Page<LearningContent> findByTag(@Param("tag") String tag, Pageable pageable);

    /**
     * 언어별 콘텐츠 통계
     */
    @Query("SELECT c.programmingLanguage, COUNT(c) FROM LearningContent c " +
            "WHERE c.isApproved = true AND c.isActive = true " +
            "GROUP BY c.programmingLanguage")
    List<Object[]> getLanguageStatistics();

    /**
     * 카테고리별 콘텐츠 통계
     */
    @Query("SELECT c.category, COUNT(c) FROM LearningContent c " +
            "WHERE c.isApproved = true AND c.isActive = true " +
            "GROUP BY c.category")
    List<Object[]> getCategoryStatistics();

    /**
     * 콘텐츠 타입별 통계
     */
    @Query("SELECT c.contentType, COUNT(c) FROM LearningContent c " +
            "WHERE c.isApproved = true AND c.isActive = true " +
            "GROUP BY c.contentType")
    List<Object[]> getContentTypeStatistics();

    /**
     * 승인 상태별 통계
     */
    @Query("SELECT c.isApproved, COUNT(c) FROM LearningContent c GROUP BY c.isApproved")
    List<Object[]> getApprovalStatistics();

    /**
     * 월별 콘텐츠 생성 통계
     */
    @Query("SELECT YEAR(c.createdAt), MONTH(c.createdAt), COUNT(c) FROM LearningContent c " +
            "GROUP BY YEAR(c.createdAt), MONTH(c.createdAt) " +
            "ORDER BY YEAR(c.createdAt) DESC, MONTH(c.createdAt) DESC")
    List<Object[]> getMonthlyCreationStats();

    /**
     * 특정 기간 내 생성된 콘텐츠 조회
     */
    @Query("SELECT c FROM LearningContent c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate")
    List<LearningContent> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 추천 콘텐츠 조회 (북마크 많은 순)
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isApproved = true AND c.isActive = true " +
            "ORDER BY c.bookmarkCount DESC")
    Page<LearningContent> findRecommendedContents(Pageable pageable);
}
