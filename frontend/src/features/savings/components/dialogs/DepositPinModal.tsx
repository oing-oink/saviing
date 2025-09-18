import {
  useCallback,
  useEffect,
  useId,
  useMemo,
  useRef,
  useState,
} from 'react';
import type { CSSProperties, PointerEvent as ReactPointerEvent } from 'react';

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/shared/components/ui/dialog';
import { formatCurrency } from '@/lib/formatters';

interface DepositPinModalProps {
  open: boolean;
  amount: number;
  fromAccountName: string;
  fromAccountNumber: string;
  onConfirm: () => void;
  onClose: () => void;
}

const KEYPAD_KEYS = [
  1,
  2,
  3,
  4,
  5,
  6,
  7,
  8,
  9,
  'delete',
  0,
  'confirm',
] as const;
const MAX_DRAG_OFFSET = 200;
const DRAG_CLOSE_THRESHOLD = 120;

const DepositPinModal = ({
  open,
  amount,
  fromAccountName,
  fromAccountNumber,
  onConfirm,
  onClose,
}: DepositPinModalProps) => {
  const descriptionId = useId();
  const [pinValue, setPinValue] = useState('');
  const [pinError, setPinError] = useState<string | null>(null);
  const [dragOffset, setDragOffset] = useState(0);
  const [isDragging, setIsDragging] = useState(false);
  const dragStartYRef = useRef<number | null>(null);

  const resetDragState = useCallback(() => {
    dragStartYRef.current = null;
    setIsDragging(false);
    setDragOffset(0);
  }, []);

  useEffect(() => {
    if (!open) {
      resetDragState();
    }
  }, [open, resetDragState]);

  const formattedAmount = useMemo(() => {
    return formatCurrency(amount);
  }, [amount]);

  const handleDigitPress = (digit: string) => {
    setPinValue(prev => {
      if (prev.length >= 4) {
        return prev;
      }

      const next = `${prev}${digit}`;
      if (pinError) {
        setPinError(null);
      }

      return next;
    });
  };

  const handleDeleteDigit = () => {
    setPinValue(prev => {
      if (!prev.length) {
        return prev;
      }

      const next = prev.slice(0, -1);
      if (pinError) {
        setPinError(null);
      }
      return next;
    });
  };

  const handleSubmit = () => {
    if (pinValue.length !== 4 || /[^0-9]/.test(pinValue)) {
      setPinError('4자리 숫자 비밀번호를 입력해주세요.');
      return;
    }

    setPinError(null);
    setPinValue('');
    onConfirm();
  };

  const handleDragStart = (event: ReactPointerEvent<HTMLDivElement>) => {
    dragStartYRef.current = event.clientY;
    setIsDragging(true);
    event.currentTarget.setPointerCapture?.(event.pointerId);
  };

  const handleDragMove = (event: ReactPointerEvent<HTMLDivElement>) => {
    if (dragStartYRef.current === null) {
      return;
    }

    const deltaY = event.clientY - dragStartYRef.current;

    if (deltaY <= 0) {
      setDragOffset(0);
      return;
    }

    setDragOffset(Math.min(deltaY, MAX_DRAG_OFFSET));
  };

  const handleClose = useCallback(() => {
    setPinValue('');
    setPinError(null);
    resetDragState();
    onClose();
  }, [resetDragState, onClose]);

  const handleDragEnd = (event: ReactPointerEvent<HTMLDivElement>) => {
    if (dragStartYRef.current === null) {
      resetDragState();
      return;
    }

    const deltaY = event.clientY - dragStartYRef.current;

    if (event.currentTarget.hasPointerCapture?.(event.pointerId)) {
      event.currentTarget.releasePointerCapture(event.pointerId);
    }

    if (deltaY > DRAG_CLOSE_THRESHOLD) {
      handleClose();
      return;
    }

    resetDragState();
  };

  const sheetBodyStyle: CSSProperties = {
    transform: `translateY(${dragOffset}px)`,
    transition: isDragging ? 'none' : 'transform 200ms ease-out',
    willChange: 'transform',
  };

  const renderKey = (key: (typeof KEYPAD_KEYS)[number]) => {
    if (key === 'delete') {
      return (
        <button
          key="keypad-delete"
          type="button"
          className="flex h-12 items-center justify-center rounded-3xl bg-muted text-lg font-medium text-muted-foreground"
          onClick={handleDeleteDigit}
        >
          삭제
        </button>
      );
    }

    if (key === 'confirm') {
      return (
        <button
          key="keypad-confirm"
          type="button"
          disabled={pinValue.length !== 4}
          className="flex h-12 items-center justify-center rounded-3xl bg-primary text-lg font-semibold text-primary-foreground disabled:cursor-not-allowed disabled:opacity-50"
          onClick={handleSubmit}
        >
          확인
        </button>
      );
    }

    return (
      <button
        key={`keypad-${key}`}
        type="button"
        className="flex h-12 items-center justify-center rounded-3xl bg-card text-2xl font-semibold text-foreground shadow-sm"
        onClick={() => handleDigitPress(String(key))}
      >
        {key}
      </button>
    );
  };

  return (
    <Dialog
      open={open}
      onOpenChange={nextOpen => {
        if (!nextOpen) {
          handleClose();
        }
      }}
    >
      <DialogContent
        aria-describedby={descriptionId}
        showCloseButton={false}
        className="fixed bottom-0 left-1/2 w-full max-w-md translate-x-[-50%] translate-y-0 rounded-t-3xl border border-border/60 px-6 pt-6 pb-8"
        style={{
          maxHeight: '90dvh',
          overflowY: 'auto',
          paddingBottom: 'calc(2rem + env(safe-area-inset-bottom, 0px))',
        }}
      >
        <div style={sheetBodyStyle} className="space-y-6">
          <div
            role="presentation"
            className={`flex items-center justify-center ${isDragging ? 'cursor-grabbing' : 'cursor-grab'}`}
            style={{ touchAction: 'none' }}
            onPointerDown={handleDragStart}
            onPointerMove={handleDragMove}
            onPointerUp={handleDragEnd}
            onPointerCancel={handleDragEnd}
          >
            <span
              aria-hidden="true"
              className="h-1.5 w-12 rounded-full bg-muted-foreground/50"
            />
          </div>

          <DialogHeader className="gap-2 text-left">
            <DialogTitle className="text-xl font-semibold text-foreground">
              간편 비밀번호 입력
            </DialogTitle>
            <DialogDescription
              id={descriptionId}
              className="text-sm text-muted-foreground"
            >
              {fromAccountName} ({fromAccountNumber})에서 {formattedAmount}원을
              이체하려면 비밀번호 4자리를 입력하세요.
            </DialogDescription>
          </DialogHeader>

          <div className="flex flex-col items-center gap-3">
            <div className="flex justify-center gap-2.5">
              {Array.from({ length: 4 }).map((_, index) => (
                <span
                  key={`pin-dot-${index}`}
                  className={`h-3 w-3 rounded-full border border-border/70 ${
                    index < pinValue.length
                      ? 'border-primary bg-primary'
                      : 'bg-transparent'
                  }`}
                />
              ))}
            </div>
            {pinError ? (
              <p className="text-center text-xs text-destructive">{pinError}</p>
            ) : null}
          </div>

          <div className="grid grid-cols-3 gap-3 pb-2">
            {KEYPAD_KEYS.map(renderKey)}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default DepositPinModal;
