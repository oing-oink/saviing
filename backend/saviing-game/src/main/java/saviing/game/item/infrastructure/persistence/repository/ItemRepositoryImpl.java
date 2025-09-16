package saviing.game.item.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.enums.category.Category;
import saviing.game.item.domain.model.vo.ItemId;
import saviing.game.item.domain.repository.ItemRepository;
import saviing.game.item.infrastructure.persistence.entity.ItemEntity;
import saviing.game.item.infrastructure.persistence.mapper.ItemEntityMapper;

import java.util.List;
import java.util.Optional;

/**
 * ItemRepository 도메인 인터페이스의 구현체
 * JPA를 사용하여 아이템 데이터의 영속성을 관리합니다.
 */
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;
    private final ItemEntityMapper itemEntityMapper;

    @Override
    public Item save(Item item) {
        ItemEntity entity = itemEntityMapper.toEntity(item);
        ItemEntity savedEntity = itemJpaRepository.save(entity);
        return itemEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Item> findById(ItemId itemId) {
        return itemJpaRepository.findById(itemId.value())
            .map(itemEntityMapper::toDomain);
    }

    @Override
    public List<Item> findByType(ItemType itemType) {
        List<ItemEntity> entities = itemJpaRepository.findByItemType(itemType.name());
        return entities.stream()
            .map(itemEntityMapper::toDomain)
            .toList();
    }

    @Override
    public List<Item> findByCategory(Category category) {
        String categoryString = formatCategoryForQuery(category);
        List<ItemEntity> entities = itemJpaRepository.findByItemCategory(categoryString);
        return entities.stream()
            .map(itemEntityMapper::toDomain)
            .toList();
    }

    @Override
    public List<Item> findByRarity(Rarity rarity) {
        List<ItemEntity> entities = itemJpaRepository.findByRarity(rarity.name());
        return entities.stream()
            .map(itemEntityMapper::toDomain)
            .toList();
    }

    @Override
    public List<Item> findAllAvailable() {
        List<ItemEntity> entities = itemJpaRepository.findByIsAvailableTrue();
        return entities.stream()
            .map(itemEntityMapper::toDomain)
            .toList();
    }

    @Override
    public List<Item> findAvailableByType(ItemType itemType) {
        List<ItemEntity> entities = itemJpaRepository.findByItemTypeAndIsAvailableTrue(itemType.name());
        return entities.stream()
            .map(itemEntityMapper::toDomain)
            .toList();
    }

    @Override
    public List<Item> findAvailableByCategory(Category category) {
        String categoryString = formatCategoryForQuery(category);
        List<ItemEntity> entities = itemJpaRepository.findByItemCategoryAndIsAvailableTrue(categoryString);
        return entities.stream()
            .map(itemEntityMapper::toDomain)
            .toList();
    }

    @Override
    public List<Item> findAll() {
        List<ItemEntity> entities = itemJpaRepository.findAll();
        return entities.stream()
            .map(itemEntityMapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsById(ItemId itemId) {
        return itemJpaRepository.existsById(itemId.value());
    }

    @Override
    public void deleteById(ItemId itemId) {
        itemJpaRepository.deleteById(itemId.value());
    }


    @Override
    public List<Item> findByNameContaining(String keyword) {
        List<ItemEntity> entities = itemJpaRepository.findByItemNameContainingIgnoreCase(keyword);
        return entities.stream()
            .map(itemEntityMapper::toDomain)
            .toList();
    }

    /**
     * Category를 데이터베이스 쿼리용 문자열로 변환합니다.
     *
     * @param category Category 객체
     * @return 쿼리용 문자열
     */
    private String formatCategoryForQuery(Category category) {
        if (category instanceof saviing.game.item.domain.model.enums.Pet pet) {
            return "Pet." + pet.name();
        } else if (category instanceof saviing.game.item.domain.model.enums.Accessory accessory) {
            return "Accessory." + accessory.name();
        } else if (category instanceof saviing.game.item.domain.model.enums.Decoration decoration) {
            return "Decoration." + decoration.name();
        } else {
            throw new IllegalArgumentException("지원하지 않는 카테고리 타입: " + category.getClass());
        }
    }
}