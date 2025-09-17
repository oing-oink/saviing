import { Copy } from 'lucide-react';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import toast from 'react-hot-toast';

const CheckingAccountWalletCard = () => {
  return (
    <div className="saving w-full max-w-md rounded-2xl bg-white font-pretendard shadow">
      <div className="mb-3 flex items-start gap-3 p-6 pb-0">
        <div className="flex-1">
          <div className="flex items-center justify-between">
            <p className="text-2xl font-bold text-primary">750,000원</p>
            <p className="text-md text-gray-500">입출금 통장</p>
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
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="flex justify-center pt-3">
        <div className="w-6/7 border-t border-gray-200 pt-2" />
      </div>
      <button className="mb-3 w-full py-2 text-center font-bold text-primary">
        저축 관리
      </button>
    </div>
  );
};

export default CheckingAccountWalletCard;
