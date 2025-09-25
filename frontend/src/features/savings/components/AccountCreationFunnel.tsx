import { useEffect, useCallback } from 'react';
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
import AccountCreationErrorBoundary from '@/features/savings/components/AccountCreationErrorBoundary';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import {
  ACCOUNT_TYPES,
  type AccountType,
} from '@/features/savings/constants/accountTypes';
import {
  SAVINGS_STEPS,
  CHECKING_STEPS,
} from '@/features/savings/constants/accountCreationSteps';
import { PAGE_PATH } from '@/shared/constants/path';

const AccountCreationFunnel = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { setForm, form } = useAccountCreationStore();

  // 현재 스텝을 URL 파라미터에서 가져오기
  const currentStep = searchParams.get('step') || 'START';

  // 이전 스텝 계산 함수
  const getPreviousStep = useCallback(
    (currentStep: string, productType: AccountType | null): string | null => {
      const steps =
        productType === ACCOUNT_TYPES.SAVINGS ? SAVINGS_STEPS : CHECKING_STEPS;
      const currentIndex = steps.findIndex(step => step === currentStep);

      if (currentIndex > 0) {
        return steps[currentIndex - 1];
      }
      return null;
    },
    [],
  );

  // 브라우저 뒤로가기 처리
  useEffect(() => {
    const handlePopState = () => {
      const previousStep = getPreviousStep(currentStep, form.productType);

      if (previousStep) {
        // 이전 스텝으로 이동
        const params = new URLSearchParams(searchParams);
        params.set('step', previousStep);

        navigate(`${PAGE_PATH.ACCOUNT_CREATION}?${params.toString()}`, {
          replace: true,
        });
      } else {
        // 첫 번째 스텝이면 funnel 밖으로 나가기
        const fromParam = searchParams.get('from');
        if (fromParam === 'products') {
          navigate(PAGE_PATH.PRODUCTS);
        } else {
          navigate(PAGE_PATH.WALLET);
        }
      }
    };

    // 히스토리 스택에 현재 상태 추가 (뒤로가기 감지용)
    window.history.pushState(null, '', window.location.href);

    window.addEventListener('popstate', handlePopState);

    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  }, [currentStep, form.productType, searchParams, navigate, getPreviousStep]);

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
      // from 파라미터 보존
      const fromParam = searchParams.get('from');
      if (fromParam) {
        params.set('from', fromParam);
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
      // from 파라미터 보존
      const fromParam = searchParams.get('from');
      if (fromParam) {
        params.set('from', fromParam);
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
    <AccountCreationErrorBoundary>
      <div className="saving min-h-screen bg-gray-50">
        <FunnelProgressBar />
        <FunnelLayout>{renderCurrentStep()}</FunnelLayout>
      </div>
    </AccountCreationErrorBoundary>
  );
};

export default AccountCreationFunnel;
