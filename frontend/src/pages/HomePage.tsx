import { Button } from '@/shared/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const HomePage = () => {
  const navigate = useNavigate();

  return (
    <div>
      <Button>Home</Button>
      <button
        onClick={() => navigate(PAGE_PATH.COLORTEST)}
        className="block w-full rounded-lg bg-blue-500 p-2 text-center text-white"
      >
        Go to Color Test Page
      </button>
      <button
        onClick={() => navigate(PAGE_PATH.GAME)}
        className="game block w-full rounded-lg bg-primary p-2 text-center text-white"
      >
        Go to Color Game Page
      </button>
    </div>
  );
};

export default HomePage;
