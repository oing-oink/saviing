package saviing.game.character.domain.model.vo;

import saviing.game.character.domain.model.enums.TerminationCategory;

import java.time.LocalDateTime;

/**
 * 계좌 해지 정보를 관리하는 Value Object
 * 해지 분류, 해지 사유, 해지 시간을 포함합니다.
 */
public record AccountTermination(
    TerminationCategory category,
    String reason,
    LocalDateTime terminatedAt
) {
    public AccountTermination {
        if (category == null) {
            throw new IllegalArgumentException("Termination category cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Termination reason cannot be empty");
        }
        if (terminatedAt == null) {
            throw new IllegalArgumentException("Termination date cannot be null");
        }
    }

    /**
     * 고객 요청에 의한 해지 정보를 생성합니다.
     * 
     * @param reason 해지 사유
     * @return 고객 요청 해지 정보
     */
    public static AccountTermination byCustomerRequest(String reason) {
        return new AccountTermination(TerminationCategory.CUSTOMER_REQUEST, reason, LocalDateTime.now());
    }

    /**
     * 시스템 오류에 의한 해지 정보를 생성합니다.
     * 
     * @param reason 해지 사유
     * @return 시스템 오류 해지 정보
     */
    public static AccountTermination bySystemError(String reason) {
        return new AccountTermination(TerminationCategory.SYSTEM_ERROR, reason, LocalDateTime.now());
    }

    /**
     * 정책 위반에 의한 해지 정보를 생성합니다.
     * 
     * @param reason 해지 사유
     * @return 정책 위반 해지 정보
     */
    public static AccountTermination byPolicyViolation(String reason) {
        return new AccountTermination(TerminationCategory.POLICY_VIOLATION, reason, LocalDateTime.now());
    }

    /**
     * 시스템 점검에 의한 해지 정보를 생성합니다.
     * 
     * @param reason 해지 사유
     * @return 시스템 점검 해지 정보
     */
    public static AccountTermination byMaintenance(String reason) {
        return new AccountTermination(TerminationCategory.MAINTENANCE, reason, LocalDateTime.now());
    }

    /**
     * 기타 사유에 의한 해지 정보를 생성합니다.
     * 
     * @param reason 해지 사유
     * @return 기타 사유 해지 정보
     */
    public static AccountTermination byOther(String reason) {
        return new AccountTermination(TerminationCategory.OTHER, reason, LocalDateTime.now());
    }

    /**
     * 고객 요청에 의한 해지인지 확인합니다.
     * 
     * @return 고객 요청 해지 여부
     */
    public boolean isCustomerRequest() {
        return category == TerminationCategory.CUSTOMER_REQUEST;
    }

    /**
     * 시스템 관련 해지인지 확인합니다.
     * 
     * @return 시스템 관련 해지 여부 (시스템 오류 또는 점검)
     */
    public boolean isSystemRelated() {
        return category == TerminationCategory.SYSTEM_ERROR || 
               category == TerminationCategory.MAINTENANCE;
    }
}