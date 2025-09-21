interface ToolbarProps {
  onSaveClick?: () => void;
  onCancelClick?: () => void;
  isSaving?: boolean;
  isDirty?: boolean;
}

const Toolbar = ({
  onSaveClick,
  onCancelClick,
  isSaving = false,
  isDirty = false,
}: ToolbarProps) => {
  return (
    <div className="z-10 flex w-full justify-end gap-2 px-4 py-2">
      <button
        type="button"
        onClick={onCancelClick}
        className="rounded-md bg-gray-100 px-4 py-2 text-sm font-semibold text-gray-700 shadow focus:ring-2 focus:ring-primary focus:outline-none"
        disabled={!isDirty || isSaving}
      >
        취소
      </button>
      <button
        type="button"
        onClick={onSaveClick}
        className="rounded-md bg-primary px-4 py-2 text-sm font-semibold text-white shadow focus:ring-2 focus:ring-primary focus:outline-none disabled:opacity-60"
        disabled={!isDirty || isSaving}
      >
        {isSaving ? '저장 중...' : '저장'}
      </button>
    </div>
  );
};

export default Toolbar;
