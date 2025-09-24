package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.result.UpdateInterestRateResult;

public interface UpdateAccountInterestRateUseCase {

    /**
     * 계좌의 보너스 금리를 현재 금리보다 높은 경우에만 업데이트합니다.
     *
     * 게임 진행도에 따른 이자율 혜택 증가 정책을 구현하기 위해 사용되며,
     * 이자율은 증가만 가능하고 감소는 허용하지 않습니다.
     *
     * @param accountId 금리를 변경할 계좌 ID
     * @param newBonusRatePercentage 새로 설정할 보너스 금리 (백분율)
     * @return 금리 업데이트 결과 (계좌 ID와 설정된 보너스 금리)
     */
    UpdateInterestRateResult updateAccountInterestRate(Long accountId, Double newBonusRatePercentage);
}