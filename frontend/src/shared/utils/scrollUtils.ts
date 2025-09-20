import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

/**
 * 페이지 최상단으로 스크롤하는 함수
 *
 * @param smooth - 부드러운 스크롤 여부 (기본값: true)
 */
export const scrollToTop = (smooth: boolean = true) => {
  try {
    // ScrollArea의 viewport를 찾아서 스크롤
    const scrollViewport = document.querySelector(
      '[data-slot="scroll-area-viewport"]',
    );

    if (scrollViewport && typeof scrollViewport.scrollTo === 'function') {
      scrollViewport.scrollTo({
        top: 0,
        behavior: smooth ? 'smooth' : 'instant',
      });
    } else {
      // ScrollArea가 없거나 scrollTo가 지원되지 않는 경우 일반 window 스크롤
      window.scrollTo({
        top: 0,
        behavior: smooth ? 'smooth' : 'instant',
      });
    }
  } catch (error) {
    // 에러 발생 시 기본 window 스크롤로 폴백
    console.warn(
      'ScrollArea scrolling failed, falling back to window scroll:',
      error,
    );
    window.scrollTo({ top: 0, behavior: smooth ? 'smooth' : 'instant' });
  }
};

/**
 * 특정 요소로 스크롤하는 함수
 *
 * @param elementId - 스크롤할 요소의 ID
 * @param smooth - 부드러운 스크롤 여부 (기본값: true)
 * @param offset - 스크롤 위치 오프셋 (px, 기본값: 0)
 */
export const scrollToElement = (
  elementId: string,
  smooth: boolean = true,
  offset: number = 0,
) => {
  try {
    const element = document.getElementById(elementId);
    if (!element) {
      return;
    }

    const scrollViewport = document.querySelector(
      '[data-slot="scroll-area-viewport"]',
    );

    if (scrollViewport && typeof scrollViewport.scrollTo === 'function') {
      const elementRect = element.getBoundingClientRect();
      const viewportRect = scrollViewport.getBoundingClientRect();
      const scrollTop = scrollViewport.scrollTop;

      const targetPosition =
        scrollTop + elementRect.top - viewportRect.top - offset;

      scrollViewport.scrollTo({
        top: targetPosition,
        behavior: smooth ? 'smooth' : 'instant',
      });
    } else {
      // ScrollArea가 없는 경우 일반 스크롤
      const targetPosition = element.offsetTop - offset;
      window.scrollTo({
        top: targetPosition,
        behavior: smooth ? 'smooth' : 'instant',
      });
    }
  } catch (error) {
    console.warn('ScrollArea element scrolling failed:', error);
  }
};

/**
 * 라우터 변경 시 자동으로 스크롤을 최상단으로 리셋하는 커스텀 훅
 *
 * 일반적으로 전역 레이아웃에서 사용하지 않고, 필요한 개별 페이지에서만 사용하세요.
 *
 * @param enabled - 스크롤 리셋 활성화 여부 (기본값: true)
 * @param smooth - 부드러운 스크롤 여부 (기본값: false)
 */
export const useScrollReset = (
  enabled: boolean = true,
  smooth: boolean = false,
) => {
  const location = useLocation();

  useEffect(() => {
    if (enabled) {
      // 약간의 지연을 두어 페이지 렌더링 후 스크롤
      const timeoutId = setTimeout(() => {
        scrollToTop(smooth);
      }, 0);

      return () => clearTimeout(timeoutId);
    }
  }, [location.pathname, enabled, smooth]);
};
