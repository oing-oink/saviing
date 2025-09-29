/**
 * 드래그 중인 아이템의 고스트 영역을 표시하기 위한 컴포넌트 속성.
 *
 * 고스트는 사용자가 아이템을 드래그할 때 배치 가능한 위치를 시각적으로
 * 미리보기하는 반투명 영역으로, 배치 가능 여부에 따라 색상이 변경됩니다.
 */
interface GhostItemProps {
  /**
   * 고스트 영역을 구성하는 SVG 폴리곤 좌표 문자열 배열.
   * 각 문자열은 "x1,y1 x2,y2 x3,y3 ..." 형식의 SVG points 속성 값입니다.
   * 다중 셀을 차지하는 아이템의 경우 여러 폴리곤으로 구성될 수 있습니다.
   */
  polygons: string[];

  /**
   * 현재 위치에 아이템을 배치할 수 있는지 여부.
   * true인 경우 녹색 계열로, false인 경우 빨간색 계열로 표시됩니다.
   * 충돌 감지, 레이어 제한, 그리드 경계 등을 종합적으로 검증한 결과입니다.
   */
  isValid: boolean;
}

/**
 * 드래그 중인 아이템의 배치 예상 위치를 시각화하는 고스트 컴포넌트.
 *
 * 이 컴포넌트는 사용자가 아이템을 드래그할 때 마우스 커서 위치에 따라
 * 실시간으로 업데이트되는 반투명 영역을 렌더링합니다. 배치 가능 여부에 따라
 * 시각적 피드백(색상 변경)을 제공하여 사용자 경험을 향상시킵니다.
 *
 * @param props - 고스트 영역 렌더링에 필요한 속성들
 * @returns 고스트 영역을 나타내는 SVG 요소들 또는 null (폴리곤이 없는 경우)
 */
const GhostItem = ({ polygons, isValid }: GhostItemProps) => {
  // 폴리곤이 없으면 렌더링할 것이 없음
  if (!polygons.length) {
    return null;
  }

  // 배치 가능 여부에 따른 색상 결정
  // 유효한 위치: 녹색 계열 (성공/허용을 의미)
  // 무효한 위치: 빨간색 계열 (경고/금지를 의미)
  const fillColor = isValid
    ? 'rgba(40, 199, 111, 0.35)' // 반투명 녹색 (35% 불투명도)
    : 'rgba(244, 67, 54, 0.35)'; // 반투명 빨간색 (35% 불투명도)

  const strokeColor = isValid
    ? 'rgba(40, 199, 111, 0.8)' // 진한 녹색 테두리 (80% 불투명도)
    : 'rgba(244, 67, 54, 0.8)'; // 진한 빨간색 테두리 (80% 불투명도)

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
