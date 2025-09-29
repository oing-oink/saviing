package saviing.bank.account.domain.vo;

import lombok.NonNull;

public record ProductId(@NonNull Long value) {

    public ProductId {
        if (value <= 0) {
            throw new IllegalArgumentException("상품ID는 양수여야 합니다");
        }
    }

    public static ProductId of(Long value) {
        return new ProductId(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}