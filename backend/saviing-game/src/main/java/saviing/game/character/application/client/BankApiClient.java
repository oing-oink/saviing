package saviing.game.character.application.client;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 은행 서비스 API 통신 클라이언트 인터페이스.
 *
 * 게임 서비스에서 은행 서비스의 계좌 관련 기능을 호출하기 위한 클라이언트입니다.
 * 주로 이자율 설정 기능을 제공합니다.
 */
public interface BankApiClient {

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
    Optional<BigDecimal> updateAccountInterestRate(Long accountId, BigDecimal newBonusRate);
}