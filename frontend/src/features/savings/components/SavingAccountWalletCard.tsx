import { Progress } from '@/shared/components/ui/progress';
import { Copy } from 'lucide-react';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import toast from 'react-hot-toast';

const SavingsAccountWalletCard = () => {
  const target = 1200000;
  const current = 750000;
  const percent = (current / target) * 100;

  return (
    <div className="w-full max-w-md rounded-2xl bg-white p-6 font-pretendard shadow">
      <div className="mb-6 flex items-start gap-3">
        <div className="flex-1">
          <div className="flex items-center justify-between">
            <p className="text-2xl font-bold text-primary">750,000원</p>
            <p className="text-md text-gray-500">자유적금 통장</p>
          </div>
          <div className="flex items-center gap-1 text-gray-400">
            <p className="text-sm">1042-001-532620</p>
            <CopyToClipboard
              text="1042-001-532620"
              onCopy={() => {
                toast.dismiss(); // 기존 토스트 닫기
                toast.success('계좌번호가 복사되었습니다!');
              }}
            >
              <button>
                <Copy className="h-3 w-3" />
              </button>
            </CopyToClipboard>
          </div>
          <div className="mt-3">
            <Progress value={percent} className="h-3 bg-gray-200" />
            <div className="mt-2 flex justify-between text-xs text-gray-400">
              <span>목표 금액 1,200,000원</span>
              <span>연이율 4.5%</span>
            </div>
          </div>
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="flex border-t border-gray-200 pt-3">
        <button className="font-lg flex-1 py-1 text-center font-bold text-primary">
          저축 관리
        </button>
        <button className="font-lg flex-1 border-l border-gray-200 py-1 text-center font-bold text-primary">
          입금
        </button>
      </div>
    </div>
  );
};

export default SavingsAccountWalletCard;
