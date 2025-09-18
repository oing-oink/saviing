package saviing.bank.transaction.application.port.in.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.transfer.TransferType;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

class TransferCommandTest {

    @Test
    void builder_기본값이_자동으로_설정된다() {
        LocalDate today = LocalDate.now();

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(1L)
            .targetAccountId(2L)
            .amount(MoneyWon.of(1000L))
            .build();

        assertThat(command.valueDate()).isEqualTo(today);
        assertThat(command.transferType()).isEqualTo(TransferType.INTERNAL);
        assertThat(command.requestedAt()).isNotNull();
    }

    @Test
    void builder_사용자가_설정한_값은_우선한다() {
        LocalDate customDate = LocalDate.now().plusDays(1);
        Instant requestedAt = Instant.now().minusSeconds(5);

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(1L)
            .targetAccountId(2L)
            .amount(MoneyWon.of(1000L))
            .valueDate(customDate)
            .transferType(TransferType.EXTERNAL_OUTBOUND)
            .requestedAt(requestedAt)
            .memo("custom")
            .idempotencyKey(IdempotencyKey.of("key"))
            .build();

        assertThat(command.valueDate()).isEqualTo(customDate);
        assertThat(command.transferType()).isEqualTo(TransferType.EXTERNAL_OUTBOUND);
        assertThat(command.requestedAt()).isEqualTo(requestedAt);
        assertThat(command.memo()).isEqualTo("custom");
        assertThat(command.idempotencyKey().value()).isEqualTo("key");
    }
}
