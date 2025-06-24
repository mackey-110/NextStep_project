package web.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.User;
import web.mvc.domain.UserRoadMap;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 로드맵 Repository
 */
@Repository
public interface UserRoadMapRepository extends JpaRepository<UserRoadMap, Long> {

    /**
     * 사용자별 로드맵 조회
     */
    List<UserRoadMap> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자별 진행 중인 로드맵 조회
     */
    @Query("SELECT ur FROM UserRoadMap ur WHERE ur.user = :user AND ur.status = 'IN_PROGRESS' ORDER BY ur.updatedAt DESC")
    List<UserRoadMap> findInProgressRoadMapsByUser(@Param("user") User user);

    /**
     * 사용자별 완료된 로드맵 조회
     */
    @Query("SELECT ur FROM UserRoadMap ur WHERE ur.user = :user AND ur.status = 'COMPLETED' ORDER BY ur.completedAt DESC")
    List<UserRoadMap> findCompletedRoadMapsByUser(@Param("user") User user);

    /**
     * 사용자가 특정 템플릿을 이미 사용 중인지 확인
     */
    @Query("SELECT ur FROM UserRoadMap ur WHERE ur.user = :user AND ur.template.id = :templateId AND ur.status IN ('NOT_STARTED', 'IN_PROGRESS', 'PAUSED')")
    Optional<UserRoadMap> findActiveRoadMapByUserAndTemplate(@Param("user") User user,
            @Param("templateId") Long templateId);

    /**
     * 사용자별 로드맵 통계
     */
    @Query("SELECT COUNT(ur) FROM UserRoadMap ur WHERE ur.user = :user AND ur.status = 'COMPLETED'")
    Long countCompletedRoadMapsByUser(@Param("user") User user);

    /**
     * 사용자별 총 학습 시간 계산
     */
    @Query("SELECT COALESCE(SUM(usp.studyHours), 0) FROM UserStepProgress usp WHERE usp.userRoadMap.user = :user")
    Long getTotalStudyHoursByUser(@Param("user") User user);

    /**
     * 전체 진행률 계산
     */
    @Query("SELECT AVG(ur.progressPercentage) FROM UserRoadMap ur WHERE ur.user = :user AND ur.status != 'NOT_STARTED'")
    Double getAverageProgressByUser(@Param("user") User user);
}
