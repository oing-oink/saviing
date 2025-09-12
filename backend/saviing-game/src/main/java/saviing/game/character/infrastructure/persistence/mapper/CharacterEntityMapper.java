package saviing.game.character.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.enums.ConnectionStatus;
import saviing.game.character.domain.model.vo.AccountConnection;
import saviing.game.character.domain.model.vo.AccountTermination;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CharacterLifecycle;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.domain.model.vo.GameStatus;
import saviing.game.character.infrastructure.persistence.entity.CharacterEntity;

import java.time.LocalDateTime;

/**
 * Character Entity와 Domain Model 간의 변환을 담당하는 Mapper
 */
@Component
public class CharacterEntityMapper {

    /**
     * CharacterEntity를 Character 도메인 객체로 변환합니다.
     * 
     * @param entity CharacterEntity
     * @return Character 도메인 객체
     */
    public Character toDomain(CharacterEntity entity) {
        if (entity == null) {
            return null;
        }

        return Character.builder()
            .characterId(entity.getCharacterId() != null ? CharacterId.of(entity.getCharacterId()) : null)
            .customerId(CustomerId.of(entity.getCustomerId()))
            .accountConnection(mapAccountConnection(entity))
            .gameStatus(mapGameStatus(entity))
            .characterLifecycle(mapCharacterLifecycle(entity))
            .build();
    }

    /**
     * Character 도메인 객체를 CharacterEntity로 변환합니다.
     * 
     * @param character Character 도메인 객체
     * @return CharacterEntity
     */
    public CharacterEntity toEntity(Character character) {
        if (character == null) {
            return null;
        }

        return CharacterEntity.builder()
            .characterId(character.getCharacterId() != null ? character.getCharacterId().value() : null)
            .customerId(character.getCustomerId().value())
            .accountId(character.getAccountConnection().accountId())
            .connectionStatus(character.getAccountConnection().connectionStatus())
            .connectionDate(character.getAccountConnection().connectionDate())
            .terminationReason(extractTerminationReason(character.getAccountConnection()))
            .terminatedAt(extractTerminatedAt(character.getAccountConnection()))
            .coin(character.getGameStatus().coin())
            .fishCoin(character.getGameStatus().fishCoin())
            .roomCount(character.getGameStatus().roomCount())
            .isActive(character.getGameStatus().isActive())
            .deactivatedAt(character.getCharacterLifecycle().deactivatedAt())
            .createdAt(character.getCharacterLifecycle().createdAt())
            .updatedAt(character.getCharacterLifecycle().updatedAt())
            .build();
    }

    /**
     * 기존 CharacterEntity를 Character 도메인 객체의 데이터로 업데이트합니다.
     * 
     * @param entity 기존 CharacterEntity
     * @param character Character 도메인 객체
     */
    public void updateEntity(CharacterEntity entity, Character character) {
        if (entity == null || character == null) {
            return;
        }

        entity.updateEntity(
            character.getAccountConnection().accountId(),
            character.getAccountConnection().connectionStatus(),
            character.getAccountConnection().connectionDate(),
            extractTerminationReason(character.getAccountConnection()),
            extractTerminatedAt(character.getAccountConnection()),
            character.getGameStatus().coin(),
            character.getGameStatus().fishCoin(),
            character.getGameStatus().roomCount(),
            character.getGameStatus().isActive(),
            character.getCharacterLifecycle().deactivatedAt()
        );
    }

    private AccountConnection mapAccountConnection(CharacterEntity entity) {
        AccountTermination termination = null;
        
        if (entity.getConnectionStatus() == ConnectionStatus.TERMINATED) {
            termination = new AccountTermination(
                entity.getTerminationReason(),
                entity.getTerminatedAt()
            );
        }

        return new AccountConnection(
            entity.getAccountId(),
            entity.getConnectionStatus(),
            entity.getConnectionDate(),
            termination
        );
    }

    private GameStatus mapGameStatus(CharacterEntity entity) {
        return new GameStatus(
            entity.getCoin(),
            entity.getFishCoin(),
            entity.getRoomCount(),
            entity.getIsActive()
        );
    }

    private CharacterLifecycle mapCharacterLifecycle(CharacterEntity entity) {
        return new CharacterLifecycle(entity.getCreatedAt(), entity.getUpdatedAt(), entity.getDeactivatedAt());
    }

    private String extractTerminationReason(AccountConnection accountConnection) {
        return accountConnection.terminationInfo() != null ? 
            accountConnection.terminationInfo().reason() : null;
    }

    private LocalDateTime extractTerminatedAt(AccountConnection accountConnection) {
        return accountConnection.terminationInfo() != null ? 
            accountConnection.terminationInfo().terminatedAt() : null;
    }
}