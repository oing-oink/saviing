package saviing.game.character.application.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 은행 서비스 API 통신 클라이언트 구현체.
 *
 * RestTemplate을 사용하여 은행 서비스와 HTTP 통신을 수행합니다.
 * API 호출 실패 시 로깅하고 빈 Optional을 반환하여 게임 서비스의 핵심 기능에 영향을 주지 않습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BankApiClientImpl implements BankApiClient {

    private final RestTemplate restTemplate;

    @Value("${bank.api.base-url:http://localhost:8080}")
    private String bankApiBaseUrl;

    /**
     * 계좌의 보너스 이자율을 업데이트합니다.
     *
     * 은행 서비스의 이자율 설정 API를 호출하여 계좌의 보너스 금리를 업데이트합니다.
     * 현재 금리보다 높은 경우에만 업데이트되며, 낮거나 같은 경우 기존 금리가 유지됩니다.
     *
     * @param accountId 계좌 ID
     * @param newBonusRate 새로 설정할 보너스 이자율 (백분율, 예: 3.5)
     * @return 실제 설정된 보너스 이자율 (API 호출 실패 시 빈 Optional)
     */
    @Override
    public Optional<BigDecimal> updateAccountInterestRate(Long accountId, BigDecimal newBonusRate) {
        if (accountId == null || newBonusRate == null) {
            log.warn("잘못된 파라미터: accountId={}, newBonusRate={}", accountId, newBonusRate);
            return Optional.empty();
        }

        try {
            URI uri = buildUpdateInterestRateUri(accountId);
            HttpEntity<Map<String, Object>> request = buildUpdateInterestRateRequest(newBonusRate);

            log.debug("은행 API 이자율 업데이트 호출: accountId={}, newRate={}%, uri={}",
                accountId, newBonusRate, uri);

            ResponseEntity<Map> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                request,
                Map.class
            );

            BigDecimal currentRate = extractCurrentRateFromResponse(response);
            if (currentRate != null) {
                log.info("은행 API 이자율 업데이트 성공: accountId={}, currentRate={}%",
                    accountId, currentRate);
                return Optional.of(currentRate);
            } else {
                log.warn("은행 API 응답에서 이자율 추출 실패: accountId={}, response={}",
                    accountId, response.getBody());
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("은행 API 이자율 업데이트 실패: accountId={}, newRate={}%, error={}",
                accountId, newBonusRate, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * 이자율 업데이트 API URI를 생성합니다.
     *
     * @param accountId 계좌 ID
     * @return 완성된 API URI
     */
    private URI buildUpdateInterestRateUri(Long accountId) {
        return URI.create(bankApiBaseUrl + "/v1/accounts/id/" + accountId + "/interest-rate");
    }

    /**
     * 이자율 업데이트 요청 객체를 생성합니다.
     *
     * @param newBonusRate 새로운 보너스 이자율
     * @return HTTP 요청 엔터티
     */
    private HttpEntity<Map<String, Object>> buildUpdateInterestRateRequest(BigDecimal newBonusRate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("newBonusRatePercentage", newBonusRate);

        return new HttpEntity<>(body, headers);
    }

    /**
     * API 응답에서 현재 이자율을 추출합니다.
     *
     * @param response 은행 API 응답
     * @return 추출된 현재 이자율 (추출 실패 시 null)
     */
    private BigDecimal extractCurrentRateFromResponse(ResponseEntity<Map> response) {
        try {
            Map<String, Object> body = response.getBody();
            if (body == null) {
                return null;
            }

            // ApiResult 구조: { "success": true, "body": { "currentBonusRatePercentage": 3.5 } }
            Map<String, Object> data = (Map<String, Object>) body.get("body");
            if (data == null) {
                return null;
            }

            Object rate = data.get("currentBonusRatePercentage");
            if (rate == null) {
                return null;
            }

            if (rate instanceof Number) {
                return BigDecimal.valueOf(((Number) rate).doubleValue());
            } else {
                return new BigDecimal(rate.toString());
            }

        } catch (Exception e) {
            log.warn("응답에서 이자율 추출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }
}