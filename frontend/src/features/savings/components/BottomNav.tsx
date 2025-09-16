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

  const activeIndex = menus.findIndex(menu => menu.name === active);

  return (
    <div className="fixed bottom-4 left-1/2 flex -translate-x-1/2 items-center gap-2 rounded-full bg-white px-3 py-2 shadow-md">
      {/* 배경 슬라이더 */}
      <div
        className="absolute rounded-full bg-violet-600 transition-all duration-300 ease-in-out"
        style={{
          width: active ? '96px' : '64px',
          height: '40px',
          transform: `translateX(${activeIndex * (64 + 8)}px)`,
          left: '12px',
        }}
      />

      {menus.map(menu => (
        <button
          key={menu.name}
          onClick={() => setActive(menu.name)}
          className={`relative z-10 flex w-16 items-center justify-center gap-1 rounded-full px-2 py-2 transition-colors ${
            active === menu.name
              ? 'w-24 text-white'
              : 'text-gray-400 hover:text-violet-600'
          }`}
        >
          {menu.icon}
          {active === menu.name && (
            <span className="ml-1 text-sm font-medium opacity-100 transition-opacity delay-700 duration-200">
              {menu.name}
            </span>
          )}
        </button>
      ))}
    </div>
  );
};

export default BottomNav;
