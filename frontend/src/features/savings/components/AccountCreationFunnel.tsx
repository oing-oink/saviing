import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import FunnelStep from '@/features/savings/components/FunnelStep';
import FunnelLayout from '@/features/savings/components/FunnelLayout';
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
import { useGetAccountStore } from '@/features/savings/store/useGetAccountStore';
import {
  ACCOUNT_TYPES,
  type AccountType,
} from '@/features/savings/constants/accountTypes';

const AccountCreationFunnel = () => {
  const [searchParams] = useSearchParams();
  const { setForm, setStep } = useGetAccountStore();

  useEffect(() => {
    const typeParam = searchParams.get('type') as AccountType;
    if (typeParam && Object.values(ACCOUNT_TYPES).includes(typeParam)) {
      setForm({ productType: typeParam });
      setStep('PRODUCT_TYPE'); // 타입이 미리 선택되었으면 해당 단계로
    }
  }, [searchParams, setForm, setStep]);

  return (
    <div className="saving min-h-screen bg-gray-50">
      <FunnelProgressBar />

      <FunnelLayout>
        <FunnelStep name="START">
          <StartStep />
        </FunnelStep>

        <FunnelStep name="PRODUCT_TYPE">
          <AccountTypeStep />
        </FunnelStep>

        <FunnelStep name="USER_INFO">
          <UserInfoStep />
        </FunnelStep>

        <FunnelStep name="AUTH">
          <AuthStep />
        </FunnelStep>

        <FunnelStep name="TERMS">
          <TermsStep />
        </FunnelStep>

        <FunnelStep name="SET_CONDITION">
          <SetConditionStep />
        </FunnelStep>

        <FunnelStep name="CONFIRM">
          <ConfirmStep />
        </FunnelStep>

        <FunnelStep name="COMPLETE">
          <CompleteStep />
        </FunnelStep>
      </FunnelLayout>
    </div>
  );
};

export default AccountCreationFunnel;
