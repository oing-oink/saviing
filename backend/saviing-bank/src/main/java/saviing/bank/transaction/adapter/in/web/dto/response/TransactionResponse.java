package saviing.bank.transaction.adapter.in.web.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;

import saviing.bank.transaction.application.port.in.result.TransactionResult;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "거래 정보 응답")
public record TransactionResponse(
    @Schema(description = "거래 ID", example = "1")
    Long transactionId,

    @Schema(description = "계좌 ID", example = "1")
    Long accountId,

    @Schema(description = "거래 유형", example = "TRANSFER_OUT", allowableValues = {"TRANSFER_OUT", "TRANSFER_IN", "REVERSAL"})
    String transactionType,

    @Schema(description = "거래 방향", example = "CREDIT", allowableValues = {"CREDIT", "DEBIT"})
    String direction,

    @Schema(description = "거래 금액 (원)", example = "100000")
    BigDecimal amount,

    @Schema(description = "거래 후 잔액 (원)", example = "9900000")
    BigDecimal balanceAfter,

    @Schema(description = "가치일자", example = "2024-01-15")
    LocalDate valueDate,

    @Schema(description = "거래 처리 일시", example = "2024-01-15T14:30:00Z")
    Instant postedAt,

    @Schema(description = "거래 상태", example = "POSTED", allowableValues = {"POSTED", "VOID"})
    String status,

    @Schema(description = "연관 거래 ID (이체 등)", example = "2")
    Long relatedTransactionId,

    @Schema(description = "거래 설명", example = "Rent transfer")
    String description,

    @Schema(description = "생성일시", example = "2024-01-15T14:30:00Z")
    Instant createdAt,

    @Schema(description = "수정일시", example = "2024-01-15T14:30:00Z")
    Instant updatedAt
) {

    public static TransactionResponse from(@NonNull TransactionResult result) {
        return TransactionResponse.builder()
            .transactionId(result.transactionId())
            .accountId(result.accountId())
            .transactionType(result.transactionType())
            .direction(result.direction())
            .amount(result.amount())
            .balanceAfter(result.balanceAfter())
            .valueDate(result.valueDate())
            .postedAt(result.postedAt())
            .status(result.status())
            .relatedTransactionId(result.relatedTransactionId())
            .description(result.description())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .build();
    }
}
