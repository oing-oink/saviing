package saviing.bank.transaction.application.port.in;

import saviing.bank.transaction.application.port.in.result.TransactionResult;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 거래 조회 유즈케이스
 * 특정 거래 ID로 거래 정보를 조회하는 기능을 제공한다.
 */
public interface GetTransactionUseCase {

    /**
     * 거래 ID로 거래 정보를 조회한다
     *
     * @param transactionId 조회할 거래 ID
     * @return 거래 조회 결과
     */
    TransactionResult getTransaction(TransactionId transactionId);
}