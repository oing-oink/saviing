package saviing.bank.transaction.application.port.in.command;

import lombok.Builder;
import lombok.NonNull;

import saviing.bank.transaction.domain.vo.TransactionId;

@Builder
public record VoidTransactionCommand(
    @NonNull TransactionId transactionId,
    String reason
) {

    /**
     * 웹 요청 파라미터로부터 VoidTransactionCommand를 생성하는 팩토리 메서드
     *
     * @param transactionId 거래 ID
     * @param reason 무효화 사유 (null 가능)
     * @return VoidTransactionCommand 인스턴스
     */
    public static VoidTransactionCommand of(Long transactionId, String reason) {
        return new VoidTransactionCommand(
            TransactionId.of(transactionId),
            reason
        );
    }
}