import type { ReactNode } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import type { AccountCreationStep } from '@/features/savings/constants/accountCreationSteps';

interface FunnelStepProps {
  name: AccountCreationStep;
  children: ReactNode;
}

const FunnelStep = ({ name, children }: FunnelStepProps) => {
  const step = useAccountCreationStore(state => state.step);
  if (step !== name) {
    return null;
  }
  return <>{children}</>;
};

export default FunnelStep;
