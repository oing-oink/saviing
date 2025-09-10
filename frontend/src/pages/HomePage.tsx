import { Button } from '@/shared/components/ui/button';
import { Link } from 'react-router-dom';

const HomePage = () => {
  return (
    <div>
      <Button>Home</Button>
      <Link
        to="/colortest"
        className="block w-full rounded-lg bg-blue-500 p-2 text-center text-white"
      >
        Go to Color Test Page
      </Link>
    </div>
  );
};

export default HomePage;
