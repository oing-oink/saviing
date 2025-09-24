package saviing.bank.account.application.event;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import saviing.bank.account.application.service.ProductService;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.Product;
import saviing.bank.account.domain.model.ProductCategory;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.config.StreamProperties;

/**
 * 적금 입금 이벤트 발행자
 * 적금 계좌 입금 시 Redis Stream을 통해 게임 서비스로 이벤트를 발행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SavingsDepositEventPublisher {

    private static final String SCHEMA_VERSION = "v1";

    private final StringRedisTemplate stringRedisTemplate;
    private final StreamProperties streamProperties;
    private final ProductService productService;

    public void publish(Account account, MoneyWon transactionAmount) {
        if (account == null || transactionAmount == null) {
            return;
        }

        String streamKey = streamProperties.getKey();
        if (streamKey == null || streamKey.isBlank()) {
            log.debug("Stream key is not configured; skipping savings deposit event publishing");
            return;
        }

        try {
            Product product = productService.getProduct(account.getProductId());
            if (product.getCategory() != ProductCategory.INSTALLMENT_SAVINGS) {
                return;
            }

            Map<String, String> body = buildEventBody(account, transactionAmount);
            MapRecord<String, String, String> record = StreamRecords
                .mapBacked(body)
                .withStreamKey(streamKey);

            stringRedisTemplate.opsForStream().add(record);

            trimStreamIfNeeded(streamKey);

            log.info(
                "적금 입금 이벤트 발행 완료: stream={}, customerId={}, accountId={}",
                streamKey,
                account.getCustomerId(),
                account.getId() != null ? account.getId().value() : "N/A"
            );
        } catch (Exception ex) {
            log.error(
                "적금 입금 이벤트 발행 실패: accountId={}",
                account.getId() != null ? account.getId().value() : "unknown",
                ex
            );
        }
    }

    private Map<String, String> buildEventBody(Account account, MoneyWon transactionAmount) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("version", SCHEMA_VERSION);
        body.put("customerId", String.valueOf(account.getCustomerId()));

        AccountId accountId = account.getId();
        if (accountId != null) {
            body.put("accountId", String.valueOf(accountId.value()));
        }

        ProductId productId = account.getProductId();
        if (productId != null) {
            body.put("productId", String.valueOf(productId.value()));
        }

        body.put("amount", String.valueOf(transactionAmount.amount()));
        body.put("occurredAt", Instant.now().toString());

        return body;
    }

    private void trimStreamIfNeeded(String streamKey) {
        if (streamProperties.getMaxlenApprox() != null && streamProperties.getMaxlenApprox() > 0) {
            stringRedisTemplate.opsForStream().trim(streamKey, streamProperties.getMaxlenApprox(), true);
        }
    }
}