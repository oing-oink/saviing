package saviing.game.character.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.character.application.dto.query.GetActiveCharacterQuery;
import saviing.game.character.application.dto.query.GetAllCharactersByCustomerQuery;
import saviing.game.character.application.dto.query.GetCharacterQuery;
import saviing.game.character.application.dto.result.CharacterListResult;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.character.application.mapper.CharacterResultMapper;
import saviing.game.character.domain.exception.CharacterNotFoundException;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.repository.CharacterRepository;

import java.util.List;

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