import roomImage from '@/assets/room.png';

const Room = () => {
  return (
    <div className="w-full h-full">
      <img src={roomImage} alt="room" className="block h-full w-full object-contain" />
    </div>
  );
};

export default Room;
