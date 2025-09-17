package saviing.bank.transaction.domain.model.ledger;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.exception.InvalidLedgerStateException;

/**
 * 송금 원장의 단일 방향 엔트리.
 * 출금/입금 각각의 상태와 연관 거래 정보를 관리한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntry {

    private Long id;
    private Long accountId;
    private TransactionDirection direction;
    private MoneyWon amount;
    private LedgerEntryStatus status;
    private LocalDate valueDate;
    private Instant postedAt;
    private IdempotencyKey idempotencyKey;
    private TransactionId transactionId;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * LedgerEntry 생성자
     *
     * @param accountId 계좌 ID
     * @param direction 거래 방향 (출금/입금)
     * @param amount 거래 금액
     * @param valueDate 가치일
     * @param idempotencyKey 멱등키
     * @param now 생성 시각
     */
    private LedgerEntry(
        Long accountId,
        TransactionDirection direction,
        MoneyWon amount,
        LocalDate valueDate,
        IdempotencyKey idempotencyKey,
        Instant now
    ) {
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.direction = Objects.requireNonNull(direction, "direction must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.valueDate = Objects.requireNonNull(valueDate, "valueDate must not be null");
        this.idempotencyKey = idempotencyKey;
        this.status = LedgerEntryStatus.REQUESTED;
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 신규 송금 생성 시 호출되는 팩토리 메서드
     *
     * @param accountId 계좌 ID
     * @param direction 거래 방향
     * @param amount 거래 금액
     * @param valueDate 가치일
     * @param idempotencyKey 멱등키
     * @param now 생성 시각
     * @return 생성된 원장 엔트리
     */
    public static LedgerEntry create(
        Long accountId,
        TransactionDirection direction,
        MoneyWon amount,
        LocalDate valueDate,
        IdempotencyKey idempotencyKey,
        Instant now
    ) {
        return new LedgerEntry(accountId, direction, amount, valueDate, idempotencyKey, now);
    }

    /**
     * 저장소에서 불러온 엔트리를 복원한다
     *
     * @param id 엔트리 ID
     * @param accountId 계좌 ID
     * @param direction 거래 방향
     * @param amount 거래 금액
     * @param status 엔트리 상태
     * @param valueDate 가치일
     * @param postedAt 처리 시각
     * @param idempotencyKey 멱등키
     * @param transactionId 거래 ID
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 원장 엔트리
     */
    public static LedgerEntry restore(
        Long id,
        Long accountId,
        TransactionDirection direction,
        MoneyWon amount,
        LedgerEntryStatus status,
        LocalDate valueDate,
        Instant postedAt,
        IdempotencyKey idempotencyKey,
        TransactionId transactionId,
        Instant createdAt,
        Instant updatedAt
    ) {
        LedgerEntry entry = new LedgerEntry(accountId, direction, amount, valueDate, idempotencyKey, createdAt);
        entry.id = id;
        entry.status = status;
        entry.postedAt = postedAt;
        entry.transactionId = transactionId;
        entry.createdAt = createdAt;
        entry.updatedAt = updatedAt;
        return entry;
    }

    /**
     * 엔트리가 실제 거래와 매칭되어 POSTED 상태가 되었음을 표시한다
     *
     * @param transactionId 연결된 거래 ID
     * @param postedAt 처리 시각
     * @throws InvalidLedgerStateException 이미 무효화되거나 실패한 엔트리인 경우
     */
    public void markPosted(TransactionId transactionId, Instant postedAt) {
        if (status == LedgerEntryStatus.POSTED) {
            return;
        }
        if (status == LedgerEntryStatus.VOID || status == LedgerEntryStatus.FAILED) {
            throw new InvalidLedgerStateException("Cannot post ledger entry in state " + status);
        }
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId must not be null");
        this.postedAt = Objects.requireNonNull(postedAt, "postedAt must not be null");
        this.status = LedgerEntryStatus.POSTED;
        this.updatedAt = postedAt;
    }

    /**
     * 엔트리 처리가 실패했음을 기록한다
     *
     * @param failedAt 실패 시각
     * @throws InvalidLedgerStateException 이미 처리된 엔트리인 경우
     */
    public void markFailed(Instant failedAt) {
        if (status == LedgerEntryStatus.POSTED) {
            throw new InvalidLedgerStateException("Cannot fail already posted ledger entry");
        }
        this.status = LedgerEntryStatus.FAILED;
        this.updatedAt = failedAt;
    }

    /**
     * 엔트리를 무효 상태로 전환한다
     *
     * @param voidedAt 무효화 시각
     */
    public void voidEntry(Instant voidedAt) {
        this.status = LedgerEntryStatus.VOID;
        this.updatedAt = voidedAt;
    }

    /**
     * 읽기 전용 스냅샷으로 변환한다
     *
     * @return 원장 엔트리 스냅샷
     */
    public LedgerEntrySnapshot toSnapshot() {
        return new LedgerEntrySnapshot(
            id,
            accountId,
            direction,
            amount,
            status,
            valueDate,
            postedAt,
            idempotencyKey,
            transactionId,
            createdAt,
            updatedAt
        );
    }
}
