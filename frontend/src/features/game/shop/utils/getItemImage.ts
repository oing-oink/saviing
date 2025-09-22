/**
 * itemId를 기반으로 로컬 asset 이미지 경로를 반환하는 유틸리티 함수
 * @param itemId - 아이템 ID
 * @returns asset 이미지 경로
 */
export const getItemImage = (itemId: number): string => {
  const paddedId = itemId.toString().padStart(3, '0');

  try {
    // Vite의 동적 import를 사용하여 asset 경로 생성
    return `/public/game_assets/asset_${paddedId}.png`;
  } catch {
    // 이미지가 없는 경우 기본 이미지 또는 빈 문자열 반환
    return '';
  }
};
