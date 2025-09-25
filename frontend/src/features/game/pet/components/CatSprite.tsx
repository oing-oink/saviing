import type { PetSpriteProps } from '@/features/game/pet/types/petTypes';
import {
  getAnimationFileName,
  resolveSpritePath,
} from '@/features/game/pet/data/catAnimations';
import { useSpriteAnimation } from '@/features/game/pet/hooks/useSpriteAnimation';
import { cn } from '@/lib/utils';

/**
 * 고양이 스프라이트 애니메이션을 렌더링하는 컴포넌트
 *
 * 스프라이트 시트를 사용하여 프레임 기반 애니메이션을 구현합니다.
 * 배경 이미지의 position을 조작하여 각 프레임을 순차적으로 표시합니다.
 *
 * @param props - 스프라이트 컴포넌트의 props
 * @param props.itemId - 펫 아이템 식별자 (스프라이트 경로 결정에 사용)
 * @param props.currentAnimation - 현재 재생할 애니메이션 타입
 * @param props.className - 추가적인 CSS 클래스명
 * @param props.onAnimationComplete - 애니메이션 완료 시 호출되는 콜백 함수
 * @returns 스프라이트 애니메이션이 적용된 div 엘리먼트
 */
const CatSprite = ({
  itemId,
  currentAnimation,
  className,
  onAnimationComplete,
}: PetSpriteProps) => {
  const spritePath = resolveSpritePath(itemId);
  const spriteFileName = getAnimationFileName(currentAnimation);
  const fullSpritePath = `${spritePath}/${spriteFileName}`;

  const { currentFrame, frameWidth, frameHeight, isLoaded } =
    useSpriteAnimation({
      spritePath: fullSpritePath,
      currentAnimation,
      onAnimationComplete,
    });

  // 이미지가 로드되기 전까지는 투명 플레이스홀더 표시
  if (!isLoaded || frameWidth === 0 || frameHeight === 0) {
    return <div className={cn('bg-transparent', className)} />;
  }

  return (
    <div
      className={cn(className)}
      style={{
        width: frameWidth,
        height: frameHeight,
        backgroundImage: `url(${fullSpritePath})`,
        backgroundRepeat: 'no-repeat',
        backgroundPosition: `-${currentFrame * frameWidth}px 0px`,
        imageRendering: 'pixelated',
      }}
    />
  );
};

export default CatSprite;
