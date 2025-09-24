import type {
  Item,
  GachaDrawCurrencies,
} from '@/features/game/shop/types/item';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import closeButton from '@/assets/game_button/closeButton.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import { useGachaInfo } from '@/features/game/shop/query/useGachaInfo';

/** 가챠 결과 모달에 필요한 속성. */
interface GachaResultProps {
  item: Item;
  currencies: GachaDrawCurrencies;
  onClose: () => void;
}

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

/** 가챠에서 획득한 아이템 정보를 보여주는 전체 화면 모달. */
const GachaResult = ({ item, onClose }: GachaResultProps) => {
  const { data: gachaInfo } = useGachaInfo();
  const navigate = useNavigate();
  const rarity = item.rarity as keyof typeof RARITY_COLORS;

  return (
    <div className="game fixed inset-0 z-50 flex items-center justify-center bg-white/50">
      <div className="relative">
        <div className="mx-auto -mb-8 w-[55%] text-center text-2xl">
          <span className="inline-block rounded-xl bg-primary px-7 py-2 text-secondary">
            축하한다냥!
          </span>
        </div>
        <div className="mx-4 max-w-md justify-center rounded-4xl bg-secondary p-6 px-1 shadow-lg">
          <div className="mb-4 flex justify-end">
            <button
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700"
            >
              <img src={closeButton} alt="closeButton" className="w-[60%]" />
            </button>
          </div>
          <div className="mx-auto mb-4 flex h-48 w-48 items-center rounded-full bg-white">
            <img
              src={getItemImage(item.itemId)}
              alt={item.itemName}
              className="mx-auto h-32 w-32 object-contain"
            />
          </div>
          <div className="flex flex-col items-center pt-2">
            {/* 등급 표시 - 모달 안쪽으로 이동 */}
            <div className="mb-2">
              <span
                className={`inline-block rounded-xl px-4 py-1 text-sm font-bold ${RARITY_COLORS[rarity]} bg-white shadow-sm`}
              >
                {RARITY_NAMES[rarity]} 등급
              </span>
            </div>

            <h2 className="text-xl font-semibold text-gray-800">
              {item.itemName}
            </h2>
            <p className="px-10 pt-3 pb-2 text-center text-sm leading-relaxed whitespace-pre-line text-gray-600">
              {item.itemDescription}
            </p>

            <div className="flex gap-2">
              <button onClick={() => {
                onClose();
                navigate(PAGE_PATH.GACHA_ROLLING + `?t=${Date.now()}`, { replace: true });
              }}>
                <div className="text-md mt-2 mb-2 rounded-2xl border-3 border-level-06 bg-store-bg p-2 px-4 text-red-300">
                  <p>
                    {gachaInfo?.gachaInfo.drawPrice.coin ?? 500} 코인에 한 번 더
                  </p>
                </div>
              </button>
              <button onClick={() => {
                onClose();
                navigate(PAGE_PATH.DECO);
              }}>
                <div className="text-md mt-2 mb-2 rounded-2xl border-3 border-gray-300 bg-gray-100 p-2 px-6 text-gray-600">
                  내 인벤토리 보기
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GachaResult;
