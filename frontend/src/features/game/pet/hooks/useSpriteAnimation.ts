import { useEffect, useState } from 'react';
import {
  CAT_ANIMATIONS,
  type CatAnimationType,
} from '@/features/game/pet/data/catAnimations';

/**
 * 스프라이트 애니메이션 훅의 props 타입
 */
interface UseSpriteAnimationProps {
  /** 스프라이트 이미지 파일의 경로 */
  spritePath: string;
  /** 현재 재생할 애니메이션 타입 */
  currentAnimation: CatAnimationType;
  /** 애니메이션 완료 시 호출되는 콜백 함수 */
  onAnimationComplete?: (animation: CatAnimationType) => void;
}

/**
 * 스프라이트 시트를 사용한 프레임 기반 애니메이션을 관리하는 커스텀 훅
 *
 * 이미지 로드, 프레임 계산, 애니메이션 타이머, 프레임 전환을 자동으로 처리합니다.
 * 각 애니메이션의 설정(프레임 수, 지속 시간, 반복 여부)에 따라 동작합니다.
 *
 * @param props - 스프라이트 애니메이션 설정
 * @param props.spritePath - 스프라이트 이미지 파일 경로
 * @param props.currentAnimation - 현재 애니메이션 타입
 * @param props.onAnimationComplete - 애니메이션 완료 콜백
 * @returns 현재 프레임, 프레임 크기, 로드 상태 정보
 */
export const useSpriteAnimation = ({
  spritePath,
  currentAnimation,
  onAnimationComplete,
}: UseSpriteAnimationProps) => {
  const [currentFrame, setCurrentFrame] = useState(0);
  const [frameWidth, setFrameWidth] = useState(0);
  const [frameHeight, setFrameHeight] = useState(0);
  const [isLoaded, setIsLoaded] = useState(false);
  const [animationCompleted, setAnimationCompleted] =
    useState<CatAnimationType | null>(null);

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
    setAnimationCompleted(null);
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
            // 렌더링 중 상태 변경을 피하기 위해 완료 상태만 설정
            setAnimationCompleted(currentAnimation);
            return prev;
          }
        }
        return next;
      });
    }, config.duration);

    return () => clearInterval(interval);
  }, [isLoaded, config, currentAnimation]);

  // 애니메이션 완료 처리 (렌더링 사이클과 분리)
  useEffect(() => {
    if (animationCompleted) {
      onAnimationComplete?.(animationCompleted);
      setAnimationCompleted(null); // 완료 상태 초기화
    }
  }, [animationCompleted, onAnimationComplete]);

  return { currentFrame, frameWidth, frameHeight, isLoaded };
};
