import type { PlacementArea } from '@/features/game/room/hooks/useGrid';
import type { TabId } from '@/features/game/shop/types/item';
import type { Item } from '@/features/game/shop/types/item';

/**
 * 방 그리드 시스템의 기본 구성 정보를 정의하는 메타데이터.
 *
 * 이 인터페이스는 방의 물리적 특성과 사용 가능한 배치 레이어를 정의하여
 * 아이템 배치 시스템의 기본 규칙을 설정합니다.
 */
export interface RoomMeta {
  /**
   * 그리드 셀의 기본 크기 단위 (픽셀 또는 상대 단위).
   * 기본값은 1이며, 모든 아이템 배치 계산의 기준이 됩니다.
   */
  cellSize?: number;

  /**
   * 아이템을 배치할 수 있는 레이어 목록.
   * ['LEFT', 'RIGHT', 'BOTTOM', 'ROOM_COLOR'] 등의 API 표준 카테고리를 가지며,
   * 각 레이어는 서로 다른 배치 규칙과 시각적 렌더링을 적용받습니다.
   */
  layers?: PlacementArea[];
}

/**
 * 방에 배치된 아이템의 완전한 상태 정보를 담는 핵심 엔티티.
 *
 * 이 인터페이스는 아이템의 물리적 배치 정보(좌표, 크기, 회전)와
 * 시각적 표현 정보(이미지, 타입), 그리고 상태 메타데이터를 모두 포함합니다.
 */
export interface PlacedItem {
  /**
   * 배치된 아이템의 고유 식별자.
   * 드래프트 상태에서는 `draft-${itemId}-${timestamp}` 형식으로 생성되고,
   * 서버에 저장된 후에는 서버가 제공하는 ID로 업데이트됩니다.
   */
  id: string;

  /**
   * 사용자 인벤토리에서의 아이템 인스턴스 ID.
   * 서버 저장 시 인벤토리 소모를 추적하기 위해 사용됩니다.
   */
  inventoryItemId?: number;

  /**
   * 아이템의 종류를 나타내는 마스터 데이터 ID.
   * 아이템 이미지, 크기, 속성 등을 결정하는 기준 식별자입니다.
   */
  itemId: number;

  /**
   * 아이템이 배치된 그리드 셀의 식별자.
   * `{placementArea}-{row}-{col}` 형식 (예: "BOTTOM-5-3")으로 구성되며,
   * 아이템의 좌상단 기준점이 위치한 셀을 나타냅니다.
   */
  cellId: string;

  /**
   * 그리드에서의 X축 좌표 (열 번호).
   * 0부터 시작하며, cellId에서 파싱된 값과 동일해야 합니다.
   */
  positionX: number;

  /**
   * 그리드에서의 Y축 좌표 (행 번호).
   * 0부터 시작하며, cellId에서 파싱된 값과 동일해야 합니다.
   */
  positionY: number;

  /**
   * 아이템의 회전 각도 (도 단위).
   * 90도 단위로만 회전 가능하며, 시계방향 기준입니다.
   * 회전에 따라 footprint 계산이 달라집니다.
   */
  rotation: 0 | 90 | 180 | 270;

  /**
   * 아이템이 배치된 레이어 타입.
   * 'LEFT', 'RIGHT', 'BOTTOM', 'ROOM_COLOR' 등의 API 표준 카테고리를 가지며,
   * 렌더링 순서와 상호작용 규칙을 결정합니다.
   */
  layer?: PlacementArea | string;

  /**
   * 아이템이 차지하는 그리드의 가로 길이 (셀 개수).
   * 최소값은 1이며, rotation에 따라 실제 점유 영역이 달라질 수 있습니다.
   */
  xLength: number;

  /**
   * 아이템이 차지하는 그리드의 세로 길이 (셀 개수).
   * 최소값은 1이며, rotation에 따라 실제 점유 영역이 달라질 수 있습니다.
   */
  yLength: number;

  /**
   * 아이템이 실제로 점유하는 모든 그리드 셀의 ID 배열.
   * 명시적으로 제공되지 않으면 cellId, xLength, yLength로부터 계산됩니다.
   * 복잡한 모양의 아이템이나 회전된 아이템의 정확한 충돌 감지에 사용됩니다.
   */
  footprintCellIds?: string[];

  /**
   * 셀 내에서의 X축 미세 조정 오프셋 (픽셀 단위).
   * 그리드에 정확히 맞지 않는 아이템의 시각적 정렬을 위해 사용됩니다.
   */
  offsetX?: number;

  /**
   * 셀 내에서의 Y축 미세 조정 오프셋 (픽셀 단위).
   * 그리드에 정확히 맞지 않는 아이템의 시각적 정렬을 위해 사용됩니다.
   */
  offsetY?: number;

  /**
   * 아이템의 커스텀 이미지 URL.
   * 제공되지 않으면 itemId를 기반으로 기본 이미지를 사용합니다.
   */
  imageUrl?: string;

