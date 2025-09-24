import { useEffect } from 'react';
import { useSearchParams, useNavigate, useParams } from 'react-router-dom';
import {
  WarningStep,
  AuthStep,
  ReasonStep,
  ConfirmStep,
  CompleteStep,
} from '@/features/savings/components/terminationSteps';
import FunnelProgressBar from '@/features/savings/components/FunnelProgressBar';
import FunnelLayout from '@/features/savings/layouts/FunnelLayout';
import { useSavingsStore } from '@/features/savings/store/useSavingsStore';
import { PAGE_PATH } from '@/shared/constants/path';

const SavingsTerminationFunnel = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { accountId } = useParams<{ accountId: string }>();
  const { setCurrentAccount } = useSavingsStore();

  const fromParam = searchParams.get('from');
  const currentStep = searchParams.get('step') || 'WARNING';

  useEffect(() => {
    const stepParam = searchParams.get('step');

    if (!accountId) {
      navigate(PAGE_PATH.SAVINGS, { replace: true });
      return;
    }

    setCurrentAccount(Number(accountId));

    if (!stepParam) {
      const params = new URLSearchParams();
      params.set('step', 'WARNING');
      if (fromParam) {
        params.set('from', fromParam);
      }
      navigate(
        `${PAGE_PATH.SAVINGS_TERMINATION_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`,
        {
          replace: true,
        },
      );
      return;
    }

    const validSteps = ['WARNING', 'AUTH', 'REASON', 'CONFIRM', 'COMPLETE'];
    if (!validSteps.includes(stepParam)) {
      const params = new URLSearchParams();
      params.set('step', 'WARNING');
      if (fromParam) {
        params.set('from', fromParam);
      }
      navigate(
        `${PAGE_PATH.SAVINGS_TERMINATION_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`,
        {
          replace: true,
        },
      );
    }
  }, [searchParams, navigate, accountId, setCurrentAccount, fromParam]);

  const renderCurrentStep = () => {
    switch (currentStep) {
      case 'WARNING':
        return <WarningStep />;
      case 'AUTH':
        return <AuthStep />;
      case 'REASON':
        return <ReasonStep />;
      case 'CONFIRM':
        return <ConfirmStep />;
      case 'COMPLETE':
        return <CompleteStep />;
      default:
        return <WarningStep />;
    }
  };

  return (
    <div className="saving min-h-screen bg-gray-50">
      <FunnelProgressBar />
      <FunnelLayout>{renderCurrentStep()}</FunnelLayout>
    </div>
  );
};

export default SavingsTerminationFunnel;
