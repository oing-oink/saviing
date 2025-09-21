/** 드래그 중 고스트 폴리곤 표시를 위한 속성. */
interface GhostItemProps {
  polygons: string[];
  isValid: boolean;
}

/** 배치 가능 여부에 따라 색이 달라지는 고스트 영역을 SVG로 렌더링한다. */
const GhostItem = ({ polygons, isValid }: GhostItemProps) => {
  if (!polygons.length) {
    return null;
  }

  const fillColor = isValid
    ? 'rgba(40, 199, 111, 0.35)'
    : 'rgba(244, 67, 54, 0.35)';
  const strokeColor = isValid
    ? 'rgba(40, 199, 111, 0.8)'
    : 'rgba(244, 67, 54, 0.8)';

  return (
    <g pointerEvents="none">
      {polygons.map((points, index) => (
        <polygon
          key={`ghost-${index}`}
          points={points}
          fill={fillColor}
          stroke={strokeColor}
          strokeWidth={1.5}
        />
      ))}
    </g>
  );
};

export default GhostItem;
