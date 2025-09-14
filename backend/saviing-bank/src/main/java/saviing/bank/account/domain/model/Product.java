package saviing.bank.account.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import saviing.bank.account.domain.vo.ProductId;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    private ProductId id;
    private ProductCategory category;
    private String name;
    private String code;
    private String description;

    private Product(
        @NonNull ProductId id,
        @NonNull ProductCategory category,
        @NonNull String name,
        @NonNull String code,
        String description
    ) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public static Product of(
        @NonNull ProductId id,
        @NonNull ProductCategory category,
        @NonNull String name,
        @NonNull String code,
        String description
    ) {
        return new Product(id, category, name, code, description);
    }

    public static Product of(
        @NonNull ProductId id,
        @NonNull ProductCategory category,
        @NonNull String name,
        @NonNull String code
    ) {
        return new Product(id, category, name, code, null);
    }
}