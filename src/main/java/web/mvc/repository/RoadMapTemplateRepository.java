package web.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.RoadMapTemplate;
import web.mvc.domain.User;

import java.util.List;

/**
 * 로드맵 템플릿 Repository
 */
@Repository
public interface RoadMapTemplateRepository extends JpaRepository<RoadMapTemplate, Long> {

    /**
     * 활성화된 로드맵만 조회
     */
    List<RoadMapTemplate> findByIsActiveTrueOrderByCreatedAtDesc();

    /**
     * 공식 로드맵 조회
     */
    List<RoadMapTemplate> findByIsOfficialTrueAndIsActiveTrueOrderByUsageCountDesc();

    /**
     * 프로그래밍 언어별 로드맵 조회
     */
    List<RoadMapTemplate> findByProgrammingLanguageAndIsActiveTrueOrderByAverageRatingDesc(String programmingLanguage);

    /**
     * 분야별 로드맵 조회
     */
    List<RoadMapTemplate> findByFieldCategoryAndIsActiveTrueOrderByAverageRatingDesc(String fieldCategory);

    /**
     * 난이도별 로드맵 조회
     */
    @Query("SELECT r FROM RoadMapTemplate r WHERE r.difficultyLevel = :difficulty AND r.isActive = true ORDER BY r.averageRating DESC")
    List<RoadMapTemplate> findByDifficultyLevel(@Param("difficulty") String difficulty);

    /**
     * 생성자별 로드맵 조회
     */
    List<RoadMapTemplate> findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(User createdBy);

    /**
     * 키워드로 로드맵 검색 (제목, 설명)
     */
    @Query("SELECT r FROM RoadMapTemplate r WHERE r.isActive = true AND " +
            "(LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY r.averageRating DESC")
    Page<RoadMapTemplate> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 인기 로드맵 (사용 횟수 기준)
     */
    List<RoadMapTemplate> findTop10ByIsActiveTrueOrderByUsageCountDesc();

    /**
     * 최고 평점 로드맵
     */
    List<RoadMapTemplate> findTop10ByIsActiveTrueAndTotalReviewsGreaterThanOrderByAverageRatingDesc(Integer minReviews);

    /**
     * 복합 필터링 검색
     */
    @Query("SELECT r FROM RoadMapTemplate r WHERE r.isActive = true " +
            "AND (:programmingLanguage IS NULL OR r.programmingLanguage = :programmingLanguage) " +
            "AND (:fieldCategory IS NULL OR r.fieldCategory = :fieldCategory) " +
            "AND (:difficulty IS NULL OR r.difficultyLevel = :difficulty) " +
            "ORDER BY r.averageRating DESC")
    Page<RoadMapTemplate> findWithFilters(@Param("programmingLanguage") String programmingLanguage,
            @Param("fieldCategory") String fieldCategory,
            @Param("difficulty") String difficulty,
            Pageable pageable);
}
