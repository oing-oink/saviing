import PetCard from '@/features/game/pet/components/PetCard';
import PromoCarousel from '@/features/savings/components/PromoCarousel';
import SavingCard from '@/features/savings/components/SavingCard';
import GameBackgroundLayout from '@/features/game/shared/layouts/GameBackgroundLayout';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { Card } from '@/shared/components/ui/card';
import { Loader2 } from 'lucide-react';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

const HomePage = () => {
  const isAuthenticated = useCustomerStore(state => state.isAuthenticated);
  const {
    data: gameEntry,
    isLoading: isGameEntryLoading,
    error: gameEntryError,
  } = useGameEntryQuery({ enabled: isAuthenticated });

  const activePetInventoryId = gameEntry?.pet.petId;

  return (
    <GameBackgroundLayout>
      <div className="px-5 py-4">
        <div className="flex flex-col items-center gap-4">
          <PromoCarousel />
          {!isAuthenticated ? (
            <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-4 font-galmuri shadow">
              <span className="text-sm text-muted-foreground">
                로그인 후 펫 정보를 확인할 수 있어요
              </span>
            </Card>
          ) : typeof activePetInventoryId === 'number' ? (
            <PetCard petId={activePetInventoryId} />
          ) : (
            <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-4 font-galmuri shadow">
              <div className="relative z-10 flex h-full items-center justify-center">
                {isGameEntryLoading ? (
                  <>
                    <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                    <span className="ml-2 text-sm text-muted-foreground">
                      불러오는 중...
                    </span>
                  </>
                ) : (
                  <span className="text-sm text-red-500">
                    게임 정보를 불러올 수 없습니다
                    {gameEntryError ? `: ${gameEntryError.message}` : ''}
                  </span>
                )}
              </div>
            </Card>
          )}
          <SavingCard />
        </div>
      </div>
    </GameBackgroundLayout>
  );
};

export default HomePage;
