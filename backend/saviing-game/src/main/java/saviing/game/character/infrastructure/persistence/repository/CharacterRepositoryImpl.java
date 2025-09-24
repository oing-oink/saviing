package saviing.game.character.infrastructure.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saviing.game.character.domain.exception.CharacterNotFoundException;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.domain.repository.CharacterRepository;
import saviing.game.character.infrastructure.persistence.entity.CharacterEntity;
import saviing.game.character.infrastructure.persistence.mapper.CharacterEntityMapper;

/**
 * CharacterRepository의 JPA 구현체
 * 도메인 Repository 인터페이스를 JPA를 사용해 구현합니다.
 */
@RequiredArgsConstructor
@Repository
public class CharacterRepositoryImpl implements CharacterRepository {

    private final CharacterJpaRepository jpaRepository;
    private final CharacterEntityMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Character save(Character character) {
        if (character.getCharacterId() == null) {
            // 새로운 캐릭터 생성
            CharacterEntity entity = mapper.toEntity(character);
            entity = setCreationTime(entity);
            CharacterEntity savedEntity = jpaRepository.save(entity);
            return mapper.toDomain(savedEntity);
        } else {
            // 기존 캐릭터 업데이트
            Optional<CharacterEntity> existingEntity = jpaRepository.findById(character.getCharacterId().value());
            if (existingEntity.isPresent()) {
                CharacterEntity entity = existingEntity.get();
                mapper.updateEntity(entity, character);
                CharacterEntity savedEntity = jpaRepository.save(entity);
                return mapper.toDomain(savedEntity);
            } else {
                throw new CharacterNotFoundException(character.getCharacterId().value());
            }
        }
    }

    @Override
    public Optional<Character> findById(CharacterId characterId) {
        return jpaRepository.findById(characterId.value())
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Character> findActiveCharacterByCustomerId(CustomerId customerId) {
        return jpaRepository.findActiveByCustomerId(customerId.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<Character> findAllByCustomerId(CustomerId customerId) {
        return jpaRepository.findAllByCustomerId(customerId.value())
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Character> findAllActiveCharacters() {
        return jpaRepository.findAllByIsActiveTrue()
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByCustomerId(CustomerId customerId) {
        return jpaRepository.existsByCustomerId(customerId.value());
    }

    @Override
    public Integer findTopPetLevelSumByCharacterId(CharacterId characterId, int limit) {
        if (limit <= 0) {
            return 0;
        }

        String sql = """
            SELECT COALESCE(SUM(p.level), 0) as total_level_sum
            FROM (
                SELECT p.level, ROW_NUMBER() OVER (ORDER BY p.level DESC) as rn
                FROM pet_inventory pi
                JOIN pet p ON pi.inventory_item_id = p.inventory_item_id
                WHERE pi.character_id = :characterId
            ) p
            WHERE p.rn <= :limit
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("characterId", characterId.value());
        query.setParameter("limit", limit);

        Number result = (Number) query.getSingleResult();
        return result != null ? result.intValue() : 0;
    }

    @Override
    public Map<String, Integer> findTopRaritySumByCharacterIdAndCategory(CharacterId characterId, int limit) {
        Map<String, Integer> categoryRarityMap = new HashMap<>();

        if (limit <= 0) {
            return categoryRarityMap;
        }

        String petSql = """
            SELECT
                grouped.item_category,
                COALESCE(SUM(grouped.rarity_value), 0) as total_rarity_sum
            FROM (
                SELECT
                    i.item_category,
                    CASE i.rarity
                        WHEN 'COMMON' THEN 1
                        WHEN 'RARE' THEN 2
                        WHEN 'EPIC' THEN 3
                        WHEN 'LEGENDARY' THEN 4
                        ELSE 0
                    END as rarity_value,
                    ROW_NUMBER() OVER (PARTITION BY i.item_category ORDER BY
                        CASE i.rarity
                            WHEN 'LEGENDARY' THEN 4
                            WHEN 'EPIC' THEN 3
                            WHEN 'RARE' THEN 2
                            WHEN 'COMMON' THEN 1
                            ELSE 0
                        END DESC) as rn
                FROM pet_inventory pi
                JOIN items i ON pi.item_id = i.item_id
                WHERE pi.character_id = :characterId
            ) grouped
            WHERE grouped.rn <= :limit
            GROUP BY grouped.item_category
            ORDER BY total_rarity_sum DESC
        """;

        Query petQuery = entityManager.createNativeQuery(petSql);
        petQuery.setParameter("characterId", characterId.value());
        petQuery.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> petResults = petQuery.getResultList();
        for (Object[] row : petResults) {
            String category = (String) row[0];
            Number raritySum = (Number) row[1];
            categoryRarityMap.put(category, raritySum != null ? raritySum.intValue() : 0);
        }

        String decorationSql = """
            SELECT
                grouped.item_category,
                COALESCE(SUM(grouped.rarity_value), 0) as total_rarity_sum
            FROM (
                SELECT
                    i.item_category,
                    CASE i.rarity
                        WHEN 'COMMON' THEN 1
                        WHEN 'RARE' THEN 2
                        WHEN 'EPIC' THEN 3
                        WHEN 'LEGENDARY' THEN 4
                        ELSE 0
                    END as rarity_value,
                    ROW_NUMBER() OVER (PARTITION BY i.item_category ORDER BY
                        CASE i.rarity
                            WHEN 'LEGENDARY' THEN 4
                            WHEN 'EPIC' THEN 3
                            WHEN 'RARE' THEN 2
                            WHEN 'COMMON' THEN 1
                            ELSE 0
                        END DESC) as rn
                FROM decoration_inventory di
                JOIN items i ON di.item_id = i.item_id
                WHERE di.character_id = :characterId
            ) grouped
            WHERE grouped.rn <= :limit
            GROUP BY grouped.item_category
            ORDER BY total_rarity_sum DESC
        """;

        Query decorationQuery = entityManager.createNativeQuery(decorationSql);
        decorationQuery.setParameter("characterId", characterId.value());
        decorationQuery.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> decorationResults = decorationQuery.getResultList();
        for (Object[] row : decorationResults) {
            String category = (String) row[0];
            Number raritySum = (Number) row[1];
            categoryRarityMap.put(category, raritySum != null ? raritySum.intValue() : 0);
        }

        return categoryRarityMap;
    }

    /**
     * 새로운 엔티티에 생성 시간을 설정합니다.
     * JPA가 자동으로 타임스탬프를 설정하므로 엔티티를 그대로 반환합니다.
     *
     * @param entity CharacterEntity
     * @return 입력받은 CharacterEntity (타임스탬프는 JPA가 자동 설정)
     */
    private CharacterEntity setCreationTime(CharacterEntity entity) {
        // JPA @PrePersist에서 자동으로 타임스탬프가 설정됨
        return entity;
    }
}