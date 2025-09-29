package saviing.bank.account.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 계좌 상태를 변경하기 위한 요청입니다.
 */
public record UpdateAccountStatusRequest(
    @Schema(description = "변경할 계좌 상태", example = "CLOSED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "계좌 상태는 필수입니다")
    String status
) {}
