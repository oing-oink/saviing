package saviing.bank.transaction.adapter.in.web.dto.request;

import lombok.Builder;

import saviing.bank.transaction.application.port.in.command.VoidTransactionCommand;

@Builder
public record VoidTransactionRequest(
    String reason
) {

    /**
     * Request를 VoidTransactionCommand로 변환하는 팩토리 메서드
     *
     * @param transactionId 거래 ID
     * @return VoidTransactionCommand 인스턴스
     */
    public VoidTransactionCommand toCommand(Long transactionId) {
        return VoidTransactionCommand.of(transactionId, reason);
    }
}