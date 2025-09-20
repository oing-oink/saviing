interface GhostItemProps {
  polygons: string[];
  isValid: boolean;
}

const GhostItem = ({ polygons, isValid }: GhostItemProps) => {
  if (!polygons.length) {
    return null;
  }

  const fillColor = isValid ? 'rgba(40, 199, 111, 0.35)' : 'rgba(244, 67, 54, 0.35)';
  const strokeColor = isValid ? 'rgba(40, 199, 111, 0.8)' : 'rgba(244, 67, 54, 0.8)';

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
