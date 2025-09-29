package saviing.bank.transaction.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import saviing.bank.common.vo.MoneyWon;

@DisplayName("Transaction 도메인 테스트")
class TransactionTests {

    @Test
    @DisplayName("거래를 생성할 수 있다")
    void 거래_생성_성공() {
        // Given
        Long accountId = 1L;
        TransactionType transactionType = TransactionType.TRANSFER_IN;
        TransactionDirection direction = TransactionDirection.CREDIT;
        MoneyWon amount = MoneyWon.of(10000);
        LocalDate valueDate = LocalDate.now();
        Instant postedAt = Instant.now();
        String description = "테스트 입금";

        // When
        Transaction transaction = Transaction.create(
            accountId, transactionType, direction, amount, MoneyWon.of(5000000L),
            valueDate, postedAt, description
        );

        // Then
        assertThat(transaction.getAccountId()).isEqualTo(accountId);
        assertThat(transaction.getTransactionType()).isEqualTo(transactionType);
        assertThat(transaction.getDirection()).isEqualTo(direction);
        assertThat(transaction.getAmount()).isEqualTo(amount);
        assertThat(transaction.getBalanceAfter()).isEqualTo(MoneyWon.of(5000000L));
        assertThat(transaction.getValueDate()).isEqualTo(valueDate);
        assertThat(transaction.getPostedAt()).isEqualTo(postedAt);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.POSTED);
        assertThat(transaction.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("거래 유형과 방향이 일치하지 않으면 예외가 발생한다")
    void 거래_유형_방향_불일치_예외() {
        // Given
        Long accountId = 1L;
        TransactionType transactionType = TransactionType.TRANSFER_IN; // CREDIT이어야 함
        TransactionDirection direction = TransactionDirection.DEBIT; // 잘못된 방향
        MoneyWon amount = MoneyWon.of(10000);
        LocalDate valueDate = LocalDate.now();
        Instant postedAt = Instant.now();

        // When & Then
        assertThatThrownBy(() -> Transaction.create(
            accountId, transactionType, direction, amount, MoneyWon.of(5000000L),
            valueDate, postedAt, null
        )).isInstanceOf(saviing.bank.transaction.exception.InvalidTransactionStateException.class)
          .hasMessageContaining("거래 유형")
          .hasMessageContaining("방향이어야 합니다");
    }

    @Test
    @DisplayName("거래를 무효화할 수 있다")
    void 거래_무효화_성공() {
        // Given
        Transaction transaction = createTestTransaction();
        Instant voidedAt = Instant.now();

        // When
        transaction.voidTransaction(voidedAt);

        // Then
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.VOID);
        assertThat(transaction.getUpdatedAt()).isEqualTo(voidedAt);
    }

    @Test
    @DisplayName("이미 무효화된 거래를 다시 무효화하면 예외가 발생한다")
    void 이미_무효화된_거래_재무효화_예외() {
        // Given
        Transaction transaction = createTestTransaction();
        transaction.voidTransaction(Instant.now());

        // When & Then
        assertThatThrownBy(() -> transaction.voidTransaction(Instant.now()))
            .isInstanceOf(saviing.bank.transaction.exception.TransactionAlreadyVoidException.class)
            .hasMessageContaining("이미 무효화된 거래입니다");
    }

    @Test
    @DisplayName("CREDIT 거래의 잔액 영향을 계산할 수 있다")
    void CREDIT_거래_잔액영향_계산() {
        // Given
        Transaction transaction = Transaction.create(
            1L,
            TransactionType.TRANSFER_IN,
            TransactionDirection.CREDIT,
            MoneyWon.of(10000),
            MoneyWon.of(5010000L),
            LocalDate.now(),
            Instant.now(),
            null
        );

        // When
        MoneyWon impact = transaction.getBalanceImpact();

        // Then
        assertThat(impact.amount()).isEqualTo(10000);
    }

    @Test
    @DisplayName("DEBIT 거래의 잔액 영향을 계산할 수 있다")
    void DEBIT_거래_잔액영향_계산() {
        // Given
        Transaction transaction = Transaction.create(
            1L,
            TransactionType.TRANSFER_OUT,
            TransactionDirection.DEBIT,
            MoneyWon.of(5000),
            MoneyWon.of(4995000L),
            LocalDate.now(),
            Instant.now(),
            null
        );

        // When
        MoneyWon impact = transaction.getBalanceImpact();

        // Then
        assertThat(impact.amount()).isEqualTo(-5000);
    }

    @Test
    @DisplayName("무효화된 거래의 잔액 영향은 0이다")
    void 무효화된_거래_잔액영향_0() {
        // Given
        Transaction transaction = createTestTransaction();
        transaction.voidTransaction(Instant.now());

        // When
        MoneyWon impact = transaction.getBalanceImpact();

        // Then
        assertThat(impact.amount()).isEqualTo(0);
    }

    private Transaction createTestTransaction() {
        return Transaction.create(
            1L,
            TransactionType.TRANSFER_IN,
            TransactionDirection.CREDIT,
            MoneyWon.of(10000),
            MoneyWon.of(5010000L),
            LocalDate.now(),
            Instant.now(),
            "테스트 거래"
        );
    }
}
