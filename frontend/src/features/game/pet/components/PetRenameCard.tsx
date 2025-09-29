import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/shared/components/ui/card';
import { Button } from '@/shared/components/ui/button';
import { usePetStatusCard } from '@/features/game/pet/hooks/usePetStatusCard';
import { usePetRename } from '@/features/game/pet/query/usePetRename';

interface PetRenameCardProps {
  petId: number;
  onClose?: () => void;
}

const PetRenameCard = ({ petId, onClose }: PetRenameCardProps) => {
  const navigate = useNavigate();
  const { petData, isLoading, error } = usePetStatusCard(petId);
  const renameMutation = usePetRename(petId);

  const [newName, setNewName] = useState('');

  const handleSave = async () => {
    if (!newName.trim()) {
      return;
    }
    await renameMutation.mutateAsync({ name: newName.trim() });
    if (onClose) {
      onClose();
    } else {
      navigate(-1);
    }
  };

  if (isLoading) {
    return (
      <Card className="mx-5 flex min-h-60 items-center justify-center rounded-t-2xl p-4">
        <div className="text-sm text-muted-foreground">불러오는 중...</div>
      </Card>
    );
  }

  if (error || !petData) {
    return (
      <Card className="mx-5 flex min-h-60 items-center justify-center rounded-t-2xl p-4">
        <div className="text-red-500">데이터를 불러올 수 없습니다</div>
      </Card>
    );
  }

  return (
    <Card className="mx-5 h-60 gap-2 overflow-y-auto rounded-t-2xl p-4">
      <div className="mt-1 space-y-2">
        <div className="space-y-1">
          <label className="text-xs text-muted-foreground">현재 이름</label>
          <input
            className="w-full rounded-md border px-3 py-2 text-sm"
            value={petData.name}
            disabled
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs">변경할 이름</label>
          <input
            className="w-full rounded-md border px-3 py-2 text-sm"
            placeholder={petData.name}
            value={newName}
            onChange={e => setNewName(e.target.value)}
          />
        </div>
      </div>

      <div className="mt-3 flex justify-center gap-4">
        <Button
          variant="secondary"
          className="h-10 flex-1"
          onClick={() => (onClose ? onClose() : navigate(-1))}
        >
          취소
        </Button>
        <Button
          className="h-10 flex-1"
          onClick={handleSave}
          disabled={renameMutation.isPending || !newName.trim()}
        >
          {renameMutation.isPending ? '저장 중...' : '저장'}
        </Button>
      </div>
    </Card>
  );
};

export default PetRenameCard;
