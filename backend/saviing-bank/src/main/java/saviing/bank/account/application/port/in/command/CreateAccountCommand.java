package saviing.bank.account.application.port.in.command;

import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.account.domain.vo.MoneyWon;
import saviing.bank.account.domain.vo.TermPeriod;
import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.model.TermUnit;

/**
 * 계좌 생성 명령의 최상위 인터페이스
 *
 * Sealed Interface 패턴을 사용하여 타입 안전성을 보장하며,
 * 자유입출금 계좌와 적금 계좌 생성을 구분하여 처리합니다.
 *
 * @see CreateDemandDepositCommand 자유입출금 계좌 생성 명령
 * @see CreateSavingsCommand 적금 계좌 생성 명령
 */
public sealed interface CreateAccountCommand
    permits CreateDemandDepositCommand, CreateSavingsCommand {

    /** 고객 ID */
    Long customerId();

    /** 상품 ID */
    ProductId productId();

    /**
     * 웹 요청 파라미터로부터 적절한 CreateAccountCommand 구현체를 생성하는 팩토리 메서드
     *
     * 적금 관련 필드가 하나라도 존재하면 CreateSavingsCommand를 생성하고,
     * 모든 적금 필드가 null이면 CreateDemandDepositCommand를 생성합니다.
     *
     * @param customerId 고객 ID
     * @param productId 상품 ID
     * @param targetAmount 목표금액 (적금용, null 가능)
     * @param termValue 기간 값 (적금용, null 가능)
     * @param termUnit 기간 단위 (적금용, null 가능)
     * @param maturityWithdrawalAccount 만기 시 출금계좌 (적금용, null 가능)
     * @return 적절한 CreateAccountCommand 구현체
     * @throws IllegalArgumentException 적금 필드가 부분적으로만 제공된 경우
     */
    static CreateAccountCommand of(
        Long customerId,
        Long productId,
        Long targetAmount,
        Integer termValue,
        String termUnit,
        String maturityWithdrawalAccount
    ) {
        // 적금 필드가 하나라도 있는지 확인
        boolean hasSavingsFields = targetAmount != null || termValue != null || termUnit != null || maturityWithdrawalAccount != null;

        if (hasSavingsFields) {
            // 적금 필드 중 하나라도 있으면 필수 필드들 검증 (기본적인 검증만)
            if (targetAmount == null) {
                throw new IllegalArgumentException("적금 계좌 생성 시 목표금액은 필수입니다");
            }
            if (termValue == null || termUnit == null) {
                throw new IllegalArgumentException("적금 계좌 생성 시 기간 정보는 필수입니다");
            }

            // 기본적인 데이터 변환
            TermUnit unit = TermUnit.valueOf(termUnit.toUpperCase());
            TermPeriod period = TermPeriod.of(termValue, unit);

            return new CreateSavingsCommand(
                customerId,
                ProductId.of(productId),
                MoneyWon.of(targetAmount),
                period,
                maturityWithdrawalAccount != null ? new AccountNumber(maturityWithdrawalAccount) : null
            );
        }

        // 적금 필드가 전혀 없으면 자유입출금 계좌
        return new CreateDemandDepositCommand(customerId, ProductId.of(productId));
    }
}