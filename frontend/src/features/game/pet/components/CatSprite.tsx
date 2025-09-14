import type { PetSpriteProps } from '@/features/game/pet/types/petTypes';
import {
  CAT_SPRITE_PATHS,
  getAnimationFileName,
} from '@/features/game/pet/data/catAnimations';
import { useSpriteAnimation } from '@/features/game/pet/hooks/useSpriteAnimation';
import { cn } from '@/lib/utils';

const CatSprite = ({
  petId,
  currentAnimation,
  className,
  onAnimationComplete,
}: PetSpriteProps) => {
  const spritePath = CAT_SPRITE_PATHS[petId as keyof typeof CAT_SPRITE_PATHS];
  const spriteFileName = getAnimationFileName(currentAnimation);
  const fullSpritePath = `${spritePath}/${spriteFileName}`;

  const { currentFrame, frameWidth, frameHeight, isLoaded } = useSpriteAnimation({
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
