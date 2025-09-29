package saviing.bank.account.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

import saviing.bank.account.application.port.in.result.CreateAccountResult;

@Schema(description = "계좌 생성 응답", accessMode = Schema.AccessMode.READ_ONLY)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateAccountResponse(
    @Schema(description = "계좌 ID", example = "100001")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long accountId,

    @Schema(description = "계좌번호", example = "11098765432198765")
    String accountNumber,

    @Schema(description = "고객 ID", example = "1001")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long customerId,

    @Schema(description = "상품 정보")
    ProductDto product,

    @Schema(
        description = "복리 계산 방식",
        example = "SIMPLE",
        allowableValues = {"SIMPLE", "DAILY", "MONTH", "YEAR"}
    )
    String compoundingType,

    @Schema(
        description = "계좌 상태",
        example = "ACTIVE",
        allowableValues = {"ACTIVE", "FROZEN", "CLOSED"}
    )
    String status,

    @Schema(description = "개설일시", example = "2024-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant openedAt,

    @Schema(description = "해지일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant closedAt,

    @Schema(description = "마지막 이자 발생일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant lastAccrualAt,

    @Schema(description = "마지막 금리 변경일시", example = "2024-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant lastRateChangeAt,

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant createdAt,

    @Schema(description = "수정일시", example = "2024-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    Instant updatedAt,

    @Schema(description = "잔액(원 단위, 정수)", example = "0")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long balance,

    @Schema(description = "발생 이자", example = "0.00")
    BigDecimal interestAccrued,

    @Schema(description = "기본 금리 (퍼센트 단위, 예: 1.50 = 1.5%)", example = "1.50")
    BigDecimal baseRatePercent,

    @Schema(description = "보너스 금리 (퍼센트 단위, 예: 0.50 = 0.5%)", example = "0.00")
    BigDecimal bonusRatePercent,

    @Schema(description = "총 금리 (퍼센트 단위, 예: 2.00 = 2.0%)", example = "1.50")
    BigDecimal totalRatePercent,

    @Schema(description = "적금 정보 (적금 계좌가 아닌 경우 null)")
    SavingsDto savings
) {

    
    public static CreateAccountResponse from(CreateAccountResult result) {
        return CreateAccountResponse.builder()
            .accountId(result.accountId())
            .accountNumber(result.accountNumber())
            .customerId(result.customerId())
            .product(ProductDto.from(result.productInfo()))
            .compoundingType(result.compoundingType())
            .status(result.status())
            .openedAt(result.openedAt())
            .closedAt(result.closedAt())
            .lastAccrualAt(result.lastAccrualAt())
            .lastRateChangeAt(result.lastRateChangeAt())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .balance(result.balance())
            .interestAccrued(result.interestAccrued())
            .baseRatePercent(result.baseRatePercent())
            .bonusRatePercent(result.bonusRatePercent())
            .totalRatePercent(result.totalRatePercent())
            .savings(SavingsDto.from(result.savingsInfo()))
            .build();
    }
}