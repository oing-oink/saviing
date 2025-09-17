package saviing.bank.transaction.application.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 송금 관련 도메인 이벤트를 수신하여 모니터링 로그를 남기는 리스너.
 */
@Slf4j
@Component
public class TransferEventListener {

    /**
     * 송금이 커밋된 이후 성공 로그를 남긴다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSettled(TransferSettledEvent event) {
        log.info("[TransferSettled] transferId={}, debitTxId={}, creditTxId={}, amount={}, type={}, settledAt={}",
            event.transferId().value(),
            event.debitTransactionId() != null ? event.debitTransactionId().value() : null,
            event.creditTransactionId() != null ? event.creditTransactionId().value() : null,
            event.amount().amount(),
            event.transferType(),
            event.settledAt());
    }

    /**
     * 송금 실패 이벤트를 즉시 수신하여 경고 로그를 남긴다.
     */
    @EventListener
    public void handleFailed(TransferFailedEvent event) {
        log.warn("[TransferFailed] transferId={}, status={}, reason={}, failedAt={}, cause={}",
            event.transferId().value(),
            event.status(),
            event.reason(),
            event.failedAt(),
            event.cause() != null ? event.cause().getClass().getSimpleName() : "none");
    }
}
