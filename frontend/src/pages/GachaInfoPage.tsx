import { useGachaInfo } from '@/features/game/shop/query/useGachaInfo';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import { useItemModal } from '@/features/game/shop/hooks/useItemModal';
import type { GachaItem } from '@/features/game/shop/types/item';
import inventory_square from '@/assets/inventory_square.png';
import ItemDetailModal from '@/features/game/shop/components/ItemDetailModal';
import backButton from '@/assets/game_button/backButton.png';
import { PAGE_PATH } from '@/shared/constants/path';
import { useNavigate } from 'react-router-dom';

/** 등급 순서 정의 */
const RARITY_ORDER = ['LEGENDARY', 'EPIC', 'RARE', 'COMMON'] as const;

/** 등급별 색상 매핑 */
const RARITY_COLORS = {
  LEGENDARY: 'text-yellow-400',
  EPIC: 'text-purple-400',
  RARE: 'text-blue-400',
  COMMON: 'text-gray-400',
} as const;

/** 등급별 한글 이름 매핑 */
const RARITY_NAMES = {
  LEGENDARY: '전설',
  EPIC: '영웅',
  RARE: '희귀',
  COMMON: '일반',
} as const;

const GachaInfoPage = () => {
  const navigate = useNavigate();
  const { data: gachaData, isLoading, isError, error } = useGachaInfo();
  const { selectedItemId, isModalOpen, handleItemClick, handleCloseModal } =
    useItemModal();

  if (isLoading) {
    return (
      <div className="game flex h-screen items-center justify-center font-galmuri">
        <div className="text-sm text-gray-600">가챠 정보를 불러오는 중...</div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="game flex h-screen items-center justify-center font-galmuri">
        <div className="text-sm text-red-500">
          {error?.message ?? '가챠 정보를 불러오지 못했습니다.'}
        </div>
      </div>
    );
  }

  if (!gachaData) {
    return (
      <div className="game flex h-screen items-center justify-center font-galmuri">
        <div className="text-sm text-gray-600">가챠 정보가 없습니다.</div>
      </div>
    );
  }

  const { gachaPoolName, gachaInfo } = gachaData;
  const { drawPrice, dropRates, rewardItemIds } = gachaInfo;

  return (
    <div className="game flex h-screen flex-col bg-primary font-galmuri">
      {/* 헤더 */}
      <div className="flex-shrink-0 border-b bg-secondary px-4 py-3">
        <div className="flex items-center justify-between">
          <button onClick={() => navigate(PAGE_PATH.GACHA)}>
            <img
              className="w-9 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
              src={backButton}
              alt="뒤로가기"
            />
          </button>
          <div className="flex-1">
            <h1 className="text-center text-lg font-bold">
              {gachaPoolName} 정보
            </h1>
            <div className="text-center text-sm text-gray-600">
              뽑기 가격: {drawPrice.coin} 코인
            </div>
          </div>
          <div className="w-9"></div> {/* 중앙 정렬을 위한 공간 */}
        </div>
      </div>

      <div className="flex-1 space-y-6 overflow-y-auto p-4">
        {/* 확률 정보 */}
        <div className="rounded-lg bg-secondary p-4">
          <h2 className="text-md mb-3 font-bold">출현 확률</h2>
          <div className="grid grid-cols-2 gap-2">
            {RARITY_ORDER.map(rarity => (
              <div
                key={rarity}
                className="flex items-center justify-between rounded bg-primary p-2"
              >
                <span
                  className={`text-sm font-semibold ${RARITY_COLORS[rarity]}`}
                >
                  {RARITY_NAMES[rarity]}
                </span>
                <span className="text-sm font-bold">{dropRates[rarity]}%</span>
              </div>
            ))}
          </div>
        </div>

        {/* 등급별 아이템 목록 */}
        {RARITY_ORDER.map(rarity => {
          const items = rewardItemIds[rarity];

          return (
            <div key={rarity} className="rounded-lg bg-secondary p-4">
              <div className="mb-3 flex items-center justify-between">
                <h3 className={`text-md font-bold ${RARITY_COLORS[rarity]}`}>
                  {RARITY_NAMES[rarity]} 등급
                </h3>
                <span className="text-xs text-gray-600">
                  {items.length}개 아이템
                </span>
              </div>

              {/* 아이템 그리드 */}
              <div className="grid grid-cols-3 gap-1">
                {items.map((item: GachaItem) => (
                  <div
                    key={item.itemId}
                    className="relative -mb-6 flex aspect-square items-center justify-center"
                  >
                    <img
                      src={inventory_square}
                      alt="slot"
                      className="absolute inset-0 h-full w-full object-contain"
                    />
                    <button
                      onClick={() =>
                        handleItemClick({
                          itemId: item.itemId,
                          itemName: item.itemName,
                          itemDescription: '',
                          itemType: 'DECORATION',
                          itemCategory: 'DECORATION',
                          rarity: rarity,
                          xLength: 1,
                          yLength: 1,
                          coin: 0,
                          fishCoin: 0,
                          imageUrl: '',
                          isAvailable: true,
                          createdAt: '',
                          updatedAt: '',
                        })
                      }
                      className="relative flex h-[70%] w-[70%] items-center justify-center hover:opacity-80"
                    >
                      <img
                        src={getItemImage(item.itemId)}
                        alt={item.itemName}
                        className="h-[80%] w-[80%] object-contain"
                      />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>

      {/* 아이템 상세 모달 */}
      <ItemDetailModal
        itemId={selectedItemId}
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        hideActions={true}
        hidePrice={true}
      />
    </div>
  );
};

export default GachaInfoPage;
