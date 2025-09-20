import type { RefObject } from 'react';
import { useState, useRef, useEffect } from 'react';

interface UseGesturesProps {
  containerRef: RefObject<HTMLElement | null>;
  targetRef: RefObject<HTMLElement | null>;
  minScale?: number;
  maxScale?: number;
  panEnabled?: boolean;
}

export const useGestures = ({
  containerRef,
  targetRef,
  minScale = 1,
  maxScale = 4,
  panEnabled = true,
}: UseGesturesProps) => {
  // 화면에 반영할 값 (배율, 위치)
  const [scale, setScale] = useState(1);
  const [position, setPosition] = useState({ x: 0, y: 0 });

  // 제스처 임시 저장값 -> 렌더링 필요 x(ref 사용)
  const isPanningRef = useRef(false);
  const panStartRef = useRef({ x: 0, y: 0 });
  const initialScaleRef = useRef(1);
  const initialPinchDistanceRef = useRef(0);

  // 1. [보조 함수] 이미지가 바깥을 벗어나지 않도록 위치 제한
  const getBoundedPosition = (
    pos: { x: number; y: number },
    currentScale: number,
  ) => {
    if (!containerRef.current || !targetRef.current) {
      return pos;
    }

    // 확대된 이미지가 컨테이너보다 클 때, 이동 가능한 최대 거리 계산 (드래그 범위 확장)
    const maxPanX =
      Math.max(
        0,
        (targetRef.current.offsetWidth * currentScale -
          containerRef.current.clientWidth) /
          2,
      ) + 100; // 드래그 범위를 x축으로 200px 더 확장
    const maxPanY =
      Math.max(
        0,
        (targetRef.current.offsetHeight * currentScale -
          containerRef.current.clientHeight) /
          2,
      ) + 100; // 드래그 범위를 y축으로 200px 더 확장

    // 이미지가 바깥으로 안 나가게 위치(x, y 이동 범위) 제한
    return {
      x: Math.max(-maxPanX, Math.min(maxPanX, pos.x)),
      y: Math.max(-maxPanY, Math.min(maxPanY, pos.y)),
    };
  };

  // 2-1. 마우스 휠로 확대축소
  const handleWheel = (e: React.WheelEvent<HTMLElement>) => {
    e.preventDefault();
    const newScale = scale - e.deltaY * 0.001;
    const clampedScale = Math.min(Math.max(minScale, newScale), maxScale); // 최소 1배 ~ 최대 4배

    if (clampedScale === scale) {
      return;
    } // 한계값이면 무시

    const newPosition = getBoundedPosition(position, clampedScale);

    setScale(clampedScale);
    setPosition(newPosition);
  };

  // 2-2. 마우스로 드래그(패닝)
  // 클릭 시작 지점 저장 + 드래그 모드 활성화
  const handleMouseDown = (e: React.MouseEvent<HTMLElement>) => {
    if (!(panEnabled && scale > minScale)) {
      isPanningRef.current = false;
      return;
    }
    e.preventDefault();
    isPanningRef.current = true;
    panStartRef.current = {
      x: e.clientX - position.x,
      y: e.clientY - position.y,
    };
    if (containerRef.current) {
      containerRef.current.style.cursor = 'grabbing';
    }
  };
  // 드래그 중일 때 마우스 위치에 따라 position 업데이트
  const handleMouseMove = (e: React.MouseEvent<HTMLElement>) => {
    if (isPanningRef.current && panEnabled) {
      e.preventDefault();
      const newPos = {
        x: e.clientX - panStartRef.current.x,
        y: e.clientY - panStartRef.current.y,
      };
      setPosition(getBoundedPosition(newPos, scale));
    }
  };
  // 마우스를 떼거나 밖으로 나가면 드래그 종료
  const handleMouseUpOrLeave = () => {
    isPanningRef.current = false;
    if (containerRef.current) {
      containerRef.current.style.cursor = 'grab';
    }
  };

  // 2-3. 핀치로 확대축소 + 터치로 드래그
  // 터치 시작
  const handleTouchStart = (e: React.TouchEvent<HTMLElement>) => {
    if (e.touches.length === 2) {
      e.preventDefault();
      isPanningRef.current = false;
      initialPinchDistanceRef.current = getDistance(e.touches);
      initialScaleRef.current = scale;
      return;
    }
    if (!(panEnabled && scale > minScale)) {
      isPanningRef.current = false;
      return;
    }
    if (e.touches.length === 1) {
      e.preventDefault();
      isPanningRef.current = true;
      const touch = e.touches[0];
      panStartRef.current = {
        x: touch.clientX - position.x,
        y: touch.clientY - position.y,
      };
    }
  };
  // 터치 이동
  const handleTouchMove = (e: React.TouchEvent<HTMLElement>) => {
    if (e.touches.length === 2) {
      // 핀치 줌
      e.preventDefault();
      const newDistance = getDistance(e.touches);
      const newScale =
        initialScaleRef.current *
        (newDistance / initialPinchDistanceRef.current);
      const clampedScale = Math.min(Math.max(minScale, newScale), maxScale);

      const newPosition = getBoundedPosition(position, clampedScale);

      setScale(clampedScale);
      setPosition(newPosition);
    } else if (e.touches.length === 1 && isPanningRef.current && panEnabled) {
      // 드래그(패닝)
      e.preventDefault();
      const touch = e.touches[0];
      const newPos = {
        x: touch.clientX - panStartRef.current.x,
        y: touch.clientY - panStartRef.current.y,
      };
      setPosition(getBoundedPosition(newPos, scale));
    }
  };
  // 터치 종료
  const handleTouchEnd = () => {
    isPanningRef.current = false;
  };

  // 3. [보조 함수] 핀치 거리 계산
  const getDistance = (touches: React.TouchList) => {
    const touchesArray = Array.from(touches);
    const [touch1, touch2] = touchesArray;
    return Math.sqrt(
      Math.pow(touch2.clientX - touch1.clientX, 2) +
        Math.pow(touch2.clientY - touch1.clientY, 2),
    );
  };

  // [이벤트 핸들러 등록] useEffect를 사용하여 DOM 요소에 직접 이벤트 리스너 추가
  useEffect(() => {
    const element = containerRef.current;
    if (!element) {
      return;
    }

    // React의 SyntheticEvent가 아닌 네이티브 이벤트를 직접 사용하므로, 타입 변환이 필요합니다.
    const onWheel = (e: Event) =>
      handleWheel(e as unknown as React.WheelEvent<HTMLElement>);
    // RoomCanvas에서 panEnabled를 끄면 마우스 이벤트가 들어와도 패닝을 시작하지 않는다.
    const onMouseDown = (e: Event) => {
      if (!panEnabled) {
        return;
      }
      handleMouseDown(e as unknown as React.MouseEvent<HTMLElement>);
    };
    const onMouseMove = (e: Event) => {
      if (!panEnabled) {
        return;
      }
      handleMouseMove(e as unknown as React.MouseEvent<HTMLElement>);
    };
    const onMouseUpOrLeave = () => handleMouseUpOrLeave();
    const onTouchStart = (e: Event) =>
      handleTouchStart(e as unknown as React.TouchEvent<HTMLElement>);
    const onTouchMove = (e: Event) =>
      handleTouchMove(e as unknown as React.TouchEvent<HTMLElement>);
    const onTouchEnd = () => handleTouchEnd();

    // preventDefault가 필요한 이벤트에 passive: false 옵션 추가해 브라우저에 명시적으로 사용 알려주기
    element.addEventListener('wheel', onWheel, { passive: false });
    element.addEventListener('mousedown', onMouseDown, { passive: false });
    element.addEventListener('mousemove', onMouseMove, { passive: false });
    element.addEventListener('touchstart', onTouchStart, { passive: false });
    element.addEventListener('touchmove', onTouchMove, { passive: false });

    // passive 옵션이 필요 없는 나머지 이벤트 등록
    element.addEventListener('mouseup', onMouseUpOrLeave);
    element.addEventListener('mouseleave', onMouseUpOrLeave);
    element.addEventListener('touchend', onTouchEnd);

    // 클린업 함수: 컴포넌트 언마운트 시 등록했던 이벤트 리스너 모두 제거하여 메모리 차지 방지
    return () => {
      element.removeEventListener('wheel', onWheel);
      element.removeEventListener('mousedown', onMouseDown);
      element.removeEventListener('mousemove', onMouseMove);
      element.removeEventListener('mouseup', onMouseUpOrLeave);
      element.removeEventListener('mouseleave', onMouseUpOrLeave);
      element.removeEventListener('touchstart', onTouchStart);
      element.removeEventListener('touchmove', onTouchMove);
      element.removeEventListener('touchend', onTouchEnd);
    };
    // 의존성 배열: 핸들러 함수들이 새로운 state를 참조할 수 있도록 관련 state와 함수 포함
  }, [containerRef, scale, position, minScale, maxScale, panEnabled]);

  useEffect(() => {
    if (!panEnabled) {
      isPanningRef.current = false;
      if (containerRef.current) {
        containerRef.current.style.cursor = 'default';
      }
    }
  }, [panEnabled]);

  useEffect(() => {
    if (!panEnabled) {
      isPanningRef.current = false;
      if (containerRef.current) {
        containerRef.current.style.cursor = 'default';
      }
    }
  }, [panEnabled, containerRef]);

  return { scale, position };
};
