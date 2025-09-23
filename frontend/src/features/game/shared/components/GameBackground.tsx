import Cloud from './Cloud';
import groom1 from '@/assets/game_bg/groom1.png';
import groom2 from '@/assets/game_bg/groom2.png';

/**
 * 게임 배경 컴포넌트
 *
 * 파란 하늘 배경과 움직이는 구름들로 구성된 게임 배경
 */
const GameBackground = () => {
  return (
    <div
      className="relative h-full w-full"
      style={{ backgroundColor: 'oklch(0.9321 0.0493 202.63)' }}
    >
      <Cloud src={groom1} top={10} left={0} height={5} duration={30} />
      <Cloud src={groom2} top={30} left={20} height={8} duration={65} />
      <Cloud src={groom1} top={60} left={50} height={8} duration={40} />
      <Cloud src={groom2} top={80} left={20} height={6} duration={25} />
    </div>
  );
};

export default GameBackground;
