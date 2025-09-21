import type { ReactNode } from 'react';
import type { RoomRenderable } from './RoomBase';
import RoomBase, {
  type RoomBaseProps,
  type RoomMode,
  type RoomRenderContext,
} from './RoomBase';

interface RoomModeAdapterProps
  extends Omit<RoomBaseProps, 'mode' | 'children'> {
  mode?: RoomMode;
  children?: RoomRenderable;
  previewOverlay?: (context: RoomRenderContext) => ReactNode;
  editOverlay?: (context: RoomRenderContext) => ReactNode;
}

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
