package web.mvc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 관련 설정 프로퍼티
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 토큰 서명에 사용할 비밀키
     */
    private String secret = "nextStepSecretKeyForJWTTokenGenerationAndValidation2024";

    /**
     * JWT 토큰 만료 시간 (밀리초)
     * 기본값: 24시간 (86400000ms)
     */
    private long expiration = 86400000L;

    /**
     * 리프레시 토큰 만료 시간 (밀리초)
     * 기본값: 7일 (604800000ms)
     */
    private long refreshExpiration = 604800000L;

    /**
     * JWT 토큰 헤더명
     */
    private String header = "Authorization";

    /**
     * JWT 토큰 접두사
     */
    private String prefix = "Bearer ";
}
