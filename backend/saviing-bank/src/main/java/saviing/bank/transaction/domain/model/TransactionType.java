package saviing.bank.transaction.domain.model;

public enum TransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    TRANSFER_OUT("이체 출금"),
    TRANSFER_IN("이체 입금"),
    INTEREST("이자 지급"),
    FEE("수수료"),
    REVERSAL("취소"),
    ADJUSTMENT("조정");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}