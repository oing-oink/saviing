package saviing.bank.transaction.application.port.in;

import java.util.List;

import saviing.bank.transaction.application.port.in.result.TransactionResult;

/**
 * 계좌별 거래 내역 조회 유즈케이스
 * 계좌 ID 또는 계좌번호로 거래 내역을 조회하는 기능을 제공한다.
 */
public interface GetTransactionsByAccountUseCase {

    /**
     * 계좌 ID로 모든 거래 내역을 조회한다
     *
     * @param accountId 계좌 ID
     * @return 거래 내역 목록
     */
    List<TransactionResult> getTransactionsByAccount(Long accountId);

    /**
     * 계좌 ID로 거래 내역을 페이지롌이션하여 조회한다
     *
     * @param accountId 계좌 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 거래 내역 목록
     */
    List<TransactionResult> getTransactionsByAccount(Long accountId, int page, int size);

    /**
     * 계좌번호로 모든 거래 내역을 조회한다
     *
     * @param accountNumber 계좌번호
     * @return 거래 내역 목록
     */
    List<TransactionResult> getTransactionsByAccount(String accountNumber);

    /**
     * 계좌번호로 거래 내역을 페이지롌이션하여 조회한다
     *
     * @param accountNumber 계좌번호
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 거래 내역 목록
     */
    List<TransactionResult> getTransactionsByAccount(String accountNumber, int page, int size);
}