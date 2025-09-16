import elevatorBasic from '@/assets/game_button/elevatorBasic.png';

/**
 * 엘리베이터 버튼
 *
 * room 변경을 위한 엘리베이터 버튼 컴포넌트
 */
const ElevatorButton = () => {
  return (
    <div className="flex justify-end px-3">
      <button>
        <img className="w-9" src={elevatorBasic} />
      </button>
    </div>
  );
};

export default ElevatorButton;
