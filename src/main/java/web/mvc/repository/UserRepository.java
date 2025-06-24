package web.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.User;
import web.mvc.domain.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 찾기
     */
    Optional<User> findByEmail(String email);

    /**
     * 닉네임으로 사용자 찾기
     */
    Optional<User> findByNickname(String nickname);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 존재 여부 확인
     */
    boolean existsByNickname(String nickname);

    /**
     * 활성 사용자만 조회
     */
    List<User> findByIsActiveTrue();

    /**
     * 역할별 사용자 조회
     */
    List<User> findByUserRole(UserRole userRole);

    /**
     * 이메일 인증된 사용자 조회
     */
    List<User> findByEmailVerifiedTrue();

    /**
     * 특정 기간 이후 가입한 사용자
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * 마지막 로그인이 특정 기간 이전인 비활성 사용자
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date OR u.lastLoginAt IS NULL")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);

    /**
     * 구독 만료된 사용자
     */
    @Query("SELECT u FROM User u WHERE u.subscriptionEndDate < :now AND u.subscriptionEndDate IS NOT NULL")
    List<User> findExpiredSubscriptionUsers(@Param("now") LocalDateTime now);
}
