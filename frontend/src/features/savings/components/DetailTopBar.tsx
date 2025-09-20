import { ChevronLeft } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const DetailTopBar = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const handleBack = () => {
    // URL 파라미터에서 from 값 확인
    const fromParam = searchParams.get('from');

    if (fromParam === 'products') {
      navigate(PAGE_PATH.PRODUCTS);
    } else {
      // 기본값: WalletPage로 이동
      navigate(PAGE_PATH.WALLET);
    }
  };

  return (
    <div className="border-b bg-white px-6 py-4">
      <div className="flex items-center justify-start">
        <button
          onClick={handleBack}
          className="flex items-center text-gray-600 hover:text-gray-900"
        >
          <ChevronLeft className="h-5 w-5" />
        </button>
      </div>
    </div>
  );
};

export default DetailTopBar;
