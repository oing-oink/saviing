package saviing.game.character.domain.model.enums;

/**
 * 계좌 해지 분류를 나타내는 열거형
 */
public enum TerminationCategory {
    /** 고객 요청 */
    CUSTOMER_REQUEST,
    /** 시스템 오류 */
    SYSTEM_ERROR,
    /** 정책 위반 */
    POLICY_VIOLATION,
    /** 시스템 점검 */
    MAINTENANCE,
    /** 기타 */
    OTHER
}