import { ChevronLeft } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';


// [채은 코드] 적금 설정 변경 관련 
// interface DetailTopBarProps {
//   title: string;
//   backMode?: 'history' | 'entry';
// }

// const DetailTopBar = ({ title, backMode = 'history' }: DetailTopBarProps) => {
//   const navigate = useNavigate();
//   const [searchParams] = useSearchParams();

//   // URL 파라미터에서 from 값을 읽어옴
//   const fromParam = searchParams.get('from');
//   const entryPoint = fromParam ? decodeURIComponent(fromParam) : PAGE_PATH.HOME;

//   const handleBack = () => {
//     if (backMode === 'entry') {
//       // 명시적으로 entryPoint로 이동 (히스토리 조작 없음)
//       navigate(entryPoint);
//     } else {
//       navigate(-1);


// [승윤 코드] 계좌 개설하기 관련
const DetailTopBar = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const handleBack = () => {
    // URL 파라미터에서 from 값 확인
    const fromParam = searchParams.get('from');

    if (fromParam === 'home') {
      navigate(PAGE_PATH.HOME);
    } else if (fromParam === 'wallet') {
      navigate(PAGE_PATH.WALLET);
    } else if (fromParam === 'products') {
      navigate(PAGE_PATH.PRODUCTS);
    } else {
      // 기본값: WalletPage로 이동 (하위호환성)
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
