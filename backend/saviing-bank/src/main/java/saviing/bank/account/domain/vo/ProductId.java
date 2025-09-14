package saviing.bank.account.domain.vo;

import java.util.Objects;

public record ProductId(Long value) {

    public ProductId {
        Objects.requireNonNull(value, "상품ID는 필수입니다");

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