import { ChevronLeft } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

interface DetailTopBarProps {
  title: string;
  backMode?: 'history' | 'entry';
}

const DetailTopBar = ({ title, backMode = 'history' }: DetailTopBarProps) => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  // URL 파라미터에서 from 값을 읽어옴
  const fromParam = searchParams.get('from');
  const entryPoint = fromParam ? decodeURIComponent(fromParam) : PAGE_PATH.HOME;

  const handleBack = () => {
    if (backMode === 'entry') {
      // 명시적으로 entryPoint로 이동 (히스토리 조작 없음)
      navigate(entryPoint);
    } else {
      navigate(-1);
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
