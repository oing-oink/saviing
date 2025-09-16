import { useEffect, useRef, useState } from 'react';
import Autoplay from 'embla-carousel-autoplay';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  type CarouselApi,
} from '@/shared/components/ui/carousel';
import mouse from '@/assets/mouse_item.png';

interface PromoCardProps {
  title: string;
  subtitle: string;
  image: string;
}

const promoItems: PromoCardProps[] = [
  {
    title: '다마고치 키우고 적금 혜택 받자!',
    subtitle: '이자율 최대 5%!',
    image: mouse,
  },
  {
    title: '친구 초대하고 보너스 포인트 받자!',
    subtitle: '추천 시 3천원 지급',
    image: mouse,
  },
  {
    title: '이번 달 한정 이벤트 참여하기!',
    subtitle: '추첨을 통해 경품 증정',
    image: mouse,
  },
];

const PromoCarousel = () => {
  const plugin = useRef(Autoplay({ delay: 3000, stopOnInteraction: true }));
  const [api, setApi] = useState<CarouselApi>();
  const [current, setCurrent] = useState(0);

  useEffect(() => {
    if (!api) {
      return;
    }

    setCurrent(api.selectedScrollSnap());

    api.on('select', () => {
      setCurrent(api.selectedScrollSnap());
    });
  }, [api]);

  return (
    <div className="relative w-full">
      <Carousel plugins={[plugin.current]} setApi={setApi} className="w-full">
        <CarouselContent className="-ml-2">
          {promoItems.map((item, index) => (
            <CarouselItem key={index} className="bg-transparent pl-2">
              <div className="flex items-center justify-between rounded-2xl bg-white p-5">
                <div>
                  <p className="text-sm font-bold text-black">{item.title}</p>
                  <p className="text-xs text-gray-600">{item.subtitle}</p>
                </div>
                <img
                  src={item.image}
                  alt="promo"
                  className="h-12 w-12 object-contain"
                />
              </div>
            </CarouselItem>
          ))}
        </CarouselContent>
      </Carousel>

      {/* 독립적인 인디케이터 (카드 위에 떠있음) */}
      <div className="absolute bottom-1 left-1/2 z-10 flex -translate-x-1/2 gap-2 rounded-full bg-white/80 px-3 py-1 shadow-sm backdrop-blur-sm">
        {promoItems.map((_, dotIndex) => (
          <span
            key={dotIndex}
            className={`h-2 w-2 rounded-full transition-colors ${
              current === dotIndex ? 'bg-gray-700' : 'bg-gray-300'
            }`}
          />
        ))}
      </div>
    </div>
  );
};

export default PromoCarousel;
