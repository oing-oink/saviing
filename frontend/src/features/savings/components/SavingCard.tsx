import { Progress } from '@/shared/components/ui/progress';
import saving from '@/assets/saving/saving.png';
import freeSaving from '@/assets/saving/freeSaving.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const SavingCard = () => {
  const target = 1200000;
  const current = 750000;
  const percent = (current / target) * 100;
  const navigate = useNavigate();

  return (
    <div className="saving w-full max-w-md rounded-2xl bg-white p-6 font-pretendard shadow">
      {/* 타이틀 */}
      <h2 className="mb-4 font-medium text-gray-500">내 적금 계좌</h2>
      {/* 자유적금 */}
      <div className="mb-6 flex items-start gap-3">
        <img src={saving} alt="자유적금" className="h-10 w-10" />
        <div className="flex-1">
          <p className="text-xl font-bold text-primary">750,000원</p>
          <p className="text-sm text-gray-500">자유적금 통장</p>
          <div className="mt-3">
            <Progress value={percent} className="h-3 bg-gray-200" />
            <div className="mt-1 flex justify-between text-xs text-gray-400">
              <span>목표 금액 1,200,000원</span>
              <span>연이율 4.5%</span>
            </div>
          </div>
        </div>
      </div>

      {/* 입출금 */}
      <div className="mb-4 flex items-start gap-3">
        <img src={freeSaving} alt="입출금" className="h-10 w-10" />
        <div>
          <p className="text-xl font-bold text-primary">1,650,000원</p>
          <p className="text-sm text-gray-500">입출금 통장</p>
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="flex border-t border-gray-200 pt-3">
        <button className="font-lg flex-1 py-1 text-center font-bold text-primary">
          저축 관리
        </button>
        <button
          className="font-lg flex-1 border-l border-gray-200 py-1 text-center font-bold text-primary"
          onClick={() => navigate(PAGE_PATH.DEPOSIT)}
        >
          입금
        </button>
      </div>
    </div>
  );
};

export default SavingCard;
