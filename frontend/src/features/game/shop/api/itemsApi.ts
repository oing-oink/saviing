import { http } from '@/shared/services/api/http';
import type {
  ItemsResponse,
  Item,
  PurchaseRequest,
  PurchaseResponse,
  InventoryResponse,
  InventoryItem,
  GachaInfoResponse,
  GachaDrawRequest,
  GachaDrawResponse,
} from '@/features/game/shop/types/item';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';

/**
 * 게임 아이템 전체 목록을 조회한다.
 * @param type 아이템 타입 (예: PET, DECORATION)
 * @param category 아이템 카테고리 (예: CAT, LEFT, RIGHT, BOTTOM, TOY, FOOD)
 * @returns 아이템 목록과 총 개수
 */
export const getGameItems = async (
  type: string,
  category: string,
): Promise<ItemsResponse> => {
  const params: Record<string, string | boolean> = {
    type: type,
    category: category,
    sort: 'PRICE',
    order: 'ASC',
    coinType: 'COIN',
  };

  const response = await http.get<ItemsResponse>('/v1/game/items', {
    params,
  });

  if (!response.body) {
    return { items: [], totalCount: 0 };
  }

  return {
    items: response.body.items.map(item => ({
      ...item,
      imageUrl: getItemImage(item.itemId),
    })),
    totalCount: response.body.totalCount,
  };
};

/**
 * 특정 아이템의 상세 정보를 조회한다.
 * @param itemId 아이템 ID
 * @returns 아이템 상세 정보
 */
export const getGameItemDetail = async (itemId: number): Promise<Item> => {
  const response = await http.get<Item>(`/v1/game/items/${itemId}`);

  if (!response.body) {
    throw new Error('아이템 정보를 찾을 수 없습니다.');
  }

  return {
    ...response.body,
    imageUrl: getItemImage(response.body.itemId),
  };
};

/**
 * 아이템 구매를 요청한다.
 * @param purchaseData 구매 요청 데이터
 * @returns 구매 결과 정보
 */
export const purchaseItem = async (
  purchaseData: PurchaseRequest,
): Promise<PurchaseResponse> => {
  const response = await http.post<PurchaseResponse>(
    '/v1/game/shop/purchase',
    purchaseData,
  );

  if (!response.body) {
    throw new Error('구매 처리 중 오류가 발생했습니다.');
  }

  return response.body;
};

/**
 * 인벤토리 아이템을 기존 Item 형식으로 변환한다.
 */
const convertInventoryItemToItem = (inventoryItem: InventoryItem): Item => {
  return {
    itemId: inventoryItem.itemId,
    itemName: inventoryItem.name,
    itemDescription: inventoryItem.description,
    itemType: inventoryItem.type,
    itemCategory: inventoryItem.itemCategory,
    rarity: inventoryItem.rarity,
    xLength: inventoryItem.xLength,
    yLength: inventoryItem.yLength,
    coin: 0, // 인벤토리에서는 가격 정보 없음
    fishCoin: 0, // 인벤토리에서는 가격 정보 없음
    imageUrl: getItemImage(inventoryItem.itemId),
    isAvailable: !inventoryItem.isUsed, // isUsed의 반대
    createdAt: inventoryItem.createdAt,
    updatedAt: inventoryItem.updatedAt,
    inventoryItemId: inventoryItem.inventoryItemId, // 인벤토리 ID 보존
  };
};

/**
 * 캐릭터의 인벤토리 아이템 목록을 조회한다.
 * @param characterId 캐릭터 ID
 * @param type 인벤토리 타입 (PET, ACCESSORY, DECORATION, CONSUMPTION)
 * @param category 아이템 카테고리 (CAT, LEFT, RIGHT, BOTTOM, ROOM_COLOR, TOY, FOOD)
 * @param isUsed 사용 여부 (추후 사용 예정)
 * @returns 인벤토리 아이템 목록과 총 개수
 */
export const getInventoryItems = async (
  characterId: number,
  type?: string,
  category?: string,
  isUsed?: boolean,
): Promise<ItemsResponse> => {
  const params: Record<string, string | boolean> = {
    sort: 'NAME',
    order: 'ASC',
  };

  if (type) {
    params.type = type;
  }
  if (category) {
    params.category = category;
  }
  // isUsed 파라미터가 제공된 경우에만 추가 (추후 사용)
  if (isUsed !== undefined) {
    params.isUsed = isUsed;
  }

  const response = await http.get<InventoryResponse>(
    `/v1/game/inventory/characters/${characterId}`,
    {
      params,
    },
  );

  if (!response.body || !response.body.inventories) {
    return { items: [], totalCount: 0 };
  }

  // 인벤토리 아이템을 기존 Item 형식으로 변환
  const items = response.body.inventories.map(convertInventoryItemToItem);

  return {
    items,
    totalCount: items.length,
  };
};

/**
 * 가챠 정보를 조회한다.
 * @returns 가챠 풀 정보, 확률, 아이템 목록
 */
export const getGachaInfo = async (): Promise<GachaInfoResponse> => {
  const response = await http.get<GachaInfoResponse>(
    '/v1/game/shop/gacha/info',
  );

  if (!response.body) {
    throw new Error('가챠 정보를 찾을 수 없습니다.');
  }

  return response.body;
};

/**
 * 가챠를 뽑는다.
 * @param drawData 가챠 뽑기 요청 데이터
 * @returns 뽑힌 아이템과 잔액 정보
 */
export const drawGacha = async (
  drawData: GachaDrawRequest,
): Promise<GachaDrawResponse> => {
  const response = await http.post<GachaDrawResponse>(
    '/v1/game/shop/gacha/draw',
    drawData,
  );

  if (!response.body) {
    throw new Error('가챠 뽑기 중 오류가 발생했습니다.');
  }

  return {
    ...response.body,
    item: {
      ...response.body.item,
      imageUrl: getItemImage(response.body.item.itemId),
    },
  };
};
