import { useEffect } from 'react';
import confetti from 'canvas-confetti';

export const useFireworks = (isActive: boolean) => {
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
