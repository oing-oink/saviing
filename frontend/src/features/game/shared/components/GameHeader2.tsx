import elevatorBasic from '@/assets/game_button/elevatorBasic.png';

/**
 * 게임
 *
 * room 변경 버튼
 */
const GameHeader2 = () => {
  return (
    <div className="flex justify-end px-3">
      <button>
        <img className="w-9" src={elevatorBasic} />
      </button>
    </div>
  );
};

export default GameHeader2;
