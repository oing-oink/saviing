import { useEffect } from 'react';
import { useSearchParams, useLocation, useNavigate } from 'react-router-dom';
import {
  StartStep,
  AccountTypeStep,
  UserInfoStep,
  AuthStep,
  TermsStep,
  SetConditionStep,
  ConfirmStep,
  CompleteStep,
} from '@/features/savings/components/funnelSteps';
import FunnelProgressBar from '@/features/savings/components/FunnelProgressBar';
import FunnelLayout from '@/features/savings/layouts/FunnelLayout';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import {
  ACCOUNT_TYPES,
  type AccountType,
} from '@/features/savings/constants/accountTypes';
import { ACCOUNT_CREATION_STEPS_PATH } from '@/shared/constants/path';

const AccountCreationFunnel = () => {
  const [searchParams] = useSearchParams();
  const location = useLocation();
  const navigate = useNavigate();
  const { setForm } = useAccountCreationStore();
  const { currentStepFromUrl } = useStepProgress();

  // URL 초기화 처리
  useEffect(() => {
    if (location.pathname === '/account-creation') {
      navigate(ACCOUNT_CREATION_STEPS_PATH.START, { replace: true });
      return;
    }

    // 타입 파라미터 처리 (form에만 저장, 화면은 START에서 시작)
    const typeParam = searchParams.get('type');
    const isValidAccountType = (value: string | null): value is AccountType => {
      return (
        value !== null &&
        Object.values(ACCOUNT_TYPES).includes(value as AccountType)
      );
    };

    if (isValidAccountType(typeParam) && location.pathname.includes('/start')) {
      setForm({ productType: typeParam });
    }
  }, [location.pathname, searchParams, navigate, setForm]);

  // 현재 단계에 따라 컴포넌트 렌더링
  const renderCurrentStep = () => {
    switch (currentStepFromUrl) {
      case 'START':
        return <StartStep />;
      case 'PRODUCT_TYPE':
        return <AccountTypeStep />;
      case 'USER_INFO':
        return <UserInfoStep />;
      case 'AUTH':
        return <AuthStep />;
      case 'TERMS':
        return <TermsStep />;
      case 'SET_CONDITION':
        return <SetConditionStep />;
      case 'CONFIRM':
        return <ConfirmStep />;
      case 'COMPLETE':
        return <CompleteStep />;
      default:
        return <StartStep />;
    }
  };

  return (
    <div className="saving min-h-screen bg-gray-50">
      <FunnelProgressBar />
      <FunnelLayout>{renderCurrentStep()}</FunnelLayout>
    </div>
  );
};

export default AccountCreationFunnel;
