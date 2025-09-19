import { useEffect, useRef, useState } from 'react';
import Autoplay from 'embla-carousel-autoplay';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  type CarouselApi,
} from '@/shared/components/ui/carousel';

export interface CarouselSlide {
  id: string;
  title: string;
  subtitle?: string;
  image: string;
}

interface CustomCarouselProps {
  slides: CarouselSlide[];
  autoplay?: boolean;
  autoplayDelay?: number;
  showIndicators?: boolean;
  renderSlide?: (slide: CarouselSlide, index: number) => React.ReactNode;
  className?: string;
}

const CustomCarousel = ({
  slides,
  autoplay = true,
  autoplayDelay = 3000,
  showIndicators = true,
  renderSlide,
  className = '',
}: CustomCarouselProps) => {
  const plugin = useRef(
    autoplay
      ? Autoplay({ delay: autoplayDelay, stopOnInteraction: true })
      : null,
  );
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

  const defaultRenderSlide = (slide: CarouselSlide) => (
    <div className="flex items-center justify-between rounded-2xl bg-white p-5">
      <div>
        <p className="text-sm font-bold text-black">{slide.title}</p>
        {slide.subtitle && (
          <p className="text-xs text-gray-600">{slide.subtitle}</p>
        )}
      </div>
      <img
        src={slide.image}
        alt={slide.title}
        className="h-12 w-12 object-contain"
      />
    </div>
  );

  return (
    <div className={`relative w-full ${className}`}>
      <Carousel
        plugins={plugin.current ? [plugin.current] : []}
        setApi={setApi}
        className="w-full"
        opts={{ loop: true }}
      >
        <CarouselContent className="-ml-2">
          {slides.map((slide, index) => (
            <CarouselItem key={slide.id} className="bg-transparent pl-2">
              {renderSlide
                ? renderSlide(slide, index)
                : defaultRenderSlide(slide)}
            </CarouselItem>
          ))}
        </CarouselContent>
      </Carousel>

      {showIndicators && slides.length > 1 && (
        <div className="absolute bottom-1 left-1/2 z-10 flex -translate-x-1/2 gap-2 rounded-full bg-white/80 px-3 py-1 shadow-sm backdrop-blur-sm">
          {slides.map((_, dotIndex) => (
            <span
              key={dotIndex}
              className={`h-2 w-2 rounded-full transition-colors ${
                current === dotIndex ? 'bg-gray-700' : 'bg-gray-300'
              }`}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default CustomCarousel;
