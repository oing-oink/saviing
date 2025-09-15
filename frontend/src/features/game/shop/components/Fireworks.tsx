import { useEffect } from 'react';
import confetti from 'canvas-confetti';

interface FireworksProps {
  isActive: boolean;
}

const Fireworks = ({ isActive }: FireworksProps) => {
  useEffect(() => {
    if (!isActive) {
      return;
    }

    // 기존 폭죽 효과
    const duration = 3 * 1000; // 3초 동안
    const animationEnd = Date.now() + duration;

    const interval: NodeJS.Timeout = setInterval(() => {
      const timeLeft = animationEnd - Date.now();

      if (timeLeft <= 0) {
        return clearInterval(interval);
      }

      const particleCount = 50 * (timeLeft / duration);

      // 화면 중앙 위쪽에서 불꽃 발사
      confetti({
        particleCount,
        startVelocity: 40,
        spread: 360,
        ticks: 60,
        origin: {
          x: Math.random(), // 랜덤한 x 위치
          y: Math.random() * 0.5, // 화면 위쪽 절반까지만
        },
      });
    }, 250);

    // GachaResult에서 온 confetti 효과 추가
    const gachaDuration = 2000; // 2초 동안
    const end = Date.now() + gachaDuration;

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

    return () => clearInterval(interval);
  }, [isActive]);

  return null;
};

export default Fireworks;
