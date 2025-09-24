package saviing.game.character.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.character.application.dto.query.GetActiveCharacterQuery;
import saviing.game.character.application.dto.query.GetAllCharactersByCustomerQuery;
import saviing.game.character.application.dto.query.GetCharacterQuery;
import saviing.game.character.application.dto.query.GetGameEntryQuery;
import saviing.game.character.application.dto.result.CharacterListResult;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.character.application.dto.result.GameEntryResult;
import saviing.game.character.application.dto.query.GetCharacterStatisticsQuery;
import saviing.game.character.application.dto.result.CharacterStatisticsResult;
import saviing.game.character.application.mapper.CharacterResultMapper;
import saviing.game.character.domain.exception.CharacterNotFoundException;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.repository.CharacterRepository;
import saviing.game.inventory.application.dto.query.GetPetsByCharacterAndRoomQuery;
import saviing.game.inventory.application.dto.result.PetInventoryResult;
import saviing.game.inventory.application.service.InventoryQueryService;
import saviing.game.pet.application.dto.query.GetPetInfoQuery;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.application.service.PetQueryService;
import saviing.game.room.application.dto.query.GetRoomByCharacterQuery;
import saviing.game.room.application.dto.result.RoomResult;
import saviing.game.room.application.service.RoomQueryService;

