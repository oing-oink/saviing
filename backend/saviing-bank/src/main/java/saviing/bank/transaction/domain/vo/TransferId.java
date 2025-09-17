package saviing.bank.transaction.domain.vo;

import java.util.UUID;

import lombok.NonNull;

/**
 * 송금 식별자를 나타내는 값 객체
 * 멱등성 보장과 도메인 이벤트 추적에 사용한다.
 */
public record TransferId(String value) {

    public TransferId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TransferId value must not be null or blank");
        }
    }

    /**
     * 랜덤 UUID 기반으로 새로운 송금 ID를 생성한다
     *
     * @return 새로운 송금 ID 객체
     */
    public static TransferId newId() {
        return new TransferId(UUID.randomUUID().toString());
    }

    /**
     * 주어진 문자열로 송금 ID를 생성한다
     *
     * @param value 송금 ID 값 (비어있지 않아야 함)
     * @return 생성된 송금 ID 객체
     * @throws IllegalArgumentException 값이 null이거나 비어있는 경우
     */
    public static TransferId of(@NonNull String value) {
        return new TransferId(value);
    }

    /**
     * 송금 ID의 문자열 표현을 반환한다
     *
     * @return 송금 ID 값
     */
    @Override
    public String toString() {
        return value;
    }
}
