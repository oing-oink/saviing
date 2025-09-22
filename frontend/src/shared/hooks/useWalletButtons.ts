import { Wallet, Landmark, type LucideIcon } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';

/**
 * 지갑 페이지에서 사용할 버튼 설정 인터페이스
 */
export interface WalletButtonConfig {
  /** 버튼에 표시될 아이콘 */
  icon: LucideIcon;
  /** 버튼에 표시될 텍스트 */
  label: string;
  /** 버튼 클릭 시 실행될 함수 */
  onClick: () => void;
}

/**
 * 지갑 페이지의 계좌 개설 버튼들을 생성하는 커스텀 훅
 * 적금 개설과 입출금계좌 개설 버튼을 제공합니다.
 *
 * @returns {object} 버튼 설정 배열을 포함한 객체
 * @returns {WalletButtonConfig[]} buttons - 버튼 설정 배열
 */
export const useWalletButtons = (): { buttons: WalletButtonConfig[] } => {
  const navigate = useNavigate();

  const buttons: WalletButtonConfig[] = [
    {
      icon: Landmark,
      label: '자유적금 개설',
      onClick: () =>
        navigate(
          `${PAGE_PATH.ACCOUNT_CREATION}/start?type=${ACCOUNT_TYPES.SAVINGS}&from=wallet`,
        ),
    },
    {
      icon: Wallet,
      label: '입출금계좌 개설',
      onClick: () =>
        navigate(
          `${PAGE_PATH.ACCOUNT_CREATION}/start?type=${ACCOUNT_TYPES.CHECKING}&from=wallet`,
        ),
    },
  ];

  return { buttons };
};
