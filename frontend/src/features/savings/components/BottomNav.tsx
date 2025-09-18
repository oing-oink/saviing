import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Home, Wallet, PieChart, User } from 'lucide-react';
import { cn } from '@/lib/utils';
import { PAGE_PATH } from '@/shared/constants/path';

const MENUS = [
  { name: 'Home', icon: Home },
  { name: 'Wallet', icon: Wallet },
  { name: 'Stats', icon: PieChart },
  { name: 'Profile', icon: User },
] as const;

const useBottomNav = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // 현재 URL 경로에 따라 활성 메뉴 결정
  const getActiveMenuFromPath = (pathname: string) => {
    if (pathname === '/') return 'Home';
    if (pathname.startsWith('/wallet')) return 'Wallet';
    // 게임, 쇼핑 등 다른 페이지들은 추후 추가 가능
    // if (pathname.startsWith('/game')) return 'Stats';
    // if (pathname.startsWith('/profile')) return 'Profile';
    return 'Home'; // 기본값
  };

  const [active, setActive] = useState(() => getActiveMenuFromPath(location.pathname));

  // URL 변경 시 활성 메뉴 업데이트 (뒤로가기/앞으로가기 대응)
  useEffect(() => {
    const newActive = getActiveMenuFromPath(location.pathname);
    setActive(newActive);
  }, [location.pathname]);

  const activeIndex = MENUS.findIndex(menu => menu.name === active);

  // rem 단위
  const BUTTON_WIDTH = 3;
  const ACTIVE_BUTTON_WIDTH = 6;
  const GAP = 0.5;
  const CONTAINER_PADDING = 0.375;

  const calculateSliderPosition = () => {
    let leftOffset = CONTAINER_PADDING;

    // 활성 버튼까지의 거리 계산
    for (let i = 0; i < activeIndex; i++) {
      const buttonWidth =
        MENUS[i].name === active ? ACTIVE_BUTTON_WIDTH : BUTTON_WIDTH;
      leftOffset += buttonWidth + GAP;
    }

    // 활성 버튼의 중앙점과 슬라이더 위치
    const activeButtonWidth = ACTIVE_BUTTON_WIDTH;
    const buttonCenter = leftOffset + activeButtonWidth / 2;

    return {
      left: buttonCenter - activeButtonWidth / 2,
      width: activeButtonWidth,
    };
  };

  const handleMenuClick = (menuName: string) => {
    setActive(menuName);

    // Home과 Wallet만 실제 페이지 이동
    switch (menuName) {
      case 'Home':
        navigate(PAGE_PATH.HOME);
        break;
      case 'Wallet':
        navigate(PAGE_PATH.WALLET);
        break;
      // Stats, Profile은 상태 변경만 (추후 페이지 구현 시 추가)
    }
  };

  const sliderPosition = calculateSliderPosition();

  return {
    active,
    setActive,
    handleMenuClick,
    sliderPosition,
  };
};

const BottomNav = () => {
  const { active, handleMenuClick, sliderPosition } = useBottomNav();

  return (
    <div className="fixed bottom-4 left-1/2 flex -translate-x-1/2 items-center gap-2 rounded-full border border-white/50 bg-black/15 px-1.5 py-2 shadow-xl backdrop-blur-md">
      {/* 슬라이더 */}
      <div
        className="absolute h-10 rounded-full bg-violet-500/70 transition-all duration-500 ease-out"
        style={{
          width: `${sliderPosition.width}rem`,
          transform: `translateX(${sliderPosition.left}rem)`,
          left: 0,
        }}
      />

      {/* 네비게이션 버튼들 */}
      {MENUS.map(menu => {
        const isActive = active === menu.name;
        const IconComponent = menu.icon;

        return (
          <button
            key={menu.name}
            onClick={() => handleMenuClick(menu.name)}
            className={cn(
              'relative z-10 flex cursor-pointer items-center justify-center rounded-full px-2 py-1.5 text-gray-100 transition-all duration-300 ease-out',
              isActive ? 'w-24' : 'w-12',
            )}
            aria-label={`Navigate to ${menu.name}`}
            role="tab"
            aria-selected={isActive}
          >
            <div className="flex items-center justify-center">
              <div className="flex-shrink-0" aria-hidden="true">
                <IconComponent size={20} />
              </div>
              <span
                className={cn(
                  'ml-1 inline-block overflow-hidden text-sm font-medium whitespace-nowrap transition-all duration-200 ease-out',
                  isActive ? 'w-12' : 'w-0',
                )}
              >
                {menu.name}
              </span>
            </div>
          </button>
        );
      })}
    </div>
  );
};

export default BottomNav;