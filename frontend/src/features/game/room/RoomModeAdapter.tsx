import type { ReactNode } from 'react';
import type { RoomRenderable } from './RoomBase';
import RoomBase, {
  type RoomBaseProps,
  type RoomMode,
  type RoomRenderContext,
} from './RoomBase';

/** RoomBase에 모드별 오버레이를 주입할 때 사용하는 속성. */
interface RoomModeAdapterProps
  extends Omit<RoomBaseProps, 'mode' | 'children'> {
  mode?: RoomMode;
  children?: RoomRenderable;
  previewOverlay?: (context: RoomRenderContext) => ReactNode;
  editOverlay?: (context: RoomRenderContext) => ReactNode;
}

/**
 * RoomBase 위에 모드(preview/edit)에 따른 오버레이 콘텐츠를 간단히 배치할 수 있도록 도와주는 어댑터 컴포넌트.
 */
const RoomModeAdapter = ({
  mode = 'readonly',
  children,
  previewOverlay,
  editOverlay,
  ...rest
}: RoomModeAdapterProps) => {
  const resolveChildren = (context: RoomRenderContext) => {
    const baseContent =
      typeof children === 'function' ? children(context) : (children ?? null);

    if (mode === 'preview' && previewOverlay) {
      return (
        <>
          {baseContent}
          {previewOverlay(context)}
        </>
      );
    }

    if (mode === 'edit' && editOverlay) {
      return (
        <>
          {baseContent}
          {editOverlay(context)}
        </>
      );
    }

    return baseContent;
  };

  return (
    <RoomBase
      {...rest}
      mode={mode}
      children={context => resolveChildren(context)}
    />
  );
};

export default RoomModeAdapter;
