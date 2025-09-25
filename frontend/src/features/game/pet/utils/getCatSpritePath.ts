import {
  CAT_SPRITE_PATHS,
  getAnimationFileName,
} from '@/features/game/pet/data/catAnimations';

/**
 * 고양이 스프라이트 시트 경로를 반환한다.
 * @param petId - 고양이 아이템/펫 ID
 * @param animation - 사용할 애니메이션 이름 (기본값: 'idle')
 * @returns 해당 애니메이션 스프라이트 이미지 경로나 null
 */
export const getCatSpritePath = (
  petId: number,
  animation: Parameters<typeof getAnimationFileName>[0] = 'idle',
): string | null => {
  const basePath = CAT_SPRITE_PATHS[petId as keyof typeof CAT_SPRITE_PATHS];
  if (!basePath) {
    return null;
  }
  const fileName = getAnimationFileName(animation);
  return `${basePath}/${fileName}`;
};
