import { useMutation, useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useErrorBoundary } from 'react-error-boundary';
import {
  getAllAccounts,
  createCheckingAccount,
  createSavingsAccount,
} from '@/features/savings/api/savingsApi';
import type {
  CreateCheckingAccountRequest,
  CreateSavingsAccountRequest,
} from '@/features/savings/types/accountCreation';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';
import { PAGE_PATH } from '@/shared/constants/path';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

/**
 * 계좌 생성 관련 로직을 처리하는 커스텀 훅
 *
 * Mutation: 데이터를 변경하는 작업 (POST, PUT, DELETE)을 처리
 * - 사용자가 버튼 클릭 시에만 실행 (수동)
 * - 로딩 상태 자동 관리
 * - 성공/실패 시 각각 다른 처리 가능
 */
export const useAccountCreation = () => {
  const navigate = useNavigate();
  const { form } = useAccountCreationStore();
  const customerId = useCustomerStore(state => state.customerId);
  const { showBoundary } = useErrorBoundary();

  // 기존 계좌 확인 (Query: 데이터 읽어오기)
  const {
    data: existingAccounts,
    isLoading: isCheckingAccounts,
    error: checkAccountsError,
  } = useQuery({
    queryKey: ['existingAccounts', customerId],
    queryFn: () => {
      if (customerId == null) {
        throw new Error('로그인 정보가 없습니다.');
      }
      return getAllAccounts(customerId);
    }, // checkExistingAccounts 대신 getAllAccounts 사용
    staleTime: 1000 * 60 * 5, // 5분간 캐시 유지
    gcTime: 1000 * 60 * 10, // 10분간 메모리 유지
    enabled: customerId != null,
  });

  // API 에러 발생 시 ErrorBoundary로 전달
  useEffect(() => {
    if (checkAccountsError) {
      showBoundary(checkAccountsError);
    }
  }, [checkAccountsError, showBoundary]);

  // 입출금 계좌 생성 (Mutation: 데이터 변경)
  const createCheckingMutation = useMutation({
    // 실제 API 호출 함수
    mutationFn: (request: CreateCheckingAccountRequest) =>
      createCheckingAccount(request),
    // 성공 시 실행될 코드
    onSuccess: () => {
      navigate(`${PAGE_PATH.ACCOUNT_CREATION}?step=COMPLETE`);
    },
    // 실패 시 실행될 코드
    onError: error => {
      console.error('입출금 계좌 생성 실패:', error);
    },
  });

  // 적금 계좌 생성 (Mutation: 데이터 변경)
  const createSavingsMutation = useMutation({
    // 실제 API 호출 함수
    mutationFn: (request: CreateSavingsAccountRequest) =>
      createSavingsAccount(request),
    // 성공 시 실행될 코드
    onSuccess: () => {
      navigate(`${PAGE_PATH.ACCOUNT_CREATION}?step=COMPLETE`);
    },
    // 실패 시 실행될 코드
    onError: error => {
      console.error('적금 계좌 생성 실패:', error);
    },
  });

  /**
   * 적금 계좌 생성 전 입출금 계좌 존재 여부 확인
   * 입출금 계좌가 없으면 입출금 계좌 생성 페이지로 이동
   */
  const validateSavingsAccountCreation = (): boolean => {
    if (!existingAccounts) {
      console.error('기존 계좌 정보를 확인할 수 없습니다.');
      return false;
    }

    // 실제 계좌 목록에서 입출금 계좌가 있는지 확인
    const hasCheckingAccount = existingAccounts.some(
      account =>
        account.product?.productId === 1 && account.status === 'ACTIVE',
    );

    if (!hasCheckingAccount) {
      // 입출금 계좌가 없으면 입출금 계좌 생성 페이지로 리다이렉트
      const params = new URLSearchParams();
      params.set('step', 'START');
      params.set('type', ACCOUNT_TYPES.CHECKING);
      params.set('from', 'savings-validation');
      navigate(`${PAGE_PATH.ACCOUNT_CREATION}?${params.toString()}`);
      return false;
    }

    return true;
  };

  /**
   * 계좌 생성 실행 함수
   * 사용자가 버튼을 클릭했을 때 호출됨
   */
  const createAccount = () => {
    // productType이 없지만 period가 있으면 적금으로 판단
    const isCheckingAccount = form.productType === ACCOUNT_TYPES.CHECKING;
    const isSavingsAccount =
      form.productType === ACCOUNT_TYPES.SAVINGS ||
      ('period' in form && form.period);

    if (!isCheckingAccount && !isSavingsAccount) {
      console.error('계좌 타입이 선택되지 않았습니다.');
      return;
    }

    if (isCheckingAccount) {
      // 입출금 계좌 생성 요청
      if (customerId == null) {
        console.error('로그인 정보가 없어 계좌를 생성할 수 없습니다.');
        return;
      }

      const request: CreateCheckingAccountRequest = {
        customerId,
        productId: 1, // 입출금통장 상품 ID
      };
      createCheckingMutation.mutate(request);
    } else if (isSavingsAccount) {
      // 적금 계좌 생성 전 입출금 계좌 확인
      if (!validateSavingsAccountCreation()) {
        return;
      }

      // 적금 필수 데이터 확인
      if (
        !form.depositAmount ||
        !form.period ||
        !form.autoAccount ||
        !form.transferDate ||
        !form.transferCycle ||
        !form.withdrawAccountId
      ) {
        console.error('적금 계좌 생성에 필요한 정보가 부족합니다.');
        return;
      }

      if (customerId == null) {
        console.error('로그인 정보가 없어 계좌를 생성할 수 없습니다.');
        return;
      }

      // 적금 계좌 생성 요청
      const request: CreateSavingsAccountRequest = {
        customerId,
        productId: 2, // 자유적금 상품 ID
        targetAmount: form.depositAmount * form.period, // 월납입금액 * 기간
        termPeriod: {
          value: form.period,
          unit: 'WEEKS',
        },
        maturityWithdrawalAccount: form.autoAccount,
        autoTransfer: {
          enabled: true,
          cycle: form.transferCycle,
          transferDay: parseInt(form.transferDate),
          amount: form.depositAmount,
          withdrawAccountId: form.withdrawAccountId,
        },
      };
      createSavingsMutation.mutate(request);
    }
  };

  return {
    // 기존 계좌 확인 상태
    existingAccounts,
    isCheckingAccounts,
    checkAccountsError,

    // 계좌 생성 로딩 상태 (mutation.isPending)
    isCreatingChecking: createCheckingMutation.isPending,
    isCreatingSavings: createSavingsMutation.isPending,

    // 계좌 생성 에러 상태 (mutation.error)
    createCheckingError: createCheckingMutation.error,
    createSavingsError: createSavingsMutation.error,

    // 실행 함수들
    createAccount, // 계좌 생성 실행
    validateSavingsAccountCreation, // 적금 생성 가능 여부 확인
  };
};
