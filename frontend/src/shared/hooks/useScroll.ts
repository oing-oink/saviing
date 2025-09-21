import { useCallback, useEffect, useRef, useState } from 'react';

type ScrollDirection = 'up' | 'down' | null;

interface UseScrollOptions {
  /** 스크롤 이벤트 간 최소 실행 간격(ms). mode === 'throttle'일 때만 사용 */
  throttleMs?: number;
  /** 방향 변화를 인정할 최소 이동(px) */
  threshold?: number;
  /** 페이지 맨 아래 판정 시 여유(px) */
  bottomOffset?: number;
  /** 훅 활성/비활성 */
  enabled?: boolean;
  /** 스크롤 처리 모드: 프레임당 1회(raf) 또는 간격(throttle) */
  mode?: 'raf' | 'throttle';
}

interface UseScrollReturn {
  scrollY: number;
  scrollDirection: ScrollDirection;
  isScrolling: boolean;
  isAtTop: boolean;
  isAtBottom: boolean;
}

export const useScroll = (options: UseScrollOptions = {}): UseScrollReturn => {
  const {
    throttleMs = 80,
    threshold = 5,
    bottomOffset = 10,
    enabled = true,
    mode = 'raf',
  } = options;

  const [state, setState] = useState<UseScrollReturn>({
    scrollY: 0,
    scrollDirection: null,
    isScrolling: false,
    isAtTop: true,
    isAtBottom: false,
  });

  const lastY = useRef(0);
  const lastInvoke = useRef(0);
  const stopTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const ticking = useRef(false);
  const rafId = useRef<number | null>(null);

  const readAndUpdate = useCallback(() => {
    // PageScrollArea의 viewport를 우선 확인
    const scrollViewport = document.querySelector('[data-slot="scroll-area-viewport"]') as HTMLElement;
    let y = 0;
    
    if (scrollViewport) {
      // PageScrollArea가 있으면 해당 컨테이너의 scrollTop 사용
      y = scrollViewport.scrollTop ?? 0;
    } else {
      // PageScrollArea가 없으면 기존 window 스크롤 사용
      const doc = document.documentElement;
      y = window.scrollY ?? doc.scrollTop ?? 0;
    }
    
    const diff = y - lastY.current;

    setState(prev => {
      const nextDirection: ScrollDirection =
        Math.abs(diff) > threshold
          ? diff > 0
            ? 'down'
            : 'up'
          : prev.scrollDirection;

      // isAtBottom 계산
      let isAtBottom = false;
      if (scrollViewport) {
        isAtBottom = y + scrollViewport.clientHeight >= scrollViewport.scrollHeight - bottomOffset;
      } else {
        const doc = document.documentElement;
        isAtBottom = y + doc.clientHeight >= doc.scrollHeight - bottomOffset;
      }

      const next: UseScrollReturn = {
        scrollY: y,
        scrollDirection: nextDirection,
        isScrolling: true,
        isAtTop: y <= 0,
        isAtBottom,
      };

      // 값이 동일하면 리렌더 스킵
      if (
        prev.scrollY === next.scrollY &&
        prev.scrollDirection === next.scrollDirection &&
        prev.isScrolling === next.isScrolling &&
        prev.isAtTop === next.isAtTop &&
        prev.isAtBottom === next.isAtBottom
      ) {
        return prev;
      }
      return next;
    });

    if (Math.abs(diff) > threshold) {
      lastY.current = y;
    }

    // 스크롤 종료 감지(디바운스)
    if (stopTimer.current) {
      clearTimeout(stopTimer.current);
    }
    stopTimer.current = setTimeout(() => {
      setState(prev =>
        prev.isScrolling ? { ...prev, isScrolling: false } : prev,
      );
    }, 150);
  }, [threshold, bottomOffset]);

  const onScroll = useCallback(() => {
    if (!enabled) {
      return;
    }

    if (mode === 'raf') {
      if (ticking.current) {
        return;
      }
      ticking.current = true;
      rafId.current = requestAnimationFrame(() => {
        ticking.current = false;
        readAndUpdate();
      });
      return;
    }

    // throttle 모드
    const now = Date.now();
    if (now - lastInvoke.current >= throttleMs) {
      lastInvoke.current = now;
      readAndUpdate();
    }
  }, [enabled, mode, throttleMs, readAndUpdate]);

  useEffect(() => {
    if (typeof window === 'undefined' || !enabled) {
      return;
    }

    // PageScrollArea viewport 확인
    const scrollViewport = document.querySelector('[data-slot="scroll-area-viewport"]') as HTMLElement;
    
    // 초기화
    let initialY = 0;
    if (scrollViewport) {
      initialY = scrollViewport.scrollTop ?? 0;
    } else {
      initialY = window.scrollY ?? 0;
    }
    
    lastY.current = initialY;
    setState(prev => ({
      ...prev,
      scrollY: initialY,
      isAtTop: initialY <= 0,
    }));

    // 스크롤 이벤트 리스너 추가
    if (scrollViewport) {
      scrollViewport.addEventListener('scroll', onScroll, { passive: true });
    } else {
      window.addEventListener('scroll', onScroll, { passive: true });
    }

    return () => {
      if (scrollViewport) {
        scrollViewport.removeEventListener('scroll', onScroll);
      } else {
        window.removeEventListener('scroll', onScroll);
      }
      if (rafId.current != null) {
        cancelAnimationFrame(rafId.current);
      }
      if (stopTimer.current) {
        clearTimeout(stopTimer.current);
      }
    };
  }, [enabled, onScroll]);

  return state;
};
