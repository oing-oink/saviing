import type { MouseEvent, TouchEvent } from 'react';

/**
 * 배치된 아이템과의 상호작용을 위한 투명 폴리곤 컴포넌트 속성.
 *
 * 이 컴포넌트는 이미 배치된 아이템들을 시각적으로 선택 가능하게 하거나
 * 하이라이트 표시하기 위해 사용됩니다. 실제 아이템 이미지 위에 투명한
 * 상호작용 레이어를 제공합니다.
 */
interface PlacedItemProps {
  /**
   * 배치된 아이템의 고유 식별자.
   * 이벤트 핸들러에서 어떤 아이템이 선택되었는지 식별하는 데 사용됩니다.
   */
  id: string;

  /**
   * 아이템이 차지하는 영역을 나타내는 SVG 폴리곤 좌표 배열.
   * 각 문자열은 "x1,y1 x2,y2 x3,y3 ..." 형식의 SVG points 속성 값입니다.
   * 복잡한 모양이나 다중 셀 아이템의 경우 여러 폴리곤으로 구성될 수 있습니다.
   */
  polygons: string[];

  /**
   * 아이템이 선택되었을 때 호출되는 콜백 함수.
   * 아이템 ID와 클릭/터치 위치 좌표를 전달받아 드래그 세션을 시작하거나
   * 기타 상호작용을 처리합니다.
   *
   * @param payload - 선택된 아이템 정보와 포인터 위치
   * @param payload.id - 선택된 아이템의 ID
   * @param payload.clientX - 클릭/터치 시점의 화면 X 좌표
   * @param payload.clientY - 클릭/터치 시점의 화면 Y 좌표
   */
  onPick: (payload: { id: string; clientX: number; clientY: number }) => void;

  /**
   * 폴리곤의 표시 여부.
   * false인 경우 컴포넌트가 렌더링되지 않습니다.
   * 조건부 상호작용 활성화에 사용됩니다.
   * @default true
   */
  visible?: boolean;

  /**
   * 폴리곤의 시각적 표현 방식.
   * - 'highlight': 반투명 흰색으로 아이템을 강조 표시
   * - 'hit': 완전 투명하지만 클릭 이벤트는 처리 (히트박스)
   * @default 'highlight'
   */
  variant?: 'highlight' | 'hit';
}

/**
 * 배치된 아이템과의 사용자 상호작용을 처리하는 폴리곤 컴포넌트.
 *
 * 이 컴포넌트는 실제 아이템 이미지 위에 투명한 상호작용 레이어를 제공하여
 * 사용자가 배치된 아이템을 클릭하거나 터치했을 때 드래그 세션을 시작하거나
 * 기타 편집 동작을 수행할 수 있게 합니다. variant에 따라 시각적 피드백의
 * 강도를 조절할 수 있습니다.
 *
 * @param props - 폴리곤 렌더링과 상호작용 처리에 필요한 속성들
 * @returns 상호작용 가능한 SVG 폴리곤 요소들 또는 null (visible이 false인 경우)
 */
const PlacedItem = ({
  id,
  polygons,
  onPick,
  visible = true,
  variant = 'highlight',
}: PlacedItemProps) => {
  /**
   * 마우스 클릭 이벤트 처리기.
   * 이벤트 전파를 막고 onPick 콜백을 호출하여 아이템 선택을 처리합니다.
   *
   * @param event - React 마우스 이벤트 객체
   */
  const handleMouseDown = (event: MouseEvent<SVGPolygonElement>) => {
    if (event.cancelable) {
      event.preventDefault();
    }
    event.stopPropagation();
    onPick({ id, clientX: event.clientX, clientY: event.clientY });
  };

  /**
   * 터치 시작 이벤트 처리기.
   * 모바일 환경에서의 터치 입력을 처리하며, 첫 번째 터치 포인트의
   * 좌표를 사용하여 onPick 콜백을 호출합니다.
   *
   * @param event - React 터치 이벤트 객체
   */
  const handleTouchStart = (event: TouchEvent<SVGPolygonElement>) => {
    // React 18에서는 기본으로 passive 리스너가 사용되므로 preventDefault 호출을 건너뛴다.
    event.stopPropagation();
    const touch = event.changedTouches[0];
    onPick({ id, clientX: touch.clientX, clientY: touch.clientY });
  };

  // visible이 false이면 렌더링하지 않음
  if (!visible) {
    return null;
  }

  // variant에 따른 시각적 스타일 결정
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
          data-interactive="true"
          onMouseDown={handleMouseDown}
          onTouchStart={handleTouchStart}
          pointerEvents="auto"
        />
      ))}
    </g>
  );
};

export default PlacedItem;
