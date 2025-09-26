# API 통신 가이드

이 문서는 프로젝트에서 API 통신을 구현할 때 따라야 할 표준과 패턴을 정의합니다.

## 📋 목차

1. [HTTP 클라이언트 구조](#http-클라이언트-구조)
2. [API 응답 타입 표준](#api-응답-타입-표준)
3. [피처별 API 구조](#피처별-api-구조)
4. [React Query 통합](#react-query-통합)
5. [타입 안전성](#타입-안전성)
6. [실제 구현 예제](#실제-구현-예제)
7. [에러 처리](#에러-처리)
8. [Mock 데이터 시스템](#mock-데이터-시스템)

## 🌐 HTTP 클라이언트 구조

### Axios 클라이언트 설정

```typescript
// src/shared/services/api/axiosClient.ts
import axios from 'axios';
import { onRequest, onResponseError } from './interceptors';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10_000,
  withCredentials: true,
});

// 인터셉터 연결
api.interceptors.request.use(onRequest);
api.interceptors.response.use(res => res, onResponseError);
```

### HTTP 메서드 래퍼

```typescript
// src/shared/services/api/http.ts
export const http = {
  get: <T>(url: string, config?: AxiosRequestConfig) =>
    request<T>({ url, method: 'GET', ...config }),

  post: <T>(url: string, body?: unknown, config?: AxiosRequestConfig) =>
    request<T>({ url, method: 'POST', data: body, ...config }),

  put: <T>(url: string, body?: unknown, config?: AxiosRequestConfig) =>
    request<T>({ url, method: 'PUT', data: body, ...config }),

  delete: <T>(url: string, config?: AxiosRequestConfig) =>
    request<T>({ url, method: 'DELETE', ...config }),
};
```

## 📦 API 응답 타입 표준

### 성공 응답

```typescript
export interface ApiSuccessResponse<T> {
  success: true;
  status: number;
  body?: T;
}
```

### 에러 응답

```typescript
export interface ApiErrorResponse {
  success: false;
  status: number;
  code: string;
  message: string;
  timestamp: string;
  invalidParams?: InvalidParam[];
}

export interface InvalidParam {
  field: string;
  message: string;
  rejectedValue: string;
}
```

### API 에러 클래스

```typescript
export class ApiError extends Error {
  public readonly axiosError: AxiosError;
  public readonly response?: ApiErrorResponse;

  constructor(err: AxiosError) {
    super(err.message);
    this.name = 'ApiError';
    this.axiosError = err;

    if (err.response?.data) {
      this.response = err.response.data as ApiErrorResponse;
    }
  }
}
```

## 🏗️ 피처별 API 구조

### 디렉토리 구조

```
src/features/[feature]/
├── api/
│   └── [feature]Api.ts          # API 함수들
├── data/
│   └── mock[Feature]Api.ts      # Mock 데이터
├── query/
│   ├── [feature]Keys.ts         # Query Key 팩토리
│   └── use[Feature]Query.ts     # React Query 훅
└── types/
    └── [feature]Types.ts        # 타입 정의
```

### API 함수 작성 패턴

```typescript
// src/features/savings/api/savingsApi.ts
import { http } from '@/shared/services/api/http';
import type { SavingsAccountData } from '@/features/savings/types/savingsTypes';
import { mockGetSavingsAccount } from '@/features/savings/data/mockSavingsApi';

const USE_MOCK = import.meta.env.MODE === 'development';

/**
 * 적금 계좌 상세 정보 조회
 *
 * @param accountId - 조회할 적금 계좌의 고유 식별자
 * @returns 적금 계좌의 상세 정보
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getSavingsAccount = async (
  accountId: string,
): Promise<SavingsAccountData> => {
  if (USE_MOCK) {
    const mockResponse = await mockGetSavingsAccount(accountId);
    return mockResponse.body!;
  }

  const response = await http.get<SavingsAccountData>(
    `/v1/savings/accounts/${accountId}`,
  );
  return response.body!;
};
```

### Game Shop - 아이템 구매 요청

- **엔드포인트**: `POST /v1/game/shop/purchase`
- **설명**: 캐릭터가 상점에서 아이템을 구매할 때 사용합니다.
- **요청 본문**:

```json
{
  "characterId": 1001,
  "itemId": 501,
  "paymentMethod": "COIN",
  "count": 3
}
```

- `count`는 선택 값이며, 전달하지 않으면 서버에서 기본값(1)으로 처리됩니다.

```typescript
// src/features/game/shop/types/item.ts
export interface PurchaseRequest {
  characterId: number;
  itemId: number;
  paymentMethod: PaymentMethod;
  /** 구매 수량 (미지정 시 1로 처리). */
  count?: number;
}
```

### Game Pet - 펫 상호작용

- **엔드포인트**: `POST /v1/game/pets/{petId}/interaction`
- **설명**: 지정한 펫에게 사료를 주거나 놀아줄 때 사용합니다.
- **요청 본문**:

```json
{
  "type": "FOOD"
}
```

- `type` 값은 `FOOD` 또는 `TOY` 중 하나입니다.
- **응답 본문**:

```json
{
  "pet": {
    "petId": 406,
    "itemId": 1001,
    "name": "갈색 냥이",
    "level": 1,
    "exp": 0,
    "requiredExp": 100,
    "affection": 55,
    "maxAffection": 100,
    "energy": 100,
    "maxEnergy": 100
  },
  "consumption": [
    {
      "inventoryItemId": 370,
      "itemId": 96,
      "type": "FOOD",
      "remaining": 21
    }
  ]
}
```

```typescript
// src/features/game/pet/types/petTypes.ts
export type PetInteractionType = 'FEED' | 'PLAY';

export interface PetInteractionRequest {
  type: PetInteractionType;
}

export interface ConsumptionItem {
  inventoryItemId: number;
  itemId: number;
  type: 'FOOD' | 'TOY';
  remaining: number;
}

export interface PetInteractionResponse {
  pet: PetData;
  consumption: ConsumptionItem[];
}
```

## 🔄 React Query 통합

### Query Key 팩토리

```typescript
// src/features/savings/query/savingsKeys.ts
export const savingsKeys = {
  all: ['savings'] as const,
  lists: () => [...savingsKeys.all, 'list'] as const,
  list: (filters: string) => [...savingsKeys.lists(), { filters }] as const,
  details: () => [...savingsKeys.all, 'detail'] as const,
  detail: (id: string) => [...savingsKeys.details(), id] as const,
};
```

### 커스텀 Query 훅

```typescript
// src/features/savings/query/useSavingsQuery.ts
import { useQuery } from '@tanstack/react-query';
import { getSavingsAccount } from '@/features/savings/api/savingsApi';
import { savingsKeys } from '@/features/savings/query/savingsKeys';

export const useSavingsAccount = (accountId: string) => {
  return useQuery({
    queryKey: savingsKeys.detail(accountId),
    queryFn: () => getSavingsAccount(accountId),
    staleTime: 1000 * 60, // 1분
    gcTime: 1000 * 60 * 5, // 5분
  });
};
```

### 데이터 변환 레이어

```typescript
// 데이터 가공이 필요한 경우 별도 훅으로 분리
export const useSavingsDisplayData = (accountId: string) => {
  const query = useSavingsAccount(accountId);

  const displayData: SavingsDisplayData | undefined = query.data
    ? {
        accountNumber: query.data.accountNumber,
        productName: query.data.product.productName,
        interestRate: (query.data.baseRate + query.data.bonusRate) / 100.0,
        targetAmount: query.data.savings.targetAmount,
        maturityDate: query.data.savings.maturityDate,
        balance: query.data.balance,
      }
    : undefined;

  return {
    ...query,
    data: displayData,
  };
};
```

## 🔒 타입 안전성

### 요청/응답 타입 정의

```typescript
// src/features/savings/types/savingsTypes.ts

// 서버 응답 타입
export interface SavingsAccountData {
  accountId: number;
  accountNumber: string;
  customerId: number;
  product: ProductInfo;
  balance: number;
  // ... 기타 필드
}

// UI에서 사용할 변환된 타입
export interface SavingsDisplayData {
  accountNumber: string;
  productName: string;
  interestRate: number;
  targetAmount: number;
  maturityDate: string;
  balance: number;
}
```

### API 함수 타입 정의

```typescript
// 제네릭을 활용한 타입 안전한 API 함수
export const createSavingsAccount = async (
  data: CreateSavingsRequest,
): Promise<SavingsAccountData> => {
  const response = await http.post<SavingsAccountData>(
    '/v1/savings/accounts',
    data,
  );
  return response.body!;
};
```

## 💡 실제 구현 예제

### GET 요청

```typescript
// 목록 조회
export const getSavingsAccounts = async (): Promise<SavingsAccountData[]> => {
  if (USE_MOCK) {
    return mockSavingsAccountList;
  }

  const response = await http.get<SavingsAccountData[]>('/v1/savings/accounts');
  return response.body!;
};

// 상세 조회
export const getSavingsAccount = async (
  accountId: string,
): Promise<SavingsAccountData> => {
  const response = await http.get<SavingsAccountData>(
    `/v1/savings/accounts/${accountId}`,
  );
  return response.body!;
};
```

### POST 요청

```typescript
export const createSavingsAccount = async (
  data: CreateSavingsRequest,
): Promise<SavingsAccountData> => {
  const response = await http.post<SavingsAccountData>(
    '/v1/savings/accounts',
    data,
  );
  return response.body!;
};
```

### PUT 요청

```typescript
export const updateSavingsAccount = async (
  accountId: string,
  data: UpdateSavingsRequest,
): Promise<SavingsAccountData> => {
  const response = await http.put<SavingsAccountData>(
    `/v1/savings/accounts/${accountId}`,
    data,
  );
  return response.body!;
};
```

### DELETE 요청

```typescript
export const deleteSavingsAccount = async (
  accountId: string,
): Promise<void> => {
  await http.delete(`/v1/savings/accounts/${accountId}`);
};
```

## ⚠️ 에러 처리

### 인터셉터를 통한 공통 에러 처리

```typescript
// src/shared/services/api/interceptors.ts
export const onResponseError = (error: AxiosError) => {
  if (error.response?.status === 401) {
    // 인증 만료 시 로그인 페이지로 리다이렉트
    router.navigate(PAGE_PATH.LOGIN);
  }
  return Promise.reject(new ApiError(error));
};
```

### 컴포넌트에서 에러 처리

```typescript
const { data, error, isLoading } = useSavingsAccount(accountId);

if (error) {
  // ApiError 타입으로 에러 정보 접근 가능
  console.error('API 에러:', error.response?.message);
  return <div>데이터를 불러오는 중 오류가 발생했습니다.</div>;
}
```

### 뮤테이션 에러 처리

```typescript
const createAccountMutation = useMutation({
  mutationFn: createSavingsAccount,
  onError: (error: ApiError) => {
    if (error.response?.invalidParams) {
      // 유효성 검증 에러 처리
      error.response.invalidParams.forEach(param => {
        console.error(`${param.field}: ${param.message}`);
      });
    }
  },
});
```

## 🎭 Mock 데이터 시스템

### Mock 데이터 구조

```typescript
// src/features/savings/data/mockSavingsApi.ts
export const mockSavingsAccountData: SavingsAccountData = {
  accountId: 1,
  accountNumber: '11012345678901234',
  customerId: 1001,
  product: {
    productId: 1,
    productName: '자유입출금통장',
    productCode: 'FREE_CHECKING',
    productCategory: 'DEMAND_DEPOSIT',
    description: '언제든지 자유롭게 입출금이 가능한 통장',
  },
  // ... 나머지 필드
};

export const mockGetSavingsAccount = async (
  accountId: string,
): Promise<ApiSuccessResponse<SavingsAccountData>> => {
  // 실제 API 지연 시간 시뮬레이션
  await new Promise(resolve => setTimeout(resolve, 500));

  return {
    success: true,
    status: 200,
    body: mockSavingsAccountData,
  };
};
```

### Mock 사용 제어

```typescript
// 환경별 Mock 사용 설정
const USE_MOCK = import.meta.env.MODE === 'development';

// 또는 API별 세밀한 제어
const USE_MOCK_SAVINGS = import.meta.env.VITE_USE_MOCK_SAVINGS === 'true';
```

## 📝 코딩 컨벤션

### 1. API 함수명

- **조회**: `get[Entity]`, `get[Entity]s`
- **생성**: `create[Entity]`
- **수정**: `update[Entity]`
- **삭제**: `delete[Entity]`

### 2. 파일명

- **API 함수**: `[feature]Api.ts`
- **Mock 데이터**: `mock[Feature]Api.ts`
- **Query 훅**: `use[Feature]Query.ts`
- **Query Keys**: `[feature]Keys.ts`
- **타입**: `[feature]Types.ts`

### 3. JSDoc 주석

```typescript
/**
 * 적금 계좌 상세 정보를 조회하는 API 함수
 *
 * 개발 환경에서는 mock 데이터를, 프로덕션 환경에서는 실제 API를 호출합니다.
 *
 * @param accountId - 조회할 적금 계좌의 고유 식별자
 * @returns 적금 계좌의 상세 정보가 담긴 SavingsAccountData 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
```

## ✅ 체크리스트

새로운 API를 추가할 때 다음 사항들을 확인하세요:

- [ ] 타입 정의 (요청/응답)
- [ ] API 함수 구현
- [ ] Mock 데이터 구현
- [ ] Query Key 팩토리에 추가
- [ ] React Query 훅 구현
- [ ] JSDoc 주석 작성
- [ ] 에러 처리 구현
- [ ] 컴포넌트에서 사용 테스트

---

이 가이드를 따라 일관성 있고 유지보수하기 쉬운 API 통신 코드를 작성하세요.
