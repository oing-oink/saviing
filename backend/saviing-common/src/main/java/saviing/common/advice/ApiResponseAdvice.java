package saviing.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import saviing.common.response.CommonResult;

@Slf4j
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {

        // supports에서 CommonResult 타입 확인 완료되었으므로, 타입 캐스팅 가능
        CommonResult apiResponse = (CommonResult) body;
        
        response.setStatusCode(apiResponse.getStatusCode());
        
        for(ResponseCookie cookie : apiResponse.getCookies()) {
            response.getHeaders().add("Set-Cookie", cookie.toString());
        }
        
        apiResponse.getHeaders().forEach((k, v) -> response.getHeaders().add(k, v));
        
        return body;
    }
    
    /**
     * CommonResult 객체 확인 여부 반환
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return CommonResult.class.isAssignableFrom(returnType.getParameterType());
    }
}