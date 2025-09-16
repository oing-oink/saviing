package saviing.game.item.domain.model.enums;

import saviing.game.item.domain.model.enums.category.Category;

import java.util.Arrays;
import java.util.List;

/**
 * 아이템 타입 열거형
 * 아이템의 주요 분류를 정의하고 각 타입별 허용되는 카테고리를 관리합니다.
 */
public enum ItemType {

    /**
     * 펫 아이템
     * 캐릭터와 함께하는 동반자 아이템
     */
    PET("펫") {
        @Override
        public List<Category> getCategories() {
            return Arrays.asList(Pet.values());
        }

        @Override
        public boolean isValidCategory(Category category) {
            return category instanceof Pet;
        }
    },

    /**
     * 액세서리 아이템
     * 캐릭터가 착용하는 장식 아이템
     */
    ACCESSORY("액세서리") {
        @Override
        public List<Category> getCategories() {
            return Arrays.asList(Accessory.values());
        }

        @Override
        public boolean isValidCategory(Category category) {
            return category instanceof Accessory;
        }
    },

    /**
     * 데코레이션 아이템
     * 방을 꾸미는 장식 아이템
     */
    DECORATION("데코레이션") {
        @Override
        public List<Category> getCategories() {
            return Arrays.asList(Decoration.values());
        }

        @Override
        public boolean isValidCategory(Category category) {
            return category instanceof Decoration;
        }
    };

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 해당 타입에서 사용 가능한 모든 카테고리를 반환합니다.
     *
     * @return 사용 가능한 카테고리 목록
     */
    public abstract List<Category> getCategories();

    /**
     * 주어진 카테고리가 이 타입에 유효한지 확인합니다.
     *
     * @param category 확인할 카테고리
     * @return 유효한 카테고리인지 여부
     */
    public abstract boolean isValidCategory(Category category);

    /**
     * 카테고리가 이 타입에 속하는지 확인합니다.
     *
     * @param category 확인할 카테고리
     * @return 해당 타입에 속하는지 여부
     */
    public boolean hasCategory(Category category) {
        return category != null && category.belongsTo(this);
    }
}