import { Wallet, Landmark, type LucideIcon } from 'lucide-react';

export interface WalletButtonConfig {
  icon: LucideIcon;
  label: string;
  onClick: () => void;
}

export const useWalletButtons = (): { buttons: WalletButtonConfig[] } => {
  const buttons: WalletButtonConfig[] = [
    {
      icon: Landmark,
      label: '자유적금 개설',
      onClick: () => console.log('자유적금 개설'),
    },
    {
      icon: Wallet,
      label: '입출금계좌 개설',
      onClick: () => console.log('입출금계좌 개설'),
    },
  ];

  return { buttons };
};
