import type { ReactNode } from 'react';
import { useGetAccountStore } from '@/features/savings/store/useGetAccountStore';
import type { GetAccountStep } from '@/features/savings/constants/getAccountSteps';

interface FunnelStepProps {
  name: GetAccountStep;
  children: ReactNode;
}

const FunnelStep = ({ name, children }: FunnelStepProps) => {
  const step = useGetAccountStore(state => state.step);
  if (step !== name) {
    return null;
  }
  return <>{children}</>;
};

export default FunnelStep;
