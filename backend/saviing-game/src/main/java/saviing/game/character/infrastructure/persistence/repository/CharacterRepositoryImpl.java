package saviing.game.character.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.infrastructure.persistence.entity.CharacterEntity;
import saviing.game.character.infrastructure.persistence.mapper.CharacterEntityMapper;
import saviing.game.character.domain.repository.CharacterRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CharacterRepository의 JPA 구현체
 * 도메인 Repository 인터페이스를 JPA를 사용해 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class CharacterRepositoryImpl implements CharacterRepository {

    private final CharacterJpaRepository jpaRepository;
    private final CharacterEntityMapper mapper;

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
                throw new IllegalArgumentException("업데이트할 캐릭터를 찾을 수 없습니다: " + character.getCharacterId().value());
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

    /**
     * 새로운 엔티티에 생성 시간을 설정합니다.
     * 
     * @param entity CharacterEntity
     * @return 생성 시간이 설정된 CharacterEntity
     */
    private CharacterEntity setCreationTime(CharacterEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        return CharacterEntity.builder()
            .characterId(entity.getCharacterId())
            .customerId(entity.getCustomerId())
            .accountId(entity.getAccountId())
            .connectionStatus(entity.getConnectionStatus())
            .connectionDate(entity.getConnectionDate())
            .terminationReason(entity.getTerminationReason())
            .terminatedAt(entity.getTerminatedAt())
            .coin(entity.getCoin())
            .fishCoin(entity.getFishCoin())
            .roomCount(entity.getRoomCount())
            .isActive(entity.getIsActive())
            .deactivatedAt(entity.getDeactivatedAt())
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
}