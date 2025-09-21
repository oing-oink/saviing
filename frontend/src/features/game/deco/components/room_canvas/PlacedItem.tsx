import type { MouseEvent, TouchEvent } from 'react';

/** 확정 배치된 아이템을 선택하거나 하이라이트하기 위한 SVG 폴리곤 속성. */
interface PlacedItemProps {
  id: string;
  polygons: string[];
  onPick: (payload: { id: string; clientX: number; clientY: number }) => void;
  visible?: boolean;
  variant?: 'highlight' | 'hit';
}

/** 배치된 아이템 영역을 클릭/터치로 선택할 수 있도록 SVG 폴리곤을 렌더링한다. */
const PlacedItem = ({
  id,
  polygons,
  onPick,
  visible = true,
  variant = 'highlight',
}: PlacedItemProps) => {
  const handleMouseDown = (event: MouseEvent<SVGPolygonElement>) => {
    event.preventDefault();
    event.stopPropagation();
    onPick({ id, clientX: event.clientX, clientY: event.clientY });
  };

  const handleTouchStart = (event: TouchEvent<SVGPolygonElement>) => {
    event.preventDefault();
    event.stopPropagation();
    const touch = event.changedTouches[0];
    onPick({ id, clientX: touch.clientX, clientY: touch.clientY });
  };

  if (!visible) {
    return null;
  }

  const fill =
    variant === 'highlight' ? 'rgba(255, 255, 255, 0.35)' : 'transparent';
  const stroke =
    variant === 'highlight' ? 'rgba(255, 255, 255, 0.6)' : 'transparent';

  return (
    <g className="cursor-pointer">
      {polygons.map((points, index) => (
        <polygon
          key={`${id}-poly-${index}`}
          points={points}
          fill={fill}
          stroke={stroke}
          strokeWidth={1}
          onMouseDown={handleMouseDown}
          onTouchStart={handleTouchStart}
          pointerEvents="auto"
        />
      ))}
    </g>
  );
};

export default PlacedItem;
