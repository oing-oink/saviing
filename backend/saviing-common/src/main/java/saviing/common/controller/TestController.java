package saviing.common.controller;

import org.springframework.web.bind.annotation.*;
import saviing.common.response.ApiResult;
import saviing.common.exception.BusinessException;
import saviing.common.exception.ErrorCode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // 성공 응답 (데이터 없음)
    @GetMapping("/success")
    public ApiResult<Void> testSuccess() {
        return ApiResult.ok();
    }

    // 성공 응답 (데이터 포함)
    @GetMapping("/success-with-data")
    public ApiResult<TestResponse> testSuccessWithData() {
        TestResponse data = new TestResponse("Hello World", 123);
        return ApiResult.ok(data);
    }

    // 커스텀 상태코드
    @PostMapping("/created")
    public ApiResult<TestResponse> testCreated() {
        TestResponse data = new TestResponse("Created successfully", 456);
        return ApiResult.of(201, data);
    }

    // 헤더와 쿠키 포함 응답
    @GetMapping("/with-headers")
    public ApiResult<String> testWithHeaders() {
        return ApiResult.ok("Response with headers and cookies")
            .header("X-Custom-Header", "CustomValue")
            .header("X-Api-Version", "1.0")
            .cookie("sessionId", "abc123")
            .cookie("theme", "dark");
    }

    // Validation 에러 테스트
    @PostMapping("/validation")
    public ApiResult<String> testValidation(@Valid @RequestBody TestRequest request) {
        return ApiResult.ok("Validation passed: " + request.name());
    }

    // BusinessException 테스트
    @GetMapping("/business-error")
    public ApiResult<Void> testBusinessError() {
        throw new BusinessException(ErrorCode.BANK_ACCOUNT_NOT_FOUND);
    }

    // IllegalArgumentException 테스트
    @GetMapping("/illegal-arg")
    public ApiResult<Void> testIllegalArgument() {
        throw new IllegalArgumentException("잘못된 파라미터입니다.");
    }

    // 일반 Exception 테스트
    @GetMapping("/server-error")
    public ApiResult<Void> testServerError() {
        throw new RuntimeException("예상치 못한 서버 오류");
    }

    // DTO classes
    public record TestResponse(String message, Integer code) {}

    public record TestRequest(
        @NotBlank(message = "이름은 필수입니다")
        String name,
        
        @NotNull(message = "나이는 필수입니다")
        Integer age
    ) {}
}