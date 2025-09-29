package saviing.game.character.domain.repository;

import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 캐릭터 도메인의 Repository 인터페이스
 * 캐릭터 Aggregate의 영속성을 담당합니다.
 */
public interface CharacterRepository {
    
    /**
     * 캐릭터를 저장합니다.
     * 
     * @param character 저장할 캐릭터
     * @return 저장된 캐릭터
     */
    Character save(Character character);
    
    /**
     * 캐릭터 ID로 캐릭터를 조회합니다.
     * 
     * @param characterId 캐릭터 ID
     * @return 조회된 캐릭터 (Optional)
     */
    Optional<Character> findById(CharacterId characterId);
    
    /**
     * 고객 ID로 활성 캐릭터를 조회합니다.
     * 고객당 하나의 활성 캐릭터만 존재합니다.
     * 
     * @param customerId 고객 ID
     * @return 활성 캐릭터 (Optional)
     */
    Optional<Character> findActiveCharacterByCustomerId(CustomerId customerId);
    
    /**
     * 고객 ID로 모든 캐릭터를 조회합니다 (비활성 포함).
     * 
     * @param customerId 고객 ID
     * @return 캐릭터 목록
     */
    List<Character> findAllByCustomerId(CustomerId customerId);
    
    /**
     * 모든 활성 캐릭터를 조회합니다.
     * 
     * @return 활성 캐릭터 목록
     */
    List<Character> findAllActiveCharacters();
    
    /**
     * 고객 ID로 캐릭터 존재 여부를 확인합니다.
     *
     * @param customerId 고객 ID
     * @return 캐릭터 존재 여부
     */
    boolean existsByCustomerId(CustomerId customerId);

    /**
     * 캐릭터의 상위 펫 레벨 합계를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param limit 조회할 펫 개수
     * @return 펫 레벨 합계
     */
    Integer findTopPetLevelSumByCharacterId(CharacterId characterId, int limit);

    /**
     * 캐릭터의 카테고리별 희귀도 합계를 조회합니다.
     *
     * @param characterId 캐릭터 ID
     * @param limit 카테고리당 조회할 아이템 개수
     * @return 카테고리별 희귀도 합계 Map
     */
    Map<String, Integer> findTopRaritySumByCharacterIdAndCategory(CharacterId characterId, int limit);
}