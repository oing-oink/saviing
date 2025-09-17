import type { LucideIcon } from 'lucide-react';

interface WalletButtonProps {
  icon: LucideIcon;
  label: string;
  onClick?: () => void;
}

const WalletButton = ({ icon: Icon, label, onClick }: WalletButtonProps) => {
  return (
    <button
      onClick={onClick}
      className="saving flex-1 rounded-2xl shadow focus:outline-none"
    >
      <div className="flex flex-col items-center rounded-2xl bg-primary py-5 text-white">
        <Icon className="h-12 w-12 py-2" />
        <p>{label}</p>
      </div>
    </button>
  );
};

export default WalletButton;
