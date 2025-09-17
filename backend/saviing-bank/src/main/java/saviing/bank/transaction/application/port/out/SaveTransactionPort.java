package saviing.bank.transaction.application.port.out;

import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 거래 데이터 저장 포트
 * 영속성 계층에서 거래 데이터를 저장하고 업데이트하는 기능을 제공한다.
 */
public interface SaveTransactionPort {

    /**
     * 새로운 거래를 저장하고 생성된 ID를 반환한다
     *
     * @param transaction 저장할 거래 엔티티
     * @return 생성된 거래 ID
     */
    TransactionId saveTransaction(Transaction transaction);

    /**
     * 기존 거래 정보를 업데이트한다
     *
     * @param transaction 업데이트할 거래 엔티티
     */
    void updateTransaction(Transaction transaction);
}