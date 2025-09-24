package saviing.game.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 은행 API 통신을 위한 설정 클래스.
 *
 * RestTemplate 빈을 생성하고 타임아웃 설정을 적용합니다.
 * 은행 서비스와의 통신 실패가 게임 서비스에 미치는 영향을 최소화하기 위해
 * 짧은 타임아웃을 설정합니다.
 */
@Configuration
public class BankApiConfig {

    @Value("${bank.api.connect-timeout:3000}")
    private int connectTimeoutMs;

    @Value("${bank.api.read-timeout:5000}")
    private int readTimeoutMs;

    /**
     * 은행 API 통신용 RestTemplate을 생성합니다.
     *
     * 짧은 타임아웃을 설정하여 은행 서비스 장애 시에도
     * 게임 서비스의 응답 속도에 미치는 영향을 최소화합니다.
     *
     * @return 타임아웃이 설정된 RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);

        return new RestTemplate(factory);
    }
}