package saviing.bank.transaction.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import saviing.bank.transaction.application.port.in.GetTransactionUseCase;
import saviing.bank.transaction.application.port.in.GetTransactionsByAccountUseCase;
import saviing.bank.transaction.application.port.in.result.TransactionResult;
import saviing.bank.transaction.exception.TransactionNotFoundException;

import java.util.Map;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.vo.TransactionId;

/**
 * 거래 조회 서비스
 * 거래 조회 및 계좌별 거래 내역 조회 기능을 제공하는 애플리케이션 서비스이다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionQueryService implements GetTransactionUseCase, GetTransactionsByAccountUseCase {

    private final LoadTransactionPort loadTransactionPort;

    /**
     * 거래 ID로 거래 정보를 조회한다
     *
     * @param transactionId 조회할 거래 ID
     * @return 거래 조회 결과
     * @throws TransactionNotFoundException 거래를 찾을 수 없는 경우
     */
    @Override
    public TransactionResult getTransaction(TransactionId transactionId) {
        Transaction transaction = loadTransactionPort.loadTransaction(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(
                Map.of("transactionId", transactionId.value())
            ));

        return mapToResult(transaction);
    }

    /**
     * 계좌 ID로 모든 거래 내역을 조회한다
     *
     * @param accountId 계좌 ID
     * @return 거래 내역 목록
     */
    @Override
    public List<TransactionResult> getTransactionsByAccount(Long accountId) {
        List<Transaction> transactions = loadTransactionPort.loadTransactionsByAccount(accountId);
        return transactions.stream()
            .map(this::mapToResult)
            .toList();
    }

    /**
     * 계좌 ID로 거래 내역을 페이지네이션하여 조회한다
     *
     * @param accountId 계좌 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 거래 내역 목록
     */
    @Override
    public List<TransactionResult> getTransactionsByAccount(Long accountId, int page, int size) {
        List<Transaction> transactions = loadTransactionPort.loadTransactionsByAccount(accountId, page, size);
        return transactions.stream()
            .map(this::mapToResult)
            .toList();
    }


    /**
     * Transaction 도메인 엔티티를 TransactionResult DTO로 변환한다
     *
     * @param transaction 거래 도메인 엔티티
     * @return 거래 조회 결과 DTO
     */
    private TransactionResult mapToResult(Transaction transaction) {
        return TransactionResult.from(transaction);
    }
}