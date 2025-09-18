import { Wallet, Landmark, type LucideIcon } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';

export interface WalletButtonConfig {
  icon: LucideIcon;
  label: string;
  onClick: () => void;
}

export const useWalletButtons = (): { buttons: WalletButtonConfig[] } => {
  const navigate = useNavigate();
  const buttons: WalletButtonConfig[] = [
    {
      icon: Landmark,
      label: '자유적금 개설',
      onClick: () =>
        navigate(`${PAGE_PATH.ACCOUNT_CREATION}/start?type=${ACCOUNT_TYPES.SAVINGS}`),
    },
    {
      icon: Wallet,
      label: '입출금계좌 개설',
      onClick: () =>
        navigate(`${PAGE_PATH.ACCOUNT_CREATION}/start?type=${ACCOUNT_TYPES.CHECKING}`),
    },
  ];

  return { buttons };
};
