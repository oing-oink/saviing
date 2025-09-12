interface CloudProps {
  src: string;
  top: number; // % 단위 (y 위치)
  left: number; // % 단위 (첫 시작 x 위치)
  height: number; // rem 단위
  duration: number; // 초 단위
}

const Cloud = ({ src, top, left, height, duration }: CloudProps) => {
  return (
    <div
      className="absolute flex"
      style={{
        top: `${top}%`,
        left: `${left}%`,
        height: `${height}rem`,
        width: '200%',
        animation: `cloud-scroll ${duration}s linear infinite`,
      }}
    >
      <img src={src} className="h-full w-1/2 object-contain" />
      <img src={src} className="h-full w-1/2 object-contain" />
    </div>
  );
};

export default Cloud;
