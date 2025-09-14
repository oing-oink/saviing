import { useState, useRef, RefObject } from 'react';

interface UseGesturesProps {
  containerRef: RefObject<HTMLElement>;
  targetRef: RefObject<HTMLElement>;
  minScale?: number;
  maxScale?: number;
}

export const useGestures = ({
  containerRef,
  targetRef,
  minScale = 1,
  maxScale = 4,
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
    if (!containerRef.current || !targetRef.current) return pos;

    // 확대된 이미지가 컨테이너보다 클 때, 이동 가능한 최대 거리 계산
    const maxPanX = Math.max(
      0,
      (targetRef.current.offsetWidth * currentScale -
        containerRef.current.clientWidth) /
        2,
    );
    const maxPanY = Math.max(
      0,
      (targetRef.current.offsetHeight * currentScale -
        containerRef.current.clientHeight) /
        2,
    );

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

    if (clampedScale === scale) return; // 한계값이면 무시

    const newPosition = getBoundedPosition(position, clampedScale);

    setScale(clampedScale);
    setPosition(newPosition);
  };

  // 2-2. 마우스로 드래그(패닝)
  // 클릭 시작 지점 저장 + 드래그 모드 활성화
  const handleMouseDown = (e: React.MouseEvent<HTMLElement>) => {
    // 확대된 경우에만 드래그
    if (scale > minScale) {
      e.preventDefault();
      isPanningRef.current = true;
      panStartRef.current = {
        x: e.clientX - position.x,
        y: e.clientY - position.y,
      };
      if (containerRef.current) containerRef.current.style.cursor = 'grabbing';
    }
  };
  // 드래그 중일 때 마우스 위치에 따라 position 업데이트
  const handleMouseMove = (e: React.MouseEvent<HTMLElement>) => {
    if (isPanningRef.current) {
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
    if (containerRef.current) containerRef.current.style.cursor = 'grab';
  };

  // 3. [보조 함수] 핀치 거리 계산
  const getDistance = (touches: React.TouchList) => {
    const [touch1, touch2] = touches;
    return Math.sqrt(
      Math.pow(touch2.clientX - touch1.clientX, 2) +
        Math.pow(touch2.clientY - touch1.clientY, 2),
    );
  };

  // 2-3. 핀치로 확대축소 + 터치로 드래그
  // 터치 시작
  const handleTouchStart = (e: React.TouchEvent<HTMLElement>) => {
    if (e.touches.length === 2) {
      // 핀치 줌
      e.preventDefault();
      isPanningRef.current = false;
      initialPinchDistanceRef.current = getDistance(e.touches);
      initialScaleRef.current = scale;
    } else if (e.touches.length === 1 && scale > minScale) {
      // 드래그(패닝)
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
    } else if (e.touches.length === 1 && isPanningRef.current) {
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

  const gestureHandlers = {
    onWheel: handleWheel,
    onMouseDown: handleMouseDown,
    onMouseMove: handleMouseMove,
    onMouseUp: handleMouseUpOrLeave,
    onMouseLeave: handleMouseUpOrLeave,
    onTouchStart: handleTouchStart,
    onTouchMove: handleTouchMove,
    onTouchEnd: handleTouchEnd,
  };

  return { scale, position, gestureHandlers };
};