import { forwardRef } from 'react';
import { ScrollArea } from '@/shared/components/ui/scroll-area';
import { cn } from '@/lib/utils';

interface PageScrollAreaProps {
  children: React.ReactNode;
  className?: string;
  height?: string;
}

/**
 * 페이지 콘텐츠를 위한 공통 스크롤 영역 컴포넌트
 */
const PageScrollArea = forwardRef<
  React.ElementRef<typeof ScrollArea>,
  PageScrollAreaProps
>(({ children, className, height, ...props }, ref) => {
  return (
    <ScrollArea
      ref={ref}
      className={cn('h-full w-full', className)}
      style={height ? { height } : undefined}
      {...props}
    >
      {children}
    </ScrollArea>
  );
});

PageScrollArea.displayName = 'PageScrollArea';

export { PageScrollArea };
export type { PageScrollAreaProps };