  /**
   * 아이템의 분류 타입 ('DECORATION', 'FURNITURE' 등).
   * 게임 로직이나 UI 필터링에서 아이템을 분류하는 데 사용됩니다.
   */
  itemType?: string;

  /**
   * 미리보기 모드 여부를 나타내는 플래그.
   * true인 경우 실제 저장되지 않는 임시 배치 상태를 의미합니다.
   */
  isPreview?: boolean;

  /**
   * 이 아이템이 인벤토리의 어떤 슬롯에서 왔는지를 나타내는 ID.
   * 예: "slot-0", "slot-1" 등
   * 개별 슬롯 추적을 통해 중복 아이템의 개별 관리를 가능하게 합니다.
   */
  slotId?: string;
}

/**
 * 드래그 앤 드롭 세션 동안 아이템의 임시 상태를 추적하는 정보.
 *
 * 이 인터페이스는 사용자가 아이템을 드래그하는 동안의 모든 상태를 관리하며,
 * 인벤토리에서 시작된 드래그와 이미 배치된 아이템의 재배치를 모두 지원합니다.
 */
export interface DragSession {
  /**
   * 드래그 중인 아이템의 마스터 데이터 ID.
   * 인벤토리나 배치된 아이템에서 가져온 아이템 식별자입니다.
   */
  itemId: string;

  /**
   * 인벤토리에서 드래그 시작한 슬롯의 고유 ID.
   * 예: "slot-0", "slot-1" 등
   * 개별 슬롯 추적을 통해 중복 아이템의 개별 관리를 가능하게 합니다.
   */
  slotId?: string;

  /**
   * 재배치의 경우, 원본 배치 아이템의 ID.
   * 인벤토리에서 새로 드래그하는 경우에는 undefined입니다.
   * 이 값이 있으면 기존 아이템을 이동하는 것으로 간주됩니다.
   */
  originPlacedId?: string;

  /**
   * 재배치의 경우, 원본 아이템의 완전한 상태 정보.
   * 드래그 취소 시 원래 상태로 복원하는 데 사용됩니다.
   */
  originalItem?: PlacedItem;

  /**
   * 현재 마우스/터치 포인터가 위치한 그리드 셀의 ID.
   * 실시간으로 업데이트되며, 고스트 표시와 배치 검증에 사용됩니다.
   */
  hoverCellId: string | null;

  /**
   * 아이템이 배치 가능한 레이어 타입 제한.
   * null이면 모든 레이어에 배치 가능하고, 특정 값이면 해당 레이어만 허용됩니다.
   */
  allowedGridType: TabId | null;

  /**
   * 드래그 중인 아이템의 가로 크기 (셀 개수).
   * 배치 검증과 footprint 계산에 사용됩니다.
   */
  xLength: number;

  /**
   * 드래그 중인 아이템의 세로 크기 (셀 개수).
   * 배치 검증과 footprint 계산에 사용됩니다.
   */
  yLength: number;

  /**
   * 명시적으로 지정된 점유 셀 ID 배열.
   * 복잡한 모양의 아이템이나 회전된 아이템의 정확한 배치 검증에 사용됩니다.
   */
  footprintCellIds?: string[];

  /**
   * 드래그 중 아이템의 X축 렌더링 오프셋.
   * 시각적 정렬 조정에 사용됩니다.
   */
  offsetX?: number;

  /**
   * 드래그 중 아이템의 Y축 렌더링 오프셋.
   * 시각적 정렬 조정에 사용됩니다.
   */
  offsetY?: number;

  /**
   * 드래그 중 아이템의 미리보기 이미지 URL.
   * 제공되지 않으면 itemId를 기반으로 기본 이미지를 사용합니다.
   */
  imageUrl?: string;

  /**
   * 드래그 중인 아이템의 분류 타입.
   * 배치 규칙이나 시각적 구분에 사용됩니다.
   */
  itemType?: string;

  /**
   * 미리보기 모드 드래그 여부.
   * true인 경우 실제 저장되지 않는 임시 배치로 처리됩니다.
   */
  isPreview?: boolean;
}

/**
 * 데코레이션 시스템의 전체 상태를 관리하는 중앙 상태 구조.
 *
 * 이 인터페이스는 방의 메타데이터, 저장된 아이템, 편집 중인 아이템,
 * 현재 드래그 세션, 그리고 UI 상태를 모두 포함합니다.
 */
export interface DecoState {
  /**
   * 방의 기본 구성 정보 (그리드 크기, 레이어 등).
   */
  roomMeta: RoomMeta;

  /**
   * 서버에 저장된 확정 상태의 배치 아이템 목록.
   * 이 배열은 마지막 저장 시점의 상태를 나타냅니다.
   */
  placedItems: PlacedItem[];

  /**
   * 현재 편집 중인 아이템 목록 (미저장 상태 포함).
   * 사용자가 배치/이동/삭제한 모든 변경사항이 반영된 상태입니다.
   */
  draftItems: PlacedItem[];

