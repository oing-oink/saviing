package saviing.game.character.domain.service;

import saviing.game.character.domain.exception.DuplicateActiveCharacterException;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.domain.repository.CharacterRepository;

/**
 * 캐릭터 도메인 서비스
 * 복잡한 비즈니스 규칙을 처리합니다.
 */
public class CharacterDomainService {
    
    private final CharacterRepository characterRepository;
    
    public CharacterDomainService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }
    
    /**
     * 새로운 캐릭터를 생성합니다.
     * 고객당 하나의 활성 캐릭터만 존재할 수 있는 비즈니스 규칙을 검증합니다.
     * 
     * @param customerId 고객 ID
     * @return 생성된 캐릭터
     * @throws IllegalStateException 이미 활성 캐릭터가 존재하는 경우
     */
    public Character createCharacter(CustomerId customerId) {
        // 활성 캐릭터 중복 검사
        if (characterRepository.findActiveCharacterByCustomerId(customerId).isPresent()) {
            throw new DuplicateActiveCharacterException(customerId);
        }
        
        return Character.create(customerId);
    }
    
    /**
     * 캐릭터를 비활성화하고 새로운 캐릭터를 생성합니다.
     * 기존 활성 캐릭터가 있다면 비활성화하고 새 캐릭터를 생성합니다.
     * 
     * @param customerId 고객 ID
     * @return 새로 생성된 캐릭터
     */
    public Character recreateCharacter(CustomerId customerId) {
        // 기존 활성 캐릭터가 있다면 비활성화
        characterRepository.findActiveCharacterByCustomerId(customerId)
            .ifPresent(existingCharacter -> {
                existingCharacter.deactivate();
                characterRepository.save(existingCharacter);
            });
        
        // 새로운 캐릭터 생성
        return Character.create(customerId);
    }
    
    /**
     * 고객의 활성 캐릭터 존재 여부를 확인합니다.
     * 
     * @param customerId 고객 ID
     * @return 활성 캐릭터 존재 여부
     */
    public boolean hasActiveCharacter(CustomerId customerId) {
        return characterRepository.findActiveCharacterByCustomerId(customerId).isPresent();
    }
    
    /**
     * 고객의 캐릭터 생성이 가능한지 확인합니다.
     * 
     * @param customerId 고객 ID
     * @return 캐릭터 생성 가능 여부
     */
    public boolean canCreateCharacter(CustomerId customerId) {
        return !hasActiveCharacter(customerId);
    }
}