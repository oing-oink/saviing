import { useEffect } from 'react';
import { useSearchParams, useNavigate, useParams } from 'react-router-dom';
import {
  CurrentInfoStep,
  SelectChangeStep,
  NewSettingsStep,
  ImpactReviewStep,
  ConfirmStep,
  CompleteStep,
} from '@/features/savings/components/settingsSteps';
import FunnelProgressBar from '@/features/savings/components/FunnelProgressBar';
import FunnelLayout from '@/features/savings/components/FunnelLayout';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsStore } from '@/features/savings/store/useSavingsStore';
import { PAGE_PATH } from '@/shared/constants/path';

const SavingsSettingsFunnel = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { accountId } = useParams<{ accountId: string }>();
  const { setCurrentAccount } = useSavingsStore();
  const { step, setStep, reset } = useSavingsSettingsStore();

  // 현재 스텝을 URL 파라미터에서 가져오기
  const currentStep = searchParams.get('step') || 'CURRENT_INFO';

  // URL과 store 동기화 및 초기화
  useEffect(() => {
    const stepParam = searchParams.get('step');

    // accountId가 없으면 적금 목록으로 리다이렉트
    if (!accountId) {
      navigate(PAGE_PATH.SAVINGS, { replace: true });
      return;
    }

    // 현재 계좌 ID를 전역 상태에 설정
    setCurrentAccount(Number(accountId));

    // step 파라미터가 없으면 CURRENT_INFO로 설정
    if (!stepParam) {
      const params = new URLSearchParams();
      params.set('step', 'CURRENT_INFO');
      navigate(`${PAGE_PATH.SAVINGS_SETTINGS_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`, {
        replace: true,
      });
      return;
    }

    // 유효한 스텝인지 확인
    const validSteps = ['CURRENT_INFO', 'SELECT_CHANGE', 'NEW_SETTINGS', 'IMPACT_REVIEW', 'CONFIRM', 'COMPLETE'];
    if (validSteps.includes(stepParam)) {
      setStep(stepParam as any);
    } else {
      // 유효하지 않은 스텝이면 첫 번째 스텝으로 리다이렉트
      const params = new URLSearchParams();
      params.set('step', 'CURRENT_INFO');
      navigate(`${PAGE_PATH.SAVINGS_SETTINGS_WITH_ID.replace(':accountId', accountId)}?${params.toString()}`, {
        replace: true,
      });
    }
  }, [searchParams, navigate, accountId, setCurrentAccount, setStep]);

  // 컴포넌트 언마운트 시 상태 초기화
  useEffect(() => {
    return () => {
      reset();
    };
  }, [reset]);

  // 현재 단계에 따라 컴포넌트 렌더링
  const renderCurrentStep = () => {
    switch (currentStep) {
      case 'CURRENT_INFO':
        return <CurrentInfoStep />;
      case 'SELECT_CHANGE':
        return <SelectChangeStep />;
      case 'NEW_SETTINGS':
        return <NewSettingsStep />;
      case 'IMPACT_REVIEW':
        return <ImpactReviewStep />;
      case 'CONFIRM':
        return <ConfirmStep />;
      case 'COMPLETE':
        return <CompleteStep />;
      default:
        return <CurrentInfoStep />;
    }
  };

  return (
    <div className="saving min-h-screen bg-gray-50">
      <FunnelProgressBar />
      <FunnelLayout>{renderCurrentStep()}</FunnelLayout>
    </div>
  );
};

export default SavingsSettingsFunnel;