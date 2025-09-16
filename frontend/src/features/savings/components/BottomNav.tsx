import { useState } from 'react';
import { Home, Wallet, PieChart, User } from 'lucide-react';

const BottomNav = () => {
  const [active, setActive] = useState('Home');

  const menus = [
    { name: 'Home', icon: <Home size={20} /> },
    { name: 'Wallet', icon: <Wallet size={20} /> },
    { name: 'Stats', icon: <PieChart size={20} /> },
    { name: 'Profile', icon: <User size={20} /> },
  ];

  return (
    <div className="fixed bottom-4 left-1/2 flex -translate-x-1/2 items-center gap-6 rounded-full bg-white px-4 py-2 shadow-md">
      {menus.map(menu => (
        <button
          key={menu.name}
          onClick={() => setActive(menu.name)}
          className={`flex items-center gap-2 rounded-full px-4 py-2 transition-colors ${
            active === menu.name
              ? 'bg-violet-600 text-white'
              : 'text-gray-400 hover:text-violet-600'
          }`}
        >
          {menu.icon}
          {active === menu.name && (
            <span className="text-sm font-medium">{menu.name}</span>
          )}
        </button>
      ))}
    </div>
  );
};

export default BottomNav;