  /**
   * 현재 진행 중인 드래그 세션 정보.
   * 드래그가 진행 중일 때만 값이 있고, 평상시에는 null입니다.
   */
  dragSession: DragSession | null;

  /**
   * 배치 확정 대기 중인 아이템 정보.
   * 사용자가 배치 위치를 확정하기 전의 임시 상태를 나타냅니다.
   */
  pendingPlacement: PlacedItem | null;

  /**
   * 캔버스의 현재 확대/축소 비율.
   * 1.0이 기본 크기이며, 모든 좌표 계산에 영향을 줍니다.
   */
  scale: number;
}

/**
 * 사용자 인벤토리에서 사용하는 아이템 정보.
 *
 * 기본 아이템 정보에 인벤토리 특화 데이터(수량, 장착 상태 등)를 추가한 형태입니다.
 */
export interface DecoInventoryItem extends Item {
  /**
   * 인벤토리에서의 고유 인스턴스 ID.
   * 동일한 아이템이라도 인벤토리 슬롯별로 다른 ID를 가집니다.
   */
  inventoryItemId: number;

  /**
   * 인벤토리에 보유한 해당 아이템의 개수.
   * 배치 시 이 수량이 차감됩니다.
   */
  quantity: number;

  /**
   * 현재 방에 배치되어 사용 중인지 여부.
   * true인 경우 추가 배치가 제한될 수 있습니다.
   */
  equipped?: boolean;
}

/**
 * 서버에서 인벤토리와 배치 상태를 함께 조회할 때의 응답 형식.
 *
 * 방 입장 시 또는 전체 상태 동기화 시에 사용됩니다.
 */
export interface DecoInventoryResponse {
  /**
   * 사용자가 보유한 데코 아이템 목록.
   */
  items: DecoInventoryItem[];

  /**
   * 현재 방에 배치되어 있는 아이템 목록.
   */
  placedItems: DecoPlacedItemResponse[];
}

/**
 * 서버에서 반환하는 배치 아이템의 최소 정보.
 *
 * 클라이언트 측 PlacedItem 인터페이스보다 간소화된 형태로,
 * 서버가 관리하는 핵심 데이터만 포함합니다.
 */
export interface DecoPlacedItemResponse {
  /**
   * 인벤토리 인스턴스 ID (아이템 소모 추적용).
   */
  inventoryItemId: number;

  /**
   * 아이템 마스터 데이터 ID.
   */
  itemId: number;

  /**
   * 그리드 X 좌표 (열).
   */
  positionX: number;

  /**
   * 그리드 Y 좌표 (행).
   */
  positionY: number;

  /**
   * 아이템 가로 크기 (셀 개수).
   */
  xLength: number;

  /**
   * 아이템 세로 크기 (셀 개수).
   */
  yLength: number;

  /**
   * 회전 각도 (도 단위).
   */
  rotation?: 0 | 90 | 180 | 270;

  /**
   * 배치된 레이어 ('floor', 'leftWall', 'rightWall' 등).
   */
  layer?: string;

  /**
   * X축 렌더링 오프셋 (픽셀).
   */
  offsetX?: number;

  /**
   * Y축 렌더링 오프셋 (픽셀).
   */
  offsetY?: number;
}

/**
 * 현재 방 상태를 서버에 저장하기 위한 요청 데이터.
 *
 * 클라이언트의 draftItems를 서버가 이해할 수 있는 형태로 변환한 페이로드입니다.
 */
export interface SaveDecoRequest {
  /**
   * 저장할 배치 아이템 목록.
   * 각 아이템은 서버가 필요로 하는 최소 정보만 포함합니다.
   */
  placedItems: {
    /**
     * 인벤토리 소모를 위한 인스턴스 ID (선택적).
     */
    inventoryItemId?: number;

    /**
     * 아이템 마스터 데이터 ID.
     */
    itemId: number;

    /**
     * 그리드 X 좌표.
     */
    positionX: number;

    /**
     * 그리드 Y 좌표.
     */
    positionY: number;

    /**
     * 아이템 가로 크기.
     */
    xLength: number;

    /**
     * 아이템 세로 크기.
     */
    yLength: number;

    /**
     * 회전 각도 (선택적).
     */
    rotation?: 0 | 90 | 180 | 270;

    /**
     * 배치 레이어 (선택적).
     */
    layer?: string;
  }[];
}

/**
 * 방 데코 저장 요청에 대한 서버 응답.
 *
 * 현재는 성공/실패 여부만 반환하지만, 향후 상세 오류 정보나
 * 갱신된 상태 정보를 추가할 수 있습니다.
 */
export interface SaveDecoResponse {
  /**
   * 저장 성공 여부.
   * false인 경우 클라이언트는 적절한 오류 처리를 수행해야 합니다.
   */
  success: boolean;
}
