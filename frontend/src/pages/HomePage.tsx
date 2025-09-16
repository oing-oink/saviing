import { Button } from '@/shared/components/ui/button';
import { useNavigate } from 'react-router-dom';

const HomePage = () => {
  const navigate = useNavigate();

  return (
    <div>
      <Button>Home</Button>
      <button
        onClick={() => navigate('/colortest')}
        className="block w-full rounded-lg bg-blue-500 p-2 text-center text-white"
      >
        Go to Color Test Page
      </button>
      <button
        onClick={() => navigate('/game')}
        className="game block w-full rounded-lg bg-primary p-2 text-center text-white"
      >
        Go to Color Game Page
      </button>
    </div>
  );
};

export default HomePage;
