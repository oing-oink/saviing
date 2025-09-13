package saviing.game.character.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.character.application.dto.AddCoinsCommand;
import saviing.game.character.application.dto.CancelAccountConnectionCommand;
import saviing.game.character.application.dto.CompleteAccountConnectionCommand;
import saviing.game.character.application.dto.ConnectAccountCommand;
import saviing.game.character.application.dto.CreateCharacterCommand;
import saviing.game.character.application.dto.DeactivateCharacterCommand;
import saviing.game.character.application.dto.HandleAccountTerminatedCommand;
import saviing.game.character.application.dto.IncreaseRoomCountCommand;
import saviing.game.character.application.event.DomainEventPublisher;
import saviing.game.character.domain.exception.CharacterNotFoundException;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.domain.repository.CharacterRepository;
import saviing.game.character.domain.service.CharacterDomainService;

import java.util.List;

/**
 * 캐릭터 Application Service
 * 트랜잭션 관리와 도메인 이벤트 발행을 담당합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CharacterApplicationService {
    
    private final CharacterRepository characterRepository;
    private final CharacterDomainService characterDomainService;
    private final DomainEventPublisher domainEventPublisher;
    
    /**
     * 새로운 캐릭터를 생성합니다.
     * 
     * @param command 캐릭터 생성 Command
     * @return 생성된 캐릭터 정보
     */
    @Transactional
    public Character createCharacter(CreateCharacterCommand command) {
        log.info("Creating character for customer: {}", command.customerId().value());
        
        // 도메인 서비스를 통한 캐릭터 생성
        Character character = characterDomainService.createCharacter(command.customerId());
        
        // 저장
        Character savedCharacter = characterRepository.save(character);
        
        // 도메인 이벤트 발행
        publishDomainEvents(savedCharacter);
        
        log.info("Character created with ID: {}", savedCharacter.getCharacterId().value());
        return savedCharacter;
    }
    
    /**
     * 계좌 연결을 시작합니다.
     * 
     * @param command 계좌 연결 Command
     */
    @Transactional
    public void connectAccount(ConnectAccountCommand command) {
        log.info("Starting account connection for character: {}, account: {}", 
                command.characterId().value(), command.accountId());
        
        Character character = findCharacterById(command.characterId());
        character.startConnectingAccount(command.accountId());
        
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);
        
        log.info("Account connection started for character: {}", command.characterId().value());
    }
    
    /**
     * 계좌 연결을 완료합니다.
     * 
     * @param command 계좌 연결 완료 Command
     */
    @Transactional
    public void completeAccountConnection(CompleteAccountConnectionCommand command) {
        log.info("Completing account connection for character: {}, account: {}", 
                command.characterId().value(), command.accountId());
        
        Character character = findCharacterById(command.characterId());
        character.completeAccountConnection(command.accountId());
        
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);
        
        log.info("Account connection completed for character: {}", command.characterId().value());
    }
    
    /**
     * 코인을 추가합니다.
     * 
     * @param command 코인 추가 Command
     */
    @Transactional
    public void addCoins(AddCoinsCommand command) {
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
    }
    
    /**
     * 방 수를 증가시킵니다.
     * 
     * @param command 방 수 증가 Command
     */
    @Transactional
    public void increaseRoomCount(IncreaseRoomCountCommand command) {
        log.info("Increasing room count for character: {}", command.characterId().value());
        
        Character character = findCharacterById(command.characterId());
        character.increaseRoomCount();
        
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);
        
        log.info("Room count increased for character: {}", command.characterId().value());
    }
    
    /**
     * 캐릭터를 비활성화합니다.
     * 
     * @param command 캐릭터 비활성화 Command
     */
    @Transactional
    public void deactivateCharacter(DeactivateCharacterCommand command) {
        log.info("Deactivating character: {}", command.characterId().value());
        
        Character character = findCharacterById(command.characterId());
        character.deactivate();
        
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);
        
        log.info("Character deactivated: {}", command.characterId().value());
    }
    
    /**
     * 계좌 해지를 처리합니다.
     * 외부 도메인(Bank)에서 계좌가 해지되었을 때 Character 도메인에 알립니다.
     * 
     * @param command 계좌 해지 처리 Command
     */
    @Transactional
    public void handleAccountTerminated(HandleAccountTerminatedCommand command) {
        log.info("Handling account termination for character: {}, reason: {}", 
                command.characterId().value(), command.terminationReason());
        
        Character character = findCharacterById(command.characterId());
        character.handleAccountTerminated(command.terminationReason());
        
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);
        
        log.info("Account termination handled for character: {}", command.characterId().value());
    }
    
    /**
     * 캐릭터를 재생성합니다.
     * 기존 활성 캐릭터가 있다면 비활성화하고 새 캐릭터를 생성합니다.
     * 
     * @param command 캐릭터 생성 Command
     * @return 새로 생성된 캐릭터 정보
     */
    @Transactional
    public Character recreateCharacter(CreateCharacterCommand command) {
        log.info("Recreating character for customer: {}", command.customerId().value());
        
        // 도메인 서비스를 통한 캐릭터 재생성
        Character character = characterDomainService.recreateCharacter(command.customerId());
        
        // 저장
        Character savedCharacter = characterRepository.save(character);
        
        // 도메인 이벤트 발행
        publishDomainEvents(savedCharacter);
        
        log.info("Character recreated with ID: {}", savedCharacter.getCharacterId().value());
        return savedCharacter;
    }
    
    /**
     * 계좌 연결을 취소합니다.
     * 
     * @param command 계좌 연결 취소 Command
     */
    @Transactional
    public void cancelAccountConnection(CancelAccountConnectionCommand command) {
        log.info("Canceling account connection for character: {}", command.characterId().value());
        
        Character character = findCharacterById(command.characterId());
        character.cancelConnection();
        
        Character savedCharacter = characterRepository.save(character);
        publishDomainEvents(savedCharacter);
        
        log.info("Account connection canceled for character: {}", command.characterId().value());
    }
    
    /**
     * 캐릭터 상세 정보를 조회합니다.
     * 
     * @param characterId 캐릭터 ID
     * @return 캐릭터 상세 정보
     */
    public Character getCharacter(CharacterId characterId) {
        return findCharacterById(characterId);
    }
    
    /**
     * 고객의 활성 캐릭터를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 활성 캐릭터 정보
     */
    public Character getActiveCharacterByCustomer(CustomerId customerId) {
        return characterRepository.findActiveCharacterByCustomerId(customerId)
                .orElseThrow(() -> new CharacterNotFoundException(
                        "고객 ID " + customerId.value() + "의 활성 캐릭터를 찾을 수 없습니다"));
    }
    
    /**
     * 고객의 모든 캐릭터를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 캐릭터 목록
     */
    public List<Character> getAllCharactersByCustomer(CustomerId customerId) {
        return characterRepository.findAllByCustomerId(customerId);
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
        domainEventPublisher.publishAll(character.getDomainEvents());
        character.clearDomainEvents();
    }
}