import { useEffect, useState } from 'react';
import {
  CAT_ANIMATIONS,
  type CatAnimationType,
} from '@/features/game/pet/data/catAnimations';

interface UseSpriteAnimationProps {
  spritePath: string;
  currentAnimation: CatAnimationType;
  onAnimationComplete?: (animation: CatAnimationType) => void;
}

export const useSpriteAnimation = ({
  spritePath,
  currentAnimation,
  onAnimationComplete,
}: UseSpriteAnimationProps) => {
  const [currentFrame, setCurrentFrame] = useState(0);
  const [frameWidth, setFrameWidth] = useState(0);
  const [frameHeight, setFrameHeight] = useState(0);
  const [isLoaded, setIsLoaded] = useState(false);

  const config = CAT_ANIMATIONS[currentAnimation];

  // 이미지 크기 계산
  useEffect(() => {
    const img = new Image();
    img.src = spritePath;
    img.onload = () => {
      setFrameWidth(img.width / config.frames);
      setFrameHeight(img.height);
      setIsLoaded(true);
    };
  }, [spritePath, config.frames]);

  // 애니메이션 변경 시 초기화
  useEffect(() => {
    setCurrentFrame(0);
  }, [currentAnimation]);

  // 프레임 타이머
  useEffect(() => {
    if (!isLoaded) {
      return;
    }

    const interval = setInterval(() => {
      setCurrentFrame(prev => {
        const next = prev + 1;
        if (next >= config.frames) {
          if (config.loop) {
            return 0;
          } else {
            onAnimationComplete?.(currentAnimation);
            return prev;
          }
        }
        return next;
      });
    }, config.duration);

    return () => clearInterval(interval);
  }, [isLoaded, config, currentAnimation, onAnimationComplete]);

  return { currentFrame, frameWidth, frameHeight, isLoaded };
};
