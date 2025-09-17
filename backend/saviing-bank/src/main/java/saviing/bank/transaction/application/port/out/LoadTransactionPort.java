package saviing.bank.transaction.application.port.out;

import java.util.List;
import java.util.Optional;

import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 거래 데이터 로드 포트
 * 영속성 계층에서 거래 데이터를 조회하는 기능을 제공한다.
 */
public interface LoadTransactionPort {

    /**
     * 거래 ID로 거래를 조회한다
     *
     * @param transactionId 조회할 거래 ID
     * @return 거래 엔티티 (Optional)
     */
    Optional<Transaction> loadTransaction(TransactionId transactionId);

    /**
     * 계좌 ID로 모든 거래 내역을 조회한다
     *
     * @param accountId 계좌 ID
     * @return 거래 내역 목록
     */
    List<Transaction> loadTransactionsByAccount(Long accountId);

    /**
     * 계좌 ID로 거래 내역을 페이지롌이션하여 조회한다
     *
     * @param accountId 계좌 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 거래 내역 목록
     */
    List<Transaction> loadTransactionsByAccount(Long accountId, int page, int size);
}
