import { useEffect } from 'react';
import { useSearchParams, useNavigate, useLocation } from 'react-router-dom';
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
import {
  ACCOUNT_TYPES,
  type AccountType,
} from '@/features/savings/constants/accountTypes';
import { PAGE_PATH } from '@/shared/constants/path';

const AccountCreationFunnel = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { setForm } = useAccountCreationStore();

  // 현재 스텝을 URL 파라미터에서 가져오기
  const currentStep = searchParams.get('step') || 'START';

  // URL 초기화 및 리다이렉트 처리
  useEffect(() => {
    const stepParam = searchParams.get('step');
    const typeParam = searchParams.get('type');

    // 레거시 URL 형식 처리 (/account-creation/start -> /account-creation?step=START)
    if (location.pathname !== PAGE_PATH.ACCOUNT_CREATION) {
      const pathSegments = location.pathname.split('/');
      const lastSegment = pathSegments[pathSegments.length - 1];

      // 마지막 세그먼트를 스텝으로 변환
      const stepMapping: Record<string, string> = {
        'start': 'START',
        'type': 'PRODUCT_TYPE',
        'user-info': 'USER_INFO',
        'auth': 'AUTH',
        'terms': 'TERMS',
        'condition': 'SET_CONDITION',
        'confirm': 'CONFIRM',
        'complete': 'COMPLETE',
      };

      const newStep = stepMapping[lastSegment] || 'START';
      const params = new URLSearchParams();
      params.set('step', newStep);
      if (typeParam) {
        params.set('type', typeParam);
      }

      navigate(`${PAGE_PATH.ACCOUNT_CREATION}?${params.toString()}`, {
        replace: true,
      });
      return;
    }

    // step 파라미터가 없으면 START로 설정
    if (!stepParam) {
      const params = new URLSearchParams();
      params.set('step', 'START');
      if (typeParam) {
        params.set('type', typeParam);
      }
      navigate(`${PAGE_PATH.ACCOUNT_CREATION}?${params.toString()}`, {
        replace: true,
      });
      return;
    }

    // 타입 파라미터 처리
    const isValidAccountType = (value: string | null): value is AccountType => {
      return (
        value !== null &&
        Object.values(ACCOUNT_TYPES).includes(value as AccountType)
      );
    };

    if (isValidAccountType(typeParam)) {
      setForm({ productType: typeParam });
    }
  }, [searchParams, navigate, setForm, location.pathname]);

  // 현재 단계에 따라 컴포넌트 렌더링
  const renderCurrentStep = () => {
    switch (currentStep) {
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
