import { Copy } from 'lucide-react';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import { createAccountDetailPath } from '@/shared/constants/path';
import type { SavingsAccountData } from '@/features/savings/types/savingsTypes';

interface CheckingAccountWalletCardProps {
  account: SavingsAccountData;
}

const CheckingAccountWalletCard = ({
  account,
}: CheckingAccountWalletCardProps) => {
  const navigate = useNavigate();

  // 계좌 관리 페이지로 이동
  const handleAccountManagement = () => {
    if (account?.accountId) {
      navigate(createAccountDetailPath(account.accountId));
    }
  };
  return (
    <div className="w-full max-w-md rounded-2xl bg-white font-pretendard shadow">
      <div className="mb-3 flex items-start gap-3 p-6 pb-0">
        <div className="flex-1">
          <div className="flex items-center justify-between">
            <p className="text-2xl font-bold text-primary">
              {account.balance.toLocaleString()}원
            </p>
            <p className="text-md text-gray-500">
              {account.product.productName}
            </p>
          </div>
          <div className="flex items-center gap-1 pt-1 text-gray-400">
            <p className="text-sm">{account.accountNumber}</p>
            <CopyToClipboard
              text={account.accountNumber}
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
      <div className="flex justify-center pt-1">
        <div className="w-6/7 border-t border-gray-200 pt-2" />
      </div>
      <button
        onClick={handleAccountManagement}
        className="mb-3 w-full py-2 text-center font-bold text-primary"
      >
        계좌 관리
      </button>
    </div>
  );
};

export default CheckingAccountWalletCard;
