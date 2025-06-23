package web.mvc.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

/**
 * 전역 예외 처리기
 * 모든 컨트롤러에서 발생하는 예외를 일관된 형태로 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 유효성 검증 실패 (Request Body)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error: ", e);
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("입력값이 올바르지 않습니다: " + message, "VALIDATION_ERROR"));
    }

    /**
     * 유효성 검증 실패 (Request Parameter)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Constraint violation: ", e);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("입력값이 올바르지 않습니다: " + e.getMessage(), "VALIDATION_ERROR"));
    }

    /**
     * 바인딩 예외
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
        log.error("Bind error: ", e);
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("입력값이 올바르지 않습니다: " + message, "BIND_ERROR"));
    }

    /**
     * 비즈니스 로직 예외
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.error("Business error: ", e);
        return ResponseEntity.status(e.getStatus())
                .body(ApiResponse.error(e.getMessage(), e.getErrorCode()));
    }

    /**
     * 인증 예외
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException e) {
        log.error("Unauthorized error: ", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage(), "UNAUTHORIZED"));
    }

    /**
     * 권한 부족 예외
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbiddenException(ForbiddenException e) {
        log.error("Forbidden error: ", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage(), "FORBIDDEN"));
    }

    /**
     * 리소스 없음 예외
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NotFoundException e) {
        log.error("Not found error: ", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
    }

    /**
     * 기타 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("Unexpected error: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다.", "INTERNAL_SERVER_ERROR"));
    }
}
