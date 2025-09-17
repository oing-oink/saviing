import CheckingAccountWalletCard from '@/features/savings/components/CheckingAccountWalletCard';
import SavingsAccountWalletCard from '@/features/savings/components/SavingAccountWalletCard';
import WalletButton from '@/features/savings/components/WalletButton';
import { useWalletButtons } from '@/shared/hooks/useWalletButtons';
const WalletPage = () => {
  const { buttons } = useWalletButtons();

  return (
    <div className="flex min-h-screen flex-col">
      <div className="flex-1 px-5">
        <div className="flex flex-col items-center gap-4">
          <SavingsAccountWalletCard />
          <CheckingAccountWalletCard />
          <div className="flex w-full gap-4">
            {buttons.map((btn, i) => (
              <WalletButton
                key={i}
                icon={btn.icon}
                label={btn.label}
                onClick={btn.onClick}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default WalletPage;
