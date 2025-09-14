import roomImage from '@/assets/room.png';

const Room = () => {
  return (
    <div>
      <img src={roomImage} alt="room" className="block h-auto w-full px-4" />
    </div>
  );
};

export default Room;
