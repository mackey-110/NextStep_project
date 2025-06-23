package web.mvc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.mvc.exception.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 헬스체크 및 기본 정보 제공 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/health")
@Tag(name = "헬스체크", description = "서버 상태 확인 API")
public class HealthController {

    @Operation(summary = "서버 상태 확인", description = "서버가 정상적으로 동작하는지 확인합니다.")
    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("service", "NextStep API");
        status.put("version", "v1.0.0");

        log.info("Health check requested");
        return ApiResponse.success("서버가 정상적으로 동작 중입니다.", status);
    }

    @Operation(summary = "API 정보", description = "API 기본 정보를 제공합니다.")
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "NextStep API");
        info.put("description", "개발자 진로 학습 네비게이션 플랫폼");
        info.put("version", "v1.0.0");
        info.put("environment", "development");
        info.put("docs", "/swagger-ui.html");

        return ApiResponse.success("API 정보를 조회했습니다.", info);
    }
}
