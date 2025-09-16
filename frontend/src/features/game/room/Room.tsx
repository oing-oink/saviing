import roomImage from '@/assets/room.png';

const Room = () => {
  return (
    <div className="h-full w-full">
      <img
        src={roomImage}
        alt="room"
        className="block h-full w-full object-contain"
      />
    </div>
  );
};

export default Room;
