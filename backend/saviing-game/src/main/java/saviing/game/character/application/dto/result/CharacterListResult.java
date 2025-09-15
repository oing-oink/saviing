package saviing.game.character.application.dto.result;

import java.util.List;

/**
 * 캐릭터 목록 조회 결과 Result입니다.
 *
 * @param characters 캐릭터 목록
 */
public record CharacterListResult(
    List<CharacterResult> characters
) {
    /**
     * CharacterListResult를 생성합니다.
     *
     * @param characters 캐릭터 목록
     * @return CharacterListResult
     */
    public static CharacterListResult of(List<CharacterResult> characters) {
        return new CharacterListResult(characters);
    }
}