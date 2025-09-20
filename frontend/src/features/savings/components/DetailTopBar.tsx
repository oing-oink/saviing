import { ChevronLeft } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

interface DetailTopBarProps {
  title: string;
}

const DetailTopBar = ({ title }: DetailTopBarProps) => {
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
    <header className="sticky top-0 z-50 flex w-full items-center bg-white px-4 py-4">
      <button
        onClick={handleBack}
        className="flex items-center justify-center rounded-full p-2 text-gray-600 transition-colors"
      >
        <ChevronLeft className="h-6 w-6" />
      </button>
      <h1 className="ml-2 text-xl font-semibold text-gray-800">{title}</h1>
    </header>
  );
};

export default DetailTopBar;
