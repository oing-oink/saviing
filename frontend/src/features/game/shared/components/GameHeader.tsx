import Coin from '@/features/game/shared/components/Coin';
import closeButton from '@/assets/game_button/closeButton.png';
import storeButton from '@/assets/game_button/storeButton.png';
import decoButton from '@/assets/game_button/decoButton2.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import { Button } from '@/shared/components/ui/button';
import InterestRateModal from '@/features/game/shared/components/InterestRateModal';
import SavingsSetupModal from '@/features/game/shared/components/SavingsSetupModal';
import SavingsSelectionModal from '@/features/game/shared/components/SavingsSelectionModal';
import { useState, useEffect } from 'react';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { useGameQuery } from '@/features/game/shared/query/useGameQuery';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import { useQuery } from '@tanstack/react-query';
import { getAllAccounts } from '@/features/savings/api/savingsApi';

/**
 * 게임 메인 페이지 상단바
 *
 * home 버튼 + 코인 + shop 버튼
 */
const GameHeader = () => {
  const navigate = useNavigate();
  const { customerId } = useCustomerStore();
  const { data: gameEntry } = useGameEntryQuery();
  const characterId = gameEntry?.characterId;

  // 캐릭터 게임 데이터 조회 (connectionStatus 확인용)
  const { data: gameData, isLoading: isGameDataLoading } =
    useGameQuery(characterId);

  // 적금 계좌 목록 조회 (버튼 텍스트 결정 및 모달 표시용)
  const { data: accounts, isLoading: isAccountsLoading } = useQuery({
    queryKey: ['accounts', customerId],
    queryFn: () => getAllAccounts(customerId!),
    enabled: Boolean(customerId),
    staleTime: 0, // 항상 최신 데이터를 가져오도록 설정
  });

  // 디버깅: 계좌 데이터 변경 감지
  useEffect(() => {
    if (process.env.NODE_ENV === 'development') {
      console.log('GameHeader - accounts 데이터 변경됨:', {
        accountsCount: accounts?.length || 0,
        isLoading: isAccountsLoading,
        accounts: accounts,
      });
    }
  }, [accounts, isAccountsLoading]);

  // 모달 상태 관리
  const [isInterestModalOpen, setIsInterestModalOpen] = useState(false);
  const [isSavingsSetupModalOpen, setIsSavingsSetupModalOpen] = useState(false);
  const [isSavingsSelectionModalOpen, setIsSavingsSelectionModalOpen] =
    useState(false);

  const handleAccountConnection = () => {
    if (gameData?.connectionStatus === 'CONNECTED') {
      setIsInterestModalOpen(true);
    } else if (gameData?.connectionStatus === 'NO_ACCOUNT') {
      // savings 객체가 있는 계좌들을 확인
      const savingsAccounts = accounts?.filter(
        account => account.savings !== null && account.savings !== undefined,
      );

      if (savingsAccounts && savingsAccounts.length > 0) {
        setIsSavingsSelectionModalOpen(true);
      } else {
        setIsSavingsSetupModalOpen(true);
      }
    }
  };

  const getButtonText = () => {
    if (isGameDataLoading || !gameData) {
      return '확인 중...';
    }

    if (gameData.connectionStatus === 'CONNECTED') {
      return '이자율 상세보기';
    } else if (gameData.connectionStatus === 'NO_ACCOUNT') {
      // savings 객체가 있는 계좌들을 필터링
      const savingsAccounts = accounts?.filter(
        account => account.savings !== null && account.savings !== undefined,
      );

      // 디버깅 로그 추가
      if (process.env.NODE_ENV === 'development') {
        console.log('GameHeader - getButtonText Debug:', {
          connectionStatus: gameData.connectionStatus,
          totalAccounts: accounts?.length || 0,
          savingsAccountsCount: savingsAccounts?.length || 0,
          savingsAccounts: savingsAccounts,
          allAccounts: accounts,
        });
      }

      if (savingsAccounts && savingsAccounts.length > 0) {
        console.log(
          '적금 연결하기 버튼 표시 - 현재 적금 개수:',
          savingsAccounts.length,
        );
        return '적금 연결하기';
      } else {
        console.log(
          '적금 개설하기 버튼 표시 - 현재 적금 개수:',
          savingsAccounts?.length || 0,
        );
        return '적금 개설하기';
      }
    }
    return '연결 확인 중...';
  };

  const getButtonStyle = () => {
    const buttonText = getButtonText();

    if (buttonText === '이자율 상세보기') {
      return 'rounded-2xl border border-level-05 bg-level-05/40 text-base text-white hover:bg-level-05/70 active:scale-95 active:brightness-90';
    } else if (buttonText === '적금 연결하기') {
      return 'rounded-2xl border border-level-02 bg-level-02/40 text-base text-white hover:bg-level-04/70 active:scale-95 active:brightness-90';
    } else {
      return 'rounded-2xl border border-level-01 bg-level-01/40 text-base text-white hover:bg-level-01/70 active:scale-95 active:brightness-90';
    }
  };

  // SavingsSelectionModal에서 내부적으로 연결 처리하므로 콜백 불필요

  return (
    <div>
      <div className="flex h-20 w-full items-center justify-between px-3">
        <button onClick={() => navigate(PAGE_PATH.HOME)}>
          <img
            className="w-9 pt-5 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={closeButton}
          />
        </button>
        <Coin />
        <button onClick={() => navigate(PAGE_PATH.SHOP)}>
          <img
            className="w-9 pt-5 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={storeButton}
          />
        </button>
      </div>
      <div className="flex items-center justify-between px-3">
        <Button
          onClick={handleAccountConnection}
          disabled={isGameDataLoading || !gameData}
          className={getButtonStyle()}
        >
          {getButtonText()}
        </Button>
        <button onClick={() => navigate(PAGE_PATH.DECO)}>
          <img
            className="w-9 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={decoButton}
          />
        </button>
      </div>

      {/* 모달들 */}
      {typeof characterId === 'number' && (
        <InterestRateModal
          isOpen={isInterestModalOpen}
          onClose={() => setIsInterestModalOpen(false)}
          characterId={characterId}
        />
      )}

      <SavingsSetupModal
        isOpen={isSavingsSetupModalOpen}
        onClose={() => setIsSavingsSetupModalOpen(false)}
      />

      <SavingsSelectionModal
        isOpen={isSavingsSelectionModalOpen}
        onClose={() => setIsSavingsSelectionModalOpen(false)}
      />
    </div>
  );
};

export default GameHeader;
