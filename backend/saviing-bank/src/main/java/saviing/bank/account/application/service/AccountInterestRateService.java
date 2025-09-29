package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.bank.account.application.port.in.UpdateAccountInterestRateUseCase;
import saviing.bank.account.application.port.in.result.UpdateInterestRateResult;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.BasisPoints;
import saviing.bank.account.exception.AccountNotFoundException;
import saviing.bank.account.exception.InvalidRateException;
import saviing.common.annotation.ExecutionTime;

/**
 * 계좌 이자율 관리 응용 서비스.
 *
 * 게임 진행도에 따른 이자율 혜택 증가 정책을 구현합니다:
 * - 이자율은 증가만 가능 (감소 불가)
 * - 보너스 금리만 조정 (기본 금리 유지)
 * - 적금 계좌 대상
 */
@Slf4j
@ExecutionTime
@Service
@RequiredArgsConstructor
public class AccountInterestRateService implements UpdateAccountInterestRateUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    /**
     * 계좌의 보너스 금리를 현재 금리보다 높은 경우에만 업데이트합니다.
     *
     * 게임 진행도에 따른 이자율 혜택 증가 정책을 구현하기 위해 사용되며,
     * 이자율은 증가만 가능하고 감소는 허용하지 않습니다.
     *
     * @param accountId 금리를 변경할 계좌 ID
     * @param newBonusRatePercentage 새로 설정할 보너스 금리 (백분율, 0.0 ~ 100.0)
     * @return 금리 업데이트 결과 (계좌 ID와 설정된 보너스 금리)
     * @throws AccountNotFoundException 계좌를 찾을 수 없는 경우
     * @throws InvalidRateException 유효하지 않은 금리인 경우
     */
    @Override
    @Transactional
    public UpdateInterestRateResult updateAccountInterestRate(Long accountId, Double newBonusRatePercentage) {
        log.info("계좌 이자율 업데이트 시작: accountId={}, newBonusRate={}%",
            accountId, newBonusRatePercentage);

        // 입력 검증
        validateInterestRate(newBonusRatePercentage);

        // 계좌 조회
        Account account = loadAccountPort.findById(AccountId.of(accountId))
            .orElseThrow(() -> new AccountNotFoundException(
                Map.of("accountId", accountId)
            ));

        // 적금 계좌 확인
        if (!account.isSavingsAccount()) {
            log.warn("적금 계좌가 아님: accountId={}", accountId);
            // 적금 계좌가 아니어도 현재 보너스 금리 반환 (오류 없이 처리)
            return UpdateInterestRateResult.from(account);
        }

        // 보너스 금리 변환 및 업데이트
        BasisPoints newBonusRate = BasisPoints.fromPercentage(newBonusRatePercentage);
        BasisPoints previousBonusRate = account.getBonusRate();

        BasisPoints currentBonusRate = account.updateBonusRateIfHigher(newBonusRate, Instant.now());

        // 저장 (변경된 경우에만)
        if (!currentBonusRate.equals(previousBonusRate)) {
            saveAccountPort.save(account);
            log.info("계좌 이자율 업데이트 완료: accountId={}, {}% -> {}%",
                accountId, previousBonusRate.toPercentage(), currentBonusRate.toPercentage());
        } else {
            log.info("계좌 이자율 변경 없음 (기존 금리가 더 높음): accountId={}, current={}%, requested={}%",
                accountId, currentBonusRate.toPercentage(), newBonusRatePercentage);
        }

        return UpdateInterestRateResult.from(account);
    }

    /**
     * 이자율 입력값을 검증합니다.
     *
     * @param ratePercentage 검증할 이자율 (백분율)
     * @throws InvalidRateException 유효하지 않은 금리인 경우
     */
    private void validateInterestRate(Double ratePercentage) {
        if (ratePercentage == null) {
            throw new InvalidRateException(Map.of(
                "rate", "null",
                "reason", "RATE_NULL"
            ));
        }

        if (ratePercentage < 0.0 || ratePercentage > 100.0) {
            throw new InvalidRateException(Map.of(
                "rate", ratePercentage,
                "reason", "RATE_OUT_OF_RANGE",
                "validRange", "0.0 ~ 100.0"
            ));
        }

        // NaN, Infinity 체크
        if (!Double.isFinite(ratePercentage)) {
            throw new InvalidRateException(Map.of(
                "rate", ratePercentage,
                "reason", "RATE_NOT_FINITE"
            ));
        }
    }
}