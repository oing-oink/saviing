import { useEffect } from 'react';
import confetti from 'canvas-confetti';

/**
 * 가챠 결과 화면에서 폭죽 애니메이션을 제어한다.
 * @param isActive 폭죽 효과 활성화 여부
 */
export const useFireworks = (isActive: boolean) => {
  /**
   * 화면 중앙 부근에서 일정 시간 동안 폭죽을 랜덤 발사한다.
   * @returns 애니메이션 제어에 사용되는 interval ID
   */
  const startCenterFireworks = () => {
    const duration = 3 * 1000;
    const animationEnd = Date.now() + duration;

    const interval = window.setInterval(() => {
      const timeLeft = animationEnd - Date.now();

      if (timeLeft <= 0) {
        return clearInterval(interval);
      }

      const particleCount = 50 * (timeLeft / duration);

      confetti({
        particleCount,
        startVelocity: 40,
        spread: 360,
        ticks: 60,
        origin: {
          x: Math.random(),
          y: Math.random() * 0.5,
        },
      });
    }, 250);

    return interval;
  };

  /** 화면 양쪽 끝에서 교차로 폭죽을 발사한다. */
  const startSideFireworks = () => {
    const duration = 2000;
    const end = Date.now() + duration;

    (function frame() {
      confetti({
        particleCount: 5,
        angle: 60,
        spread: 30,
        origin: { x: 0 },
      });
      confetti({
        particleCount: 5,
        angle: 120,
        spread: 30,
        origin: { x: 1 },
      });

      if (Date.now() < end) {
        requestAnimationFrame(frame);
      }
    })();
  };

  useEffect(() => {
    if (!isActive) {
      return;
    }

    const interval = startCenterFireworks();
    startSideFireworks();

    return () => clearInterval(interval);
  }, [isActive]);
};
