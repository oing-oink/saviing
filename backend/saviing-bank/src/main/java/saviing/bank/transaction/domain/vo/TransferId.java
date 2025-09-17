package saviing.bank.transaction.domain.vo;

import java.util.Objects;
import java.util.UUID;

import lombok.NonNull;

/**
 * 송금 식별자 VO. 멱등성 보장과 도메인 이벤트 추적에 사용한다.
 */
public record TransferId(String value) {

    public TransferId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TransferId value must not be null or blank");
        }
    }

    /**
     * 랜덤 UUID 기반으로 새로운 TransferId를 생성한다.
     */
    public static TransferId newId() {
        return new TransferId(UUID.randomUUID().toString());
    }

    /**
     * 주어진 문자열을 TransferId로 감싼다.
     */
    public static TransferId of(@NonNull String value) {
        return new TransferId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
