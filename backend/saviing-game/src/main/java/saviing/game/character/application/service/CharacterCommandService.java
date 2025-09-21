package saviing.game.character.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.character.application.dto.command.AddCoinsCommand;
import saviing.game.character.application.dto.command.CancelAccountConnectionCommand;
import saviing.game.character.application.dto.command.CompleteAccountConnectionCommand;
import saviing.game.character.application.dto.command.ConnectAccountCommand;
import saviing.game.character.application.dto.command.CreateCharacterCommand;
import saviing.game.character.application.dto.command.DeactivateCharacterCommand;
import saviing.game.character.application.dto.command.DebitCoinsCommand;
import saviing.game.character.application.dto.command.HandleAccountTerminatedCommand;
import saviing.game.character.application.dto.command.IncreaseRoomCountCommand;
import saviing.game.character.application.dto.result.CharacterCreatedResult;
import saviing.game.character.application.dto.result.VoidResult;
import saviing.common.event.DomainEventPublisher;
import saviing.game.character.domain.exception.CharacterNotFoundException;
import saviing.game.character.domain.exception.DuplicateActiveCharacterException;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.repository.CharacterRepository;

/**
 * 캐릭터 Command 처리 서비스
 * 상태 변경을 담당하는 Command 처리를 담당합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CharacterCommandService {

    private final CharacterRepository characterRepository;
    private final DomainEventPublisher domainEventPublisher;

    /**
     * 새로운 캐릭터를 생성합니다.
     *
     * @param command 캐릭터 생성 Command
     * @return 생성된 캐릭터 결과
     */
    @Transactional
    public CharacterCreatedResult createCharacter(CreateCharacterCommand command) {
        log.info("Creating character for customer: {}", command.customerId().value());

        if (characterRepository.findActiveCharacterByCustomerId(command.customerId()).isPresent()) {
            throw new DuplicateActiveCharacterException(command.customerId());
        }

        Character character = Character.create(command.customerId());
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Character created with ID: {}", savedCharacter.getCharacterId().value());
        return CharacterCreatedResult.builder()
            .characterId(savedCharacter.getCharacterId().value())
            .customerId(savedCharacter.getCustomerId().value())
            .createdAt(savedCharacter.getCharacterLifecycle().createdAt())
            .build();
    }

    /**
     * 계좌 연결을 시작합니다.
     *
     * @param command 계좌 연결 Command
     * @return VoidResult
     */
    @Transactional
    public VoidResult connectAccount(ConnectAccountCommand command) {
        log.info("Starting account connection for character: {}, account: {}",
                command.characterId().value(), command.accountId());

        Character character = findCharacterById(command.characterId());
        character.startConnectingAccount(command.accountId());
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Account connection started for character: {}", command.characterId().value());
        return VoidResult.success();
    }

    /**
     * 계좌 연결을 완료합니다.
     *
     * @param command 계좌 연결 완료 Command
     * @return VoidResult
     */
    @Transactional
    public VoidResult completeAccountConnection(CompleteAccountConnectionCommand command) {
        log.info("Completing account connection for character: {}, account: {}",
                command.characterId().value(), command.accountId());

        Character character = findCharacterById(command.characterId());
        character.completeAccountConnection(command.accountId());
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Account connection completed for character: {}", command.characterId().value());
        return VoidResult.success();
    }

    /**
     * 코인을 추가합니다.
     *
     * @param command 코인 추가 Command
     * @return VoidResult
     */
    @Transactional
    public VoidResult addCoins(AddCoinsCommand command) {
        log.info("Adding coins to character: {}, coin: {}, fishCoin: {}",
                command.characterId().value(), command.coinAmount(), command.fishCoinAmount());

        Character character = findCharacterById(command.characterId());

        if (command.coinAmount() > 0) {
            character.addCoin(command.coinAmount());
        }
        if (command.fishCoinAmount() > 0) {
            character.addFishCoin(command.fishCoinAmount());
        }

        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Coins added to character: {}", command.characterId().value());
        return VoidResult.success();
    }

    /**
     * 코인을 차감합니다.
     *
     * @param command 코인 차감 Command
     * @return VoidResult
     */
    @Transactional
    public VoidResult debitCoins(DebitCoinsCommand command) {
        log.info("Debiting coins from character: {}, coin: {}, fishCoin: {}",
                command.characterId().value(), command.coinAmount(), command.fishCoinAmount());

        Character character = findCharacterById(command.characterId());

        if (command.coinAmount() > 0) {
            character.debitCoin(command.coinAmount());
        }
        if (command.fishCoinAmount() > 0) {
            character.debitFishCoin(command.fishCoinAmount());
        }

        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Coins debited from character: {}", command.characterId().value());
        return VoidResult.success();
    }

    /**
     * 방 수를 증가시킵니다.
     *
     * @param command 방 수 증가 Command
     * @return VoidResult
     */
    @Transactional
    public VoidResult increaseRoomCount(IncreaseRoomCountCommand command) {
        log.info("Increasing room count for character: {}", command.characterId().value());

        Character character = findCharacterById(command.characterId());
        character.increaseRoomCount();
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Room count increased for character: {}", command.characterId().value());
        return VoidResult.success();
    }

    /**
     * 캐릭터를 비활성화합니다.
     *
     * @param command 캐릭터 비활성화 Command
     * @return VoidResult
     */
    @Transactional
    public VoidResult deactivateCharacter(DeactivateCharacterCommand command) {
        log.info("Deactivating character: {}", command.characterId().value());

        Character character = findCharacterById(command.characterId());
        character.deactivate();
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Character deactivated: {}", command.characterId().value());
        return VoidResult.success();
    }

    /**
     * 계좌 해지를 처리합니다.
     *
     * @param command 계좌 해지 처리 Command
     * @return VoidResult
     */
    @Transactional
    public VoidResult handleAccountTerminated(HandleAccountTerminatedCommand command) {
        log.info("Handling account termination for character: {}, reason: {}",
                command.characterId().value(), command.terminationReason());

        Character character = findCharacterById(command.characterId());
        character.handleAccountTerminated(command.terminationReason());
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Account termination handled for character: {}", command.characterId().value());
        return VoidResult.success();
    }

    /**
     * 계좌 연결을 취소합니다.
     *
     * @param command 계좌 연결 취소 Command
     * @return VoidResult
     */
    @Transactional
    public VoidResult cancelAccountConnection(CancelAccountConnectionCommand command) {
        log.info("Canceling account connection for character: {}", command.characterId().value());

        Character character = findCharacterById(command.characterId());
        character.cancelConnection();
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);

        log.info("Account connection canceled for character: {}", command.characterId().value());
        return VoidResult.success();
    }

    /**
     * 캐릭터 ID로 캐릭터를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @return 캐릭터 도메인 객체
     * @throws CharacterNotFoundException 캐릭터를 찾을 수 없는 경우
     */
    private Character findCharacterById(CharacterId characterId) {
        return characterRepository.findById(characterId)
                .orElseThrow(() -> new CharacterNotFoundException(characterId.value()));
    }

    /**
     * 도메인 이벤트를 발행합니다.
     *
     * @param character 캐릭터 도메인 객체
     */
    private void publishDomainEvents(Character character) {
        character.getCharacterDomainEvents().forEach(domainEventPublisher::publish);
        character.clearDomainEvents();
    }
}