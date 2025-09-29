import { http } from '@/shared/services/api/http';
import type {
  CharacterGameData,
  CharacterStatistics,
} from '@/features/game/shared/types/gameTypes';

/**
 * 특정 캐릭터의 게임 정보를 조회하는 API 함수
 *
 * 사용자의 캐릭터 정보, 보유 재화, 방 개수 등 게임 진행에 필요한 정보를 반환합니다.
 *
 * @param characterId - 조회할 캐릭터의 고유 식별자
 * @returns 캐릭터의 게임 정보가 담긴 CharacterGameData 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getCharacterGameData = async (
  characterId: number,
): Promise<CharacterGameData> => {
  const response = await http.get<CharacterGameData>(
    `/v1/game/characters/${characterId}`,
  );
  return response.body!;
};

/**
 * 특정 캐릭터의 이자율 계산 통계를 조회하는 API 함수
 *
 * 캐릭터의 펫 레벨 합과 인벤토리 레어리티 통계를 기반으로 한 이자율 계산 정보를 반환합니다.
 *
 * @param characterId - 조회할 캐릭터의 고유 식별자
 * @returns 캐릭터의 이자율 계산 통계가 담긴 CharacterStatistics 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getCharacterStatistics = async (
  characterId: number,
): Promise<CharacterStatistics> => {
  const response = await http.get<CharacterStatistics>(
    `/v1/game/characters/${characterId}/statistics`,
  );
  return response.body!;
};

/**
 * 게임 캐릭터와 적금 계좌를 연결하는 API 함수
 *
 * 선택한 적금 계좌를 게임 캐릭터와 연결하여 게임 혜택을 적용할 수 있도록 합니다.
 *
 * @param characterId - 연결할 캐릭터의 고유 식별자
 * @param accountId - 연결할 적금 계좌의 고유 식별자
 * @returns 연결 성공 메시지
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const connectCharacterToAccount = async (
  characterId: number,
  accountId: number,
): Promise<string> => {
  const response = await http.put<string>(
    `/v1/game/characters/${characterId}/account`,
    { accountId },
  );
  return response.body!;
};
