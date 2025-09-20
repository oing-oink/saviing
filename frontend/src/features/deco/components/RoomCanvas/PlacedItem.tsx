import type { MouseEvent, TouchEvent } from 'react';

interface PlacedItemProps {
  id: string;
  polygons: string[];
  onPick: (payload: { id: string; clientX: number; clientY: number }) => void;
  visible?: boolean;
}

const PlacedItem = ({ id, polygons, onPick, visible = true }: PlacedItemProps) => {
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

  return (
    <g className="cursor-pointer">
      {polygons.map((points, index) => (
        <polygon
          key={`${id}-poly-${index}`}
          points={points}
          fill="rgba(255, 255, 255, 0.35)"
          stroke="rgba(255, 255, 255, 0.6)"
          strokeWidth={1}
          onMouseDown={handleMouseDown}
          onTouchStart={handleTouchStart}
        />
      ))}
    </g>
  );
};

export default PlacedItem;
