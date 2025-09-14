package saviing.bank.account.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.account.domain.vo.ProductConfiguration;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    private ProductId id;
    private ProductCategory category;
    private String name;
    private String code;
    private String description;
    private ProductConfiguration configuration;

    private Product(
        @NonNull ProductId id,
        @NonNull ProductCategory category,
        @NonNull String name,
        @NonNull String code,
        String description,
        ProductConfiguration configuration
    ) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.code = code;
        this.description = description;
        this.configuration = configuration;
    }

    public static Product of(
        @NonNull ProductId id,
        @NonNull ProductCategory category,
        @NonNull String name,
        @NonNull String code,
        String description,
        ProductConfiguration configuration
    ) {
        return new Product(id, category, name, code, description, configuration);
    }

    public static Product of(
        @NonNull ProductId id,
        @NonNull ProductCategory category,
        @NonNull String name,
        @NonNull String code,
        ProductConfiguration configuration
    ) {
        return new Product(id, category, name, code, null, configuration);
    }
}