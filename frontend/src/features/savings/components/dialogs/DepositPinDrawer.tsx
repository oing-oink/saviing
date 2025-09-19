import { useState } from 'react';
import {
  Drawer,
  DrawerContent,
  DrawerHeader,
  DrawerTitle,
} from '@/shared/components/ui/drawer';

interface DepositPinDrawerProps {
  open: boolean;
  amount: number;
  fromAccountName: string;
  onConfirm: () => void;
  onClose: () => void;
}

const DepositPinDrawer = ({
  open,
  onConfirm,
  onClose,
}: DepositPinDrawerProps) => {
  const [pin, setPin] = useState('');

  const handleKeyPress = (key: string | number) => {
    if (typeof key === 'number' && pin.length < 4) {
      setPin(prev => prev + key);
    } else if (key === 'delete') {
      setPin(prev => prev.slice(0, -1));
    } else if (key === 'confirm' && pin.length === 4) {
      onConfirm();
      setPin('');
    }
  };

  return (
    <Drawer open={open} onOpenChange={isOpen => !isOpen && onClose()}>
      <DrawerContent className="saving mx-auto w-full max-w-md rounded-t-2xl">
        <DrawerHeader>
          <DrawerTitle className="p-2 text-center">간편비밀번호</DrawerTitle>
        </DrawerHeader>

        {/* PIN 도트 */}
        <div className="mb-6 flex justify-center gap-4">
          {[0, 1, 2, 3].map(i => (
            <div
              key={i}
              className={`h-4 w-4 rounded-full ${
                i < pin.length ? 'bg-primary' : 'bg-gray-300'
              }`}
            />
          ))}
        </div>

        {/* 키패드 */}
        <div className="grid grid-cols-3 grid-rows-4 gap-3 p-6">
          {[1, 2, 3, 4, 5, 6, 7, 8, 9].map(num => (
            <button
              key={num}
              className={`h-14 rounded-2xl text-2xl font-semibold transition-colors ${
                pin.length === 4
                  ? 'cursor-not-allowed bg-gray-100 text-gray-400'
                  : 'text-gray-900 active:bg-gray-100'
              }`}
              onClick={() => handleKeyPress(num)}
              disabled={pin.length === 4}
            >
              {num}
            </button>
          ))}

          <button
            className="h-14 rounded-2xl text-sm font-medium text-gray-600 active:bg-gray-100"
            onClick={() => handleKeyPress('delete')}
          >
            삭제
          </button>

          <button
            className={`h-14 rounded-2xl text-2xl font-semibold ${
              pin.length === 4
                ? 'cursor-not-allowed bg-gray-100 text-gray-400'
                : 'text-gray-900 active:bg-gray-100'
            }`}
            onClick={() => handleKeyPress(0)}
            disabled={pin.length === 4}
          >
            0
          </button>

          <button
            className={`h-14 rounded-2xl text-sm font-semibold ${
              pin.length === 4
                ? 'bg-primary text-white active:bg-blue-600'
                : 'bg-gray-200 text-gray-400'
            }`}
            onClick={() => handleKeyPress('confirm')}
            disabled={pin.length !== 4}
          >
            확인
          </button>
        </div>
      </DrawerContent>
    </Drawer>
  );
};

export default DepositPinDrawer;