import java.util.Comparator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 캐릭터 Query 처리 서비스
 * 조회를 담당하는 Query 처리를 담당합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CharacterQueryService {

    private final CharacterRepository characterRepository;
    private final CharacterResultMapper resultMapper;
    private final InventoryQueryService inventoryQueryService;
    private final PetQueryService petQueryService;
    private final RoomQueryService roomQueryService;

    /**
     * 캐릭터 상세 정보를 조회합니다.
     *
     * @param query 캐릭터 조회 Query
     * @return 캐릭터 조회 결과
     */
    public CharacterResult getCharacter(GetCharacterQuery query) {
        Character character = findCharacterById(query.characterId());
        return resultMapper.toResult(character);
    }

    /**
     * 고객의 활성 캐릭터를 조회합니다.
     *
     * @param query 활성 캐릭터 조회 Query
     * @return 캐릭터 조회 결과
     */
    public CharacterResult getActiveCharacter(GetActiveCharacterQuery query) {
        Character character = characterRepository.findActiveCharacterByCustomerId(query.customerId())
            .orElseThrow(() -> new CharacterNotFoundException(
                "고객 ID " + query.customerId().value() + "의 활성 캐릭터를 찾을 수 없습니다"));
        return resultMapper.toResult(character);
    }

    /**
     * 고객의 모든 캐릭터를 조회합니다.
     *
     * @param query 모든 캐릭터 조회 Query
     * @return 캐릭터 목록 조회 결과
     */
    public CharacterListResult getAllCharactersByCustomer(GetAllCharactersByCustomerQuery query) {
        List<Character> characters = characterRepository.findAllByCustomerId(query.customerId());
        List<CharacterResult> results = characters.stream()
            .map(resultMapper::toResult)
            .toList();
        return CharacterListResult.of(results);
    }

    /**
     * 메인 엔트리 게임 정보를 조회합니다.
     *
     * @param query 메인 엔트리 조회 Query
     * @return 메인 엔트리 게임 정보
     */
    public GameEntryResult getGameEntry(GetGameEntryQuery query) {
        log.info("메인 엔트리 게임 정보 조회: customerId={}", query.customerId().value());

        // 1. 고객의 활성 캐릭터 조회
        Character character = characterRepository.findActiveCharacterByCustomerId(query.customerId())
            .orElseThrow(() -> new CharacterNotFoundException(
                "고객 ID " + query.customerId().value() + "의 활성 캐릭터를 찾을 수 없습니다"));

        // 2. 1층에 배치된 펫 조회 (1층 방의 roomId를 동적으로 조회)
        RoomResult firstFloorRoom = roomQueryService.findRoomByCharacterIdAndRoomNumber(
            GetRoomByCharacterQuery.builder()
                .characterId(character.getCharacterId().value())
                .roomNumber(1)
                .build()
        );
        Long roomId = firstFloorRoom.roomId();

        log.info("1층 방 조회 완료: roomId={}, roomNumber={}", roomId, firstFloorRoom.roomNumber());

        List<PetInventoryResult> roomPets = inventoryQueryService.getPetsByCharacterAndRoom(
            GetPetsByCharacterAndRoomQuery.of(character.getCharacterId().value(), roomId));

        log.info("{}층(roomId={})에 있는 펫 개수: {}", firstFloorRoom.roomNumber(), roomId, roomPets.size());
        if (!roomPets.isEmpty()) {
            log.info("펫 목록: {}", roomPets.stream()
                .map(PetInventoryResult::inventoryItemId)
                .toList());
        }

        // 3. petId(inventoryItemId) 순으로 정렬하여 첫 번째 펫 선택
        PetResult firstPet = roomPets.stream()
            .min(Comparator.comparing(PetInventoryResult::inventoryItemId))
            .map(petInventory -> {
                log.info("첫 번째 펫 조회 시도: inventoryItemId={}", petInventory.inventoryItemId());
                try {
                    return petQueryService.getPetInfo(
                        GetPetInfoQuery.of(petInventory.inventoryItemId()));
                } catch (Exception e) {
                    log.error("펫 조회 실패: inventoryItemId={}, error={}",
                        petInventory.inventoryItemId(), e.getMessage());
                    return null;
                }
            })
            .orElse(null);

        log.info("최종 선택된 펫: {}", firstPet != null ? firstPet.petId() : "null");

        return GameEntryResult.builder()
            .characterId(character.getCharacterId().value())
            .roomId(roomId)
            .pet(firstPet)
            .build();
    }

    /**
     * 캐릭터의 잔액이 충분한지 확인합니다.
     *
     * @param characterId 캐릭터 ID
     * @param coinAmount 필요한 코인 수량
     * @param fishCoinAmount 필요한 피쉬 코인 수량
     * @return 잔액 충분 여부
     */
    public boolean hasSufficientFunds(Long characterId, Integer coinAmount, Integer fishCoinAmount) {
        Character character = findCharacterById(CharacterId.of(characterId));
        return character.hasSufficientFunds(coinAmount, fishCoinAmount);
    }

    /**
     * 캐릭터의 통계 정보를 조회합니다.
     * 상위 펫 레벨 합계와 카테고리별 희귀도 통계를 조회합니다.
     *
     * @param query 캐릭터 통계 조회 Query
     * @return 캐릭터 통계 조회 결과
     * @throws CharacterNotFoundException 캐릭터를 찾을 수 없는 경우
     */
    public CharacterStatisticsResult getCharacterStatistics(GetCharacterStatisticsQuery query) {
        log.info("캐릭터 통계 조회 시작: characterId={}", query.characterId().value());

        // 캐릭터 존재 여부 확인
        findCharacterById(query.characterId());

        // 1. 상위 펫 레벨 합계 조회 (상위 10개)
        Integer topPetLevelSum = characterRepository.findTopPetLevelSumByCharacterId(
            query.characterId(), 10
        );

        // 2. 카테고리별 희귀도 합계 조회 (카테고리당 상위 5개)
        Map<String, Integer> flatRarityMap = characterRepository.findTopRaritySumByCharacterIdAndCategory(
            query.characterId(), 5
        );

        // 3. Flat Map을 Two-depth 구조로 변환
        Map<String, Map<String, Integer>> groupedRarityMap = groupByItemType(flatRarityMap);

        // 4. Result 매핑
        CharacterStatisticsResult result = resultMapper.toStatisticsResult(
            query.characterId(),
            topPetLevelSum,
            groupedRarityMap
        );

        log.info("캐릭터 통계 조회 완료: characterId={}, petLevelSum={}, rarityCategories={}",
            query.characterId().value(), topPetLevelSum, groupedRarityMap.keySet());

        return result;
    }

    /**
     * Flat한 카테고리별 희귀도 맵을 ItemType별로 그룹화합니다.
     * PET 타입과 DECORATION 타입으로 분류하여 two-depth 구조를 만듭니다.
     *
     * @param flatRarityMap 카테고리명을 키로 하는 희귀도 합계 맵
     * @return ItemType별로 그룹화된 희귀도 통계 맵
     */
    private Map<String, Map<String, Integer>> groupByItemType(Map<String, Integer> flatRarityMap) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        result.put("PET", new HashMap<>());
        result.put("DECORATION", new HashMap<>());

        for (Map.Entry<String, Integer> entry : flatRarityMap.entrySet()) {
            String category = entry.getKey();
            Integer value = entry.getValue();

            if ("CAT".equals(category)) {
                result.get("PET").put(category, value);
            } else if (Arrays.asList("LEFT", "RIGHT", "BOTTOM", "ROOM_COLOR").contains(category)) {
                result.get("DECORATION").put(category, value);
            }
        }

        return result;
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
}