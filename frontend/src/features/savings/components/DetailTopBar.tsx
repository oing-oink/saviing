import { ChevronLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface DetailTopBarProps {
  title: string;
}

const DetailTopBar = ({ title }: DetailTopBarProps) => {
  const navigate = useNavigate();

  const handleBack = () => {
    navigate(-1);
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
