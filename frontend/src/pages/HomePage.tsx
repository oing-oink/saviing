import PetCard from '@/features/game/pet/components/PetCard';
import PromoCarousel from '@/features/savings/components/PromoCarousel';
import SavingCard from '@/features/savings/components/SavingCard';
import GameBackgroundLayout from '@/features/game/shared/layouts/GameBackgroundLayout';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { useCreateGameCharacter } from '@/features/game/entry/query/useCreateGameCharacter';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import { Card } from '@/shared/components/ui/card';
import { Loader2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import playButton from '@/assets/game_button/playButton.png';
import { ApiError } from '@/shared/types/api';

const HomePage = () => {
  const navigate = useNavigate();
  const { customerId } = useCustomerStore();
  const {
    data: gameEntry,
    isLoading: isGameEntryLoading,
    error: gameEntryError,
  } = useGameEntryQuery();

  const createCharacterMutation = useCreateGameCharacter();

  const activePetInventoryId = gameEntry?.pet?.petId;
  const hasActivePet = typeof activePetInventoryId === 'number';

  const isCharacterNotFound =
    gameEntryError instanceof ApiError &&
    gameEntryError.response?.code === 'CHARACTER_NOT_FOUND';

  const handleCreateCharacter = async () => {
    if (typeof customerId !== 'number') {
      console.error('고객 정보를 찾을 수 없어 캐릭터를 생성할 수 없습니다.');
      return;
    }

    try {
      await createCharacterMutation.mutateAsync(customerId);
      navigate(PAGE_PATH.GAME);
    } catch (error) {
      console.error('게임 캐릭터 생성 실패:', error);
    }
  };

  const handleEnterGame = () => {
    navigate(PAGE_PATH.GAME_ENTER);
  };

  if (isGameEntryLoading) {
    return (
      <GameBackgroundLayout>
        <div className="px-5 py-4">
          <div className="flex flex-col items-center gap-4">
            <PromoCarousel />
            <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-4 font-galmuri shadow">
              <div className="relative z-10 flex h-full items-center justify-center">
                <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                <span className="ml-2 text-sm text-muted-foreground">
                  불러오는 중...
                </span>
              </div>
            </Card>
            <SavingCard />
          </div>
        </div>
      </GameBackgroundLayout>
    );
  }

  if (isCharacterNotFound) {
    return (
      <GameBackgroundLayout>
        <div className="px-5 py-4">
          <div className="flex flex-col items-center gap-4">
            <PromoCarousel />
            <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-6 font-galmuri shadow">
              <span className="text-sm text-muted-foreground">
                아직 활성 캐릭터가 없어요. 새로운 게임을 시작해보세요!
              </span>
              <button
                type="button"
                className="relative z-10"
                onClick={handleCreateCharacter}
                disabled={
                  createCharacterMutation.isPending ||
                  typeof customerId !== 'number'
                }
              >
                {createCharacterMutation.isPending ? (
                  <div className="flex items-center gap-2">
                    <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                    <span className="text-sm text-muted-foreground">
                      생성 중...
                    </span>
                  </div>
                ) : (
                  <img
                    src={playButton}
                    alt="게임 생성"
                    className="h-12 w-auto"
                  />
                )}
              </button>
            </Card>
            <SavingCard />
          </div>
        </div>
      </GameBackgroundLayout>
    );
  }

  if (gameEntryError) {
    return (
      <GameBackgroundLayout>
        <div className="px-5 py-4">
          <div className="flex flex-col items-center gap-4">
            <PromoCarousel />
            <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-4 font-galmuri shadow">
              <div className="relative z-10 flex h-full items-center justify-center">
                <span className="text-sm text-red-500">
                  게임 정보를 불러올 수 없습니다
                  {gameEntryError ? `: ${gameEntryError.message}` : ''}
                </span>
              </div>
            </Card>
            <SavingCard />
          </div>
        </div>
      </GameBackgroundLayout>
    );
  }

  if (!hasActivePet) {
    return (
      <GameBackgroundLayout>
        <div className="px-5 py-4">
          <div className="flex flex-col items-center gap-4">
            <PromoCarousel />
            <Card className="game relative flex w-full flex-col items-center gap-4 overflow-hidden rounded-2xl bg-transparent py-6 font-galmuri shadow">
              <span className="text-sm text-muted-foreground">
                1층 방에 배치된 펫이 없어요.
                <br />
                게임에 입장해 펫을 배치해보세요!
              </span>
              <button
                type="button"
                className="relative z-10"
                onClick={handleEnterGame}
              >
                <img src={playButton} alt="게임 입장" className="h-12 w-auto" />
              </button>
            </Card>
            <SavingCard />
          </div>
        </div>
      </GameBackgroundLayout>
    );
  }

  return (
    <GameBackgroundLayout>
      <div className="px-5 py-4">
        <div className="flex flex-col items-center gap-4">
          <PromoCarousel />
          <PetCard petId={activePetInventoryId} />
          <SavingCard />
        </div>
      </div>
    </GameBackgroundLayout>
  );
};

export default HomePage;
