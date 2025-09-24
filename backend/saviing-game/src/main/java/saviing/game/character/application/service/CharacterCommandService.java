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
import saviing.game.room.application.service.RoomCommandService;
import saviing.game.room.application.dto.command.CreateRoomCommand;
import saviing.game.room.application.dto.result.RoomCreatedResult;
import saviing.game.room.domain.model.vo.RoomNumber;
import saviing.game.inventory.application.service.InventoryCommandService;
import saviing.game.inventory.application.dto.command.AddInventoryItemCommand;
import saviing.game.item.domain.repository.ItemRepository;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Rarity;

import java.util.List;
import java.util.Random;

/**
 * 캐릭터 Command 처리 서비스
 * 캐릭터 생성, 계좌 연결, 코인 관리 등의 상태 변경을 담당하는 Command 처리를 담당합니다.
 * 캐릭터 생성 시 기본 방도 함께 생성하여 강한 일관성을 보장합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CharacterCommandService {

    private final CharacterRepository characterRepository;
    private final RoomCommandService roomCommandService;
    private final DomainEventPublisher domainEventPublisher;
    private final InventoryCommandService inventoryCommandService;
    private final ItemRepository itemRepository;

    /**
     * 새로운 캐릭터를 생성합니다.
     * 캐릭터 생성과 동시에 기본 방(1번 방)도 함께 생성하여
     * 데이터 일관성을 보장하고 사용자가 즉시 게임을 시작할 수 있도록 합니다.
     *
     * @param command 캐릭터 생성 Command
     * @return 생성된 캐릭터 결과
     * @throws DuplicateActiveCharacterException 이미 활성 캐릭터가 존재하는 경우
     * @throws RuntimeException 방 생성에 실패한 경우 (트랜잭션 롤백됨)
     */
    @Transactional
    public CharacterCreatedResult createCharacter(CreateCharacterCommand command) {
        log.info("Creating character for customer: {}", command.customerId().value());

        // 1. 기존 활성 캐릭터 중복 확인
        if (characterRepository.findActiveCharacterByCustomerId(command.customerId()).isPresent()) {
            throw new DuplicateActiveCharacterException(command.customerId());
        }

        // 2. 캐릭터 생성 및 저장
        Character character = Character.create(command.customerId());
        Character savedCharacter = characterRepository.save(character);

        // 3. 기본 방(1번 방) 생성
        Long roomId = createDefaultRoom(savedCharacter.getCharacterId().value());

        // 4. 스타터 펫 추가 (COMMON 등급 PET 아이템 중 랜덤 선택)
        log.info("Adding starter pet for characterId: {}", savedCharacter.getCharacterId().value());
        addStarterPet(savedCharacter.getCharacterId());

        // 5. 도메인 이벤트 발행
        publishDomainEvents(savedCharacter);

        log.info("Character created with ID: {} and default room created with ID: {}",
            savedCharacter.getCharacterId().value(), roomId);

        return CharacterCreatedResult.builder()
            .characterId(savedCharacter.getCharacterId().value())
            .customerId(savedCharacter.getCustomerId().value())
            .roomId(roomId)
            .coin(savedCharacter.getGameStatus().coin())
            .fishCoin(savedCharacter.getGameStatus().fishCoin())
            .roomCount(savedCharacter.getGameStatus().roomCount())
            .isActive(savedCharacter.isActive())
            .connectionStatus(savedCharacter.getAccountConnection().connectionStatus())
            .createdAt(savedCharacter.getCharacterLifecycle().createdAt())
            .updatedAt(savedCharacter.getCharacterLifecycle().updatedAt())
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
     * 캐릭터의 기본 방(1번 방)을 생성합니다.
     * 캐릭터 생성 시 즉시 사용할 수 있는 기본 방을 제공하기 위해 호출됩니다.
     * 동일한 트랜잭션 내에서 실행되어 캐릭터와 방의 일관성을 보장합니다.
     *
     * @param characterId 방을 생성할 캐릭터의 식별자
     * @return 생성된 방의 식별자
     * @throws RuntimeException 방 생성에 실패한 경우
     */
    private Long createDefaultRoom(Long characterId) {
        try {
            CreateRoomCommand roomCommand = CreateRoomCommand.of(
                characterId,
                RoomNumber.DEFAULT.value()
            );
            RoomCreatedResult roomResult = roomCommandService.createRoom(roomCommand);
            return roomResult.roomId();
        } catch (Exception e) {
            log.error("Failed to create default room for character: {}, error: {}",
                characterId, e.getMessage(), e);
            throw new RuntimeException("기본 방 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 캐릭터에게 스타터 펫을 추가합니다.
     * COMMON 등급의 PET 아이템 중에서 랜덤으로 하나를 선택하여 인벤토리에 추가합니다.
     *
     * @param characterId 캐릭터 식별자
     */
    private void addStarterPet(CharacterId characterId) {
        try {
            // COMMON 등급 PET 아이템들 조회
            List<Item> commonPets = itemRepository.findItemsWithConditions(
                ItemType.PET,     // 타입: PET
                null,             // 카테고리: 제한 없음 (모든 펫)
                Rarity.COMMON,    // 희귀도: COMMON
                null,             // 키워드: 제한 없음
                true,             // 판매 가능한 아이템만
                "CREATED_AT",     // 생성일 기준 정렬
                "ASC",            // 오름차순
                null              // 코인 타입: 해당 없음
            );

            if (!commonPets.isEmpty()) {
                // 랜덤으로 하나 선택
                Random random = new Random();
                Item randomPet = commonPets.get(random.nextInt(commonPets.size()));

                // 인벤토리에 추가
                AddInventoryItemCommand petCommand = AddInventoryItemCommand.of(
                    characterId,
                    randomPet.getItemId()
                );
                inventoryCommandService.addInventoryItem(petCommand);

                log.info("Starter pet added to character: characterId={}, petItemId={}, petName={}",
                    characterId.value(), randomPet.getItemId().value(), randomPet.getItemName().value());
            } else {
                log.warn("No COMMON PET items found for starter pet assignment. Character: {}",
                    characterId.value());
            }
        } catch (Exception e) {
            // 스타터 펫 추가 실패 시 캐릭터 생성은 성공하되 로그만 남김
            log.error("Failed to add starter pet to character: {}, error: {}",
                characterId.value(), e.getMessage(), e);
        }
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