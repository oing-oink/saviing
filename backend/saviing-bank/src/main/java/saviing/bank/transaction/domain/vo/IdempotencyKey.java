package saviing.bank.transaction.domain.vo;

/**
 * 멱등성 보장을 위한 키 값 객체
 * 동일한 요청에 대해 중복 처리를 방지한다.
 */
public record IdempotencyKey(String value) {

    public IdempotencyKey {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("멱등키는 비어있을 수 없습니다");
        }
        if (value.length() > 64) {
            throw new IllegalArgumentException("멱등키는 64자를 초과할 수 없습니다: " + value.length());
        }
    }

    /**
     * 주어진 문자열로 멱등키를 생성한다
     * 입력된 값의 양끝 공백을 제거한다.
     *
     * @param value 멱등키 값 (비어있지 않고 64자 이하여야 함)
     * @return 생성된 멱등키 객체
     * @throws IllegalArgumentException 값이 비어있거나 64자를 초과하는 경우
     */
    public static IdempotencyKey of(String value) {
        return new IdempotencyKey(value.trim());
    }

    /**
     * 특정 계좌에 대한 멱등키를 생성한다
     * 계좌별 멱등성 스코핑을 명시적으로 표현하기 위한 팩토리 메서드
     *
     * @param sourceAccountId 출금 계좌 ID
     * @param originalKey 원본 멱등키 값
     * @return 생성된 멱등키 객체
     * @throws IllegalArgumentException 값이 유효하지 않은 경우
     */
    public static IdempotencyKey forAccount(Long sourceAccountId, String originalKey) {
        if (sourceAccountId == null) {
            throw new IllegalArgumentException("계좌 ID는 null일 수 없습니다");
        }
        return new IdempotencyKey(originalKey.trim());
    }
}