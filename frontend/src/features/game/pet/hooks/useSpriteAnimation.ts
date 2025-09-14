import { useState, useEffect } from 'react';
import type { PetAnimationState } from '@/features/game/pet/types/petTypes';
import { CAT_ANIMATIONS } from '@/features/game/pet/data/catAnimations';

interface UseSpriteAnimationProps {
  spritePath: string;
  currentAnimation: PetAnimationState;
  onAnimationComplete?: (animation: PetAnimationState) => void;
}

interface UseSpriteAnimationReturn {
  currentFrame: number;
  frameWidth: number;
  frameHeight: number;
  isLoaded: boolean;
}

export const useSpriteAnimation = ({
  spritePath,
  currentAnimation,
  onAnimationComplete,
}: UseSpriteAnimationProps): UseSpriteAnimationReturn => {
  const [currentFrame, setCurrentFrame] = useState(0);
  const [frameWidth, setFrameWidth] = useState(0);
  const [frameHeight, setFrameHeight] = useState(0);
  const [isLoaded, setIsLoaded] = useState(false);

  const animationConfig = CAT_ANIMATIONS[currentAnimation];

  // 이미지 로드해서 실제 크기 측정
  useEffect(() => {
    if (!spritePath || !animationConfig) {
      return;
    }

    setIsLoaded(false);
    const img = new Image();
    img.src = spritePath;

    img.onload = () => {
      setFrameWidth(img.width / animationConfig.frames);
      setFrameHeight(img.height);
      setIsLoaded(true);
    };

    img.onerror = () => {
      console.error(`Failed to load sprite: ${spritePath}`);
      setIsLoaded(false);
    };
  }, [spritePath, animationConfig]);

  // 애니메이션이 변경되면 첫 번째 프레임으로 리셋
  useEffect(() => {
    setCurrentFrame(0);
  }, [currentAnimation]);

  // 프레임 변경 타이머
  useEffect(() => {
    if (!animationConfig || !isLoaded) {
      return;
    }

    const interval = setInterval(() => {
      setCurrentFrame(prev => {
        const next = prev + 1;

        if (next >= animationConfig.frames) {
          if (animationConfig.loop) {
            return 0;
          } else {
            onAnimationComplete?.(currentAnimation);
            return prev;
          }
        }

        return next;
      });
    }, animationConfig.duration);

    return () => clearInterval(interval);
  }, [animationConfig, currentAnimation, onAnimationComplete, isLoaded]);

  return {
    currentFrame,
    frameWidth,
    frameHeight,
    isLoaded,
  };
};
