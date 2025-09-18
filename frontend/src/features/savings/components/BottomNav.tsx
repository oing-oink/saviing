import { useState } from 'react';
import { Home, Wallet, PieChart, User } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const BottomNav = () => {
  const [active, setActive] = useState('Home');

  const menus = [
    { name: 'Home', icon: <Home size={20} />, path: PAGE_PATH.HOME },
    { name: 'Wallet', icon: <Wallet size={20} />, path: PAGE_PATH.WALLET },
    { name: 'Stats', icon: <PieChart size={20} />, path: '#' },
    { name: 'Profile', icon: <User size={20} />, path: '#' },
  ];

  const activeIndex = menus.findIndex(menu => menu.name === active);
  const navigate = useNavigate();

  return (
    <div className="saving fixed bottom-4 left-1/2 flex -translate-x-1/2 items-center gap-2 rounded-full bg-white px-3 py-2 shadow-md">
      {/* 배경 슬라이더 */}
      <div
        className="absolute rounded-full bg-primary transition-all duration-300 ease-in-out"
        style={{
          width: active ? '6rem' : '4rem',
          height: '2.5rem',
          transform: `translateX(${activeIndex * 4.5}rem)`,
          left: '0.75rem',
        }}
      />

      {menus.map(menu => (
        <button
          key={menu.name}
          onClick={() => {
            setActive(menu.name);
            navigate(menu.path);
          }}
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
