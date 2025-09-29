import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
} from '@/shared/components/ui/dialog';
import { Button } from '@/shared/components/ui/button';
import closeButton from '@/assets/game_button/closeButton.png';
import type { ReactNode } from 'react';

interface ButtonProps {
  text: string;
  onClick: () => void;
  className?: string;
}

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  description: string;
  buttons: ButtonProps[];
  children?: ReactNode;
}

export const Modal = ({
  isOpen,
  onClose,
  title,
  description,
  buttons,
  children,
}: ModalProps) => {
  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogPortal>
        <DialogOverlay className="bg-transparent backdrop-blur-sm backdrop-brightness-110" />
        <DialogContent
          showCloseButton={false}
          className="game h-[14rem] border-0 bg-secondary p-4 font-galmuri shadow-xl sm:max-w-lg"
        >
          <DialogClose className="absolute top-4 right-4 ring-offset-background transition-opacity">
            <img
              src={closeButton}
              alt="close"
              className="h-7 w-7 border-none outline-none focus:ring-1 focus:ring-secondary focus:outline-none active:scale-95 active:brightness-90"
            />
          </DialogClose>

          <DialogHeader>
            <DialogTitle className="mt-12 text-center text-2xl tracking-widest">
              {title}
            </DialogTitle>
            <DialogDescription className="text-center">
              {description}
            </DialogDescription>
          </DialogHeader>
          {children}
          <DialogFooter className="flex-row !justify-center gap-4 pb-0">
            {buttons.map((button, index) => (
              <Button
                key={index}
                onClick={button.onClick}
                className={`w-24 text-lg tracking-widest ${button.className}`}
              >
                {button.text}
              </Button>
            ))}
          </DialogFooter>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};
