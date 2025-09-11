package saviing.common.controller;

import org.springframework.web.bind.annotation.*;
import saviing.common.response.ApiResult;
import saviing.common.exception.BusinessException;
import saviing.common.exception.GlobalErrorCode;
import saviing.common.annotation.ExecutionTime;
import saviing.common.annotation.LogLevel;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ExecutionTime
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private TestService testService;
    
    @Autowired
    private AnotherTestService anotherTestService;

    // 성공 응답 (데이터 없음)
    @ExecutionTime
    @GetMapping("/success")
    public ApiResult<Void> testSuccess() {
        return ApiResult.ok();
    }

    // 성공 응답 (데이터 포함)
    @ExecutionTime(threshold = 100)
    @GetMapping("/success-with-data")
    public ApiResult<TestResponse> testSuccessWithData() throws InterruptedException {
        Thread.sleep(150); // 의도적으로 100ms 임계값 초과
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
        throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
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
    
    // ExecutionTime 전파 테스트 - 단순한 서비스 호출
    @ExecutionTime(level = LogLevel.INFO)
    @GetMapping("/execution-time-simple")
    public ApiResult<String> testExecutionTimeSimple() throws InterruptedException {
        String result = testService.processData("simple test");
        return ApiResult.ok(result);
    }
    
    // ExecutionTime 전파 테스트 - 복합 서비스 호출
    @ExecutionTime(level = LogLevel.INFO, threshold = 200)
    @GetMapping("/execution-time-complex")
    public ApiResult<String> testExecutionTimeComplex() throws InterruptedException {
        // 여러 서비스 메서드 호출
        String processed = testService.processData("complex test");
        testService.performCalculation(100);
        String external = testService.fetchExternalData();
        String formatted = anotherTestService.formatResult(external);
        anotherTestService.saveToDatabase(formatted);
        
        return ApiResult.ok("Complex processing completed: " + processed);
    }
    
    // ExecutionTime 전파 테스트 - 중첩 호출 (서비스에서 다른 서비스 호출)
    @ExecutionTime(level = LogLevel.WARN, threshold = 50)
    @GetMapping("/execution-time-nested")
    public ApiResult<String> testExecutionTimeNested() throws InterruptedException {
        // performCalculation 내부에서 validateInput이 호출됨
        testService.performCalculation(42);
        return ApiResult.ok("Nested method calls completed");
    }
    
    // ExecutionTime 없는 일반 엔드포인트 (전파 안됨)
    @GetMapping("/no-execution-time")
    public ApiResult<String> testNoExecutionTime() throws InterruptedException {
        String result = testService.processData("no propagation test");
        return ApiResult.ok(result);
    }
    
    // ExecutionTime 깊이 테스트 - 3단계 깊이 호출
    @ExecutionTime(level = LogLevel.DEBUG)
    @GetMapping("/execution-time-depth")
    public ApiResult<String> testExecutionTimeDepth() throws InterruptedException {
        // Level 1: Controller -> Service
        String result = testService.deepProcessing("depth test");
        return ApiResult.ok("Deep processing result: " + result);
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