# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

```bash
# Development
bun dev                    # Start development server

# Building
bun run build             # Build for production (TypeScript + Vite)
bun run preview           # Preview production build

# Code Quality
bun run lint              # ESLint check
bun run format            # Format code with Prettier
bun run format:check      # Check code formatting
```

## Tech Stack

- **React 19** with TypeScript and Vite
- **Dual-purpose app**: Financial savings + interactive pet game
- **State Management**: Zustand (global state) + React Query (server state)
- **Styling**: Tailwind CSS 4.x with custom theming system
- **UI Components**: Radix UI primitives with shadcn/ui patterns
- **HTTP**: Custom Axios wrapper with typed responses

## Architecture Overview

### Feature-Driven Structure

```
src/features/
├── auth/              # Authentication
├── game/              # Game features
│   ├── pet/          # Pet interaction system
│   ├── room/         # Room decoration
│   └── shop/         # In-game store
└── savings/          # Financial features
```

Each feature contains: `api/`, `components/`, `hooks/`, `query/`, `store/`, `types/`

### State Management Strategy

**Global State (Zustand):**

- `useGameStore` - Character data, coins, in-game currency
- `usePetStore` - Pet inventory, behavior, interaction state

**Server State (React Query):**

- Query key factories for cache management (e.g., `petKeys.detail(petId)`)
- 1-minute stale time, 5-minute cache time defaults
- Centralized in `/query` folders per feature

### API Layer Architecture

**HTTP Client (`src/shared/services/api/http.ts`):**

```typescript
// Centralized Axios instance with interceptors
export const http = {
  get: <T>(url: string) => Promise<ApiSuccessResponse<T>>,
  post: <T>(url: string, body?: unknown) => Promise<ApiSuccessResponse<T>>,
  // ...
};
```

**Mock Data System:**

- Toggle-based with `USE_MOCK` flags in API functions
- Located in `/data` folders (e.g., `mockPetApi.ts`)
- Type-safe mock responses matching production contracts

### Component Patterns

**Definition Standard:**

```typescript
// Arrow function components (enforced by ESLint)
const ComponentName = ({ prop1, prop2 }: ComponentProps) => {
  return <div>Component content</div>;
};
```

**UI System:**

- Base components in `src/shared/components/ui/`
- Radix UI primitives with custom styling
- CVA for component variants

**Component Categories:**

```typescript
// Layout Components
src/features/savings/layouts/
├── SavingsLayout.tsx          # Main app layout with bottom nav
├── SavingsDetailLayout.tsx    # Detail pages with top bar
└── FunnelLayout.tsx          # Account creation funnel

// Navigation Components
src/features/savings/components/
├── DetailTopBar.tsx          # Detail page header with back button
├── StepHeader.tsx           # Funnel step header with back navigation
└── FunnelProgressBar.tsx    # Step progress indicator

// Page Components
src/pages/
├── HomePage.tsx             # Main dashboard
├── WalletPage.tsx          # Account overview
├── ProductsPage.tsx        # Product catalog
├── SavingsDetailPage.tsx   # Savings account details
└── AccountDetailPage.tsx   # Checking account details

// Sticky Components (Scroll-aware)
src/features/savings/components/
├── StickyBalance.tsx       # Savings sticky header with progress
├── AccountBalance.tsx      # Account sticky header (balance only)
├── SavingsDetailCard.tsx   # Main savings info card
└── AccountDetailCard.tsx   # Main account info card

// Funnel Components
src/features/savings/components/funnelSteps/
├── StartStep.tsx           # Account type selection
├── UserInfoStep.tsx        # User information input
├── AuthStep.tsx           # Authentication verification
└── CompleteStep.tsx       # Success confirmation

// Shared Components
src/shared/components/
├── layout/
│   └── PageScrollArea.tsx   # Custom scroll container wrapper
├── ui/
│   ├── scroll-area.tsx     # Radix ScrollArea primitive
│   ├── card.tsx           # Card component variants
│   ├── button.tsx         # Button component variants
│   └── progress.tsx       # Progress bar component
└── common/
    └── ProtectedRoute.tsx  # Route authentication wrapper
```

**Scroll System Components:**

```typescript
// PageScrollArea: Custom scroll container
<PageScrollArea className="flex-1">
  {children}
</PageScrollArea>

// useScroll: Scroll state management
const { scrollY, scrollDirection, isAtTop, isAtBottom } = useScroll();

// useScrollReset: Auto-scroll reset
useScrollReset(); // in layout components
```

### Theming System

**Multi-theme architecture:**

- Base theme (light/dark)
- Game theme (pixel-style with pet level colors)
- Savings theme (financial app styling)

**Colors:** OKLCH color space with CSS custom properties **Fonts:** Galmuri (pixel-style for game), Pretendard (UI text)

## Code Conventions

### TypeScript

- Use `interface` for object types
- Prefer `string[]` over `Array<string>`
- Use `import type` for type-only imports
- No `React.FC` - use regular function components
- No explicit `any` - strict mode enforced

### File Organization

- Absolute imports with `@/` alias
- PascalCase for components, camelCase for functions
- Feature co-location principle
- No relative imports except within same folder

### Router Architecture

**Main Router Setup:**

- React Router v7 with `createBrowserRouter`
- Centralized route configuration in `src/app/router/routes.tsx`
- Path constants for type-safe navigation in `src/shared/constants/path.ts`

**Unified Route Structure:**

```typescript
src/app/router/
└── routes.tsx          # Single centralized router configuration
```

**Route Architecture (3-tier system):**

```typescript
// 1. SavingsLayout Routes (Main Navigation)
const savingsLayoutRoutes = [
  { path: '', element: <HomePage /> },        // '/' 경로
  { path: 'wallet', element: <WalletPage /> }, // '/wallet' 경로  
  { path: 'products', element: <ProductsPage /> }, // '/products' 경로
];

// 2. Protected Routes without Layout
const protectedRoutesWithoutLayout = [
  // Game Routes
  { path: PAGE_PATH.GAME, element: <GamePage /> },
  { path: PAGE_PATH.SHOP, element: <ShopPage /> },
  { path: PAGE_PATH.GACHA, element: <GachaPage /> },
  { path: PAGE_PATH.GACHA_ROLLING, element: <GachaRollingPage /> },
  { path: PAGE_PATH.DECO, element: <DecoPage /> },
  
  // Savings Routes
  { path: PAGE_PATH.SAVINGS, element: <SavingsPage /> },
  { path: PAGE_PATH.DEPOSIT, element: <DepositPage /> },
  { path: PAGE_PATH.DEPOSIT_RESULT, element: <DepositResultPage /> },
  
  // Account Creation Funnel
  { path: PAGE_PATH.ACCOUNT_CREATION, element: <AccountCreationFunnel /> },
  { path: `${PAGE_PATH.ACCOUNT_CREATION}/*`, element: <AccountCreationFunnel /> },
];

// 3. Public Routes (No Authentication)
const publicRoutes = [
  { path: PAGE_PATH.LOGIN, element: <LoginPage /> },
  { path: PAGE_PATH.ONBOARDING, element: <OnboardingPage /> },
  { path: PAGE_PATH.AUTH_CALLBACK, element: <AuthCallbackPage /> },
  { path: PAGE_PATH.COLORTEST, element: <ColorTestPage /> },
];
```

**Router Configuration Pattern:**

```typescript
export const router = createBrowserRouter([
  {
    path: '/',
    element: <Outlet />,
    children: [
      // Public routes (no auth required)
      ...publicRoutes,
      
      // SavingsLayout routes (main navigation)
      {
        path: '/',
        element: (
          <ProtectedRoute>
            <SavingsLayout />
          </ProtectedRoute>
        ),
        children: savingsLayoutRoutes,
      },
      
      // Detail pages with SavingsDetailLayout
      {
        path: PAGE_PATH.SAVINGS_DETAIL_WITH_ID,
        element: <SavingsDetailLayout title="적금 상세" />,
        children: [{ index: true, element: <SavingsDetailPage /> }],
      },
      
      // Standalone protected routes
      ...protectedRoutesWithoutLayout.map(({ path, element }) => ({
        path,
        element: <ProtectedRoute>{element}</ProtectedRoute>,
      })),
      
      // Fallback
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
```

### Layout System

**Layout Hierarchy:**

- **SavingsLayout**: Main app layout with bottom navigation
- **SavingsDetailLayout**: Detail pages with DetailTopBar + PageScrollArea
- **FunnelLayout**: Account creation steps with StepHeader + ProgressBar

**Layout Components:**

```typescript
// Main layout for home pages
<SavingsLayout>
  <PageScrollArea>
    {children} // HomePage, WalletPage, ProductsPage
  </PageScrollArea>
</SavingsLayout>

// Detail pages with scroll management
<SavingsDetailLayout title="페이지 제목">
  <PageScrollArea>
    {children} // SavingsDetailPage, AccountDetailPage
  </PageScrollArea>
</SavingsDetailLayout>

// Funnel with step navigation
<FunnelLayout>
  <StepHeader />
  {children} // Account creation steps
</FunnelLayout>
```

### Scroll Management System

**PageScrollArea Integration:**

- Radix UI ScrollArea with custom viewport selector
- Global scroll state management via `useScroll` hook
- Automatic scroll reset on route changes via `useScrollReset`

**Scroll-Based Components:**

```typescript
// Sticky components with scroll detection
<StickyBalance
  isVisible={isSticky}
  scrollDirection={scrollDirection}
/>

// useScroll integration
const { scrollY, scrollDirection } = useScroll();
const shouldBeSticky = balanceRef.current?.getBoundingClientRect().top <= 56;
```

**Scroll Utilities:**

- `scrollToTop()`: Scroll to page top (PageScrollArea-aware)
- `scrollToElement()`: Scroll to specific element
- `useScrollReset()`: Auto-reset scroll on navigation

### Navigation System

**Context-Aware Back Navigation:**

```typescript
// URL parameter-based navigation
navigate(`/account-creation/start?type=${accountType}&from=products`);

// Smart back button logic
const fromParam = searchParams.get('from');
if (fromParam === 'products') {
  navigate(PAGE_PATH.PRODUCTS);
} else {
  navigate(PAGE_PATH.WALLET); // default
}
```

**Funnel Navigation:**

- `useStepProgress`: Step-by-step navigation with URL persistence
- URL parameter preservation across funnel steps
- Dynamic step arrays based on account type (SAVINGS vs CHECKING)

## Environment Setup

```bash
# .env
VITE_API_BASE_URL=http://localhost:8080/api
```

## CI/CD Pipeline

GitLab CI with Bun runtime:

1. **Setup** - Dependency installation
2. **Lint Check** - ESLint validation
3. **Format Check** - Prettier validation
4. **Build** - TypeScript + Vite build

## 📋 기본 네이밍 컨벤션

| 항목 | 명사/동사 | 형식 | 예시 | 설명 |
| --- | --- | --- | --- | --- |
| 📦 **인터페이스명** | **명사** | `PascalCase` | `UserInterface`, `ProductInfo`, `ApiResponse` | 객체의 구조나 계약 정의 |
| 🔧 **타입명** | **명사** | `PascalCase` | `Status`, `UserRole`, `EventHandler` | 타입 별칭이나 Union 타입 |
| 📄 **변수명** | **명사** | `camelCase`, `UPPER_CASE`, `PascalCase` | `userName`, `API_BASE_URL`, `MyComponent` | 용도에 따라 케이스 선택 |
| ⚡ **함수명** | **동사** (또는 동사+명사) | `camelCase`, `PascalCase` | `getUserData()`, `MyComponent()` | 일반함수는 camelCase, 컴포넌트는 PascalCase |
| ✅ **Boolean 변수** | **형용사/상태** | `is`, `has`, `can` | `isActive`, `hasPermission`, `canEdit` | 상태, 가능 여부 표현 |
| 🔠 **상수명** | **명사** | `UPPER_SNAKE_CASE` | `MAX_RETRY_COUNT`, `API_URL`, `DEFAULT_THEME` | 변경되지 않는 값 |
| 🎯 **컴포넌트명** | **명사** | `PascalCase` | `Button`, `UserProfile`, `ProductCard` | React 컴포넌트 |
| 🔒 **Private 변수** | **명사** | `_camelCase` | `_privateData`, `_internalState` | 언더스코어로 시작 허용 |

## 🛠️ TypeScript 타입 컨벤션

| 구분 | 권장 방식 | 금지 방식 | 예시 | 이유 |
| --- | --- | --- | --- | --- | --- | --- |
| **배열 타입** | `타입[]` | `Array<타입>` | `string[]` ✅<br>`Array<string>` ❌ | 간결하고 읽기 쉬움 |
| **any 사용** | `unknown` 또는 구체적 타입 | `any` | `unknown` ✅<br>`any` ❌ | 타입 안전성 보장 |
| **null 처리** | `타입 | null` | 타입 무시 | `User | null` ✅ | 명시적 null 처리 |
| **객체 정의** | `interface` (객체 모양) | - | `interface User {}` ✅ | 객체 구조 정의에 적합 |
| **복합 타입** | `type` (Union 등) | - | `type Status = "loading" | "success"` ✅ | 복잡한 타입 정의에 적합 |
| **빈 함수** | 허용 | 제한 없음 | `const noop = () => {}` ✅ | `@typescript-eslint/no-empty-function` off |

## ⚛️ React 컴포넌트 컨벤션

| 구분 | 권장 방식 | 금지 방식 | 예시 | 이유 |
| --- | --- | --- | --- | --- |
| **컴포넌트 정의** | 일반 함수 | `React.FC` | `const Button = (props: ButtonProps) => {}` ✅<br>`const Button: React.FC<ButtonProps> = {}` ❌ | children 자동 포함 방지, 기본값 설정 용이 |
| **함수 정의** | 화살표 함수 | function 선언 | `const handleClick = () => {}` ✅<br>`function handleClick() {}` ❌ | 일관성, 스코프 명확성 |
| **Props 정의** | interface | type (선택적) | `interface ButtonProps {}` ✅ | 확장 가능성, 명확한 구조 |
| **이벤트 핸들러** | `React.이벤트타입` | 브라우저 이벤트 | `React.MouseEvent` ✅<br>`MouseEvent` ❌ | React 이벤트 시스템 활용 |
| **컴포넌트 Export** | 명명된 함수 export | 익명 함수 export | `export default function App() {}` ✅<br>`export default () => {}` ❌ | Fast Refresh 호환, 디버깅 용이 |

## 🔧 ESLint 코드 품질 규칙

| 구분 | 규칙 | 예시 (✅ 권장 / ❌ 금지) | 이유 |
| --- | --- | --- | --- |
| **타입 변환** | `no-implicit-coercion` | `value !== null` ✅<br>`array.length > 0` ✅<br>`Number(input)` ✅<br>`!!value` ❌ | 구체적 조건으로 의도 명확화 |
| **중괄호 사용** | `curly` | `if (condition) { doSomething(); }` ✅<br>`if (condition) doSomething();` ❌ | 코드 블록 명확화, 버그 방지 |
| **TODO/BUG 추적** | `no-warning-comments` | `// TODO: 페이지네이션 추가` ⚠️<br>`// 나중에 수정` ❌ | 체계적 작업 관리 |
| **Hooks 의존성** | `react-hooks/exhaustive-deps` | `useEffect(() => {}, [userId])` ✅<br>`useEffect(() => {}, [])` ❌ | React Hooks 안전한 사용 |
| **Fast Refresh** | `react-refresh/only-export-components` | `export default Component` ✅<br>`export default { Component }` ❌ | 개발 환경 최적화 |

## 📦 Import/Export 컨벤션

| 구분 | 형식 | 예시 | 설명 |
| --- | --- | --- | --- |
| **타입 import** | `import type` | `import type { User } from './types'` | 타입만 가져올 때 명시적 구분 |
| **값 import** | `import` | `import { API_URL } from './constants'` | 실제 값이나 함수 가져올 때 |
| **혼합 import** | `import { value, type Type }` | `import { API_URL, type User } from './file'` | 값과 타입을 함께 가져올 때 |
| **default export** | 컴포넌트에만 사용 | `export default Button` | 주요 컴포넌트 내보낼 때만 |
| **상대 경로 금지** | `no-relative-import-paths` | `import { utils } from '@/utils'` ✅<br>`import { utils } from '../utils'` ❌ | 절대 경로로 명확한 구조 |

## 🎨 파일 및 폴더 구조

| 구분 | 네이밍 | 예시 | 설명 |
| --- | --- | --- | --- |
| **컴포넌트 파일** | `PascalCase.tsx` | `Button.tsx`, `UserProfile.tsx` | 컴포넌트명과 파일명 일치 |
| **훅 파일** | `use로 시작, camelCase` | `useUser.ts`, `useLocalStorage.ts` | 커스텀 훅 파일 |
| **타입 파일** | `camelCase.types.ts` | `user.types.ts`, `apiClient.types.ts` | 타입 정의 전용 파일 |
| **유틸리티 파일** | `camelCase.ts` | `dateUtils.ts`, `apiClient.ts` | 헬퍼 함수 모음 |

## 📁 폴더명 컨벤션 (camelCase 통일)

| 폴더 타입 | 네이밍 | 예시 | 설명 |
| --- | --- | --- | --- |
| **모든 폴더** | `camelCase` | `components/`, `pages/`, `hooks/`, `types/` | **프로젝트 전체 일관성** |

## 🚫 금지 사항

| 금지 항목 | 이유 | 대안 |
| --- | --- | --- |
| `any` 타입 | 타입 안전성 상실 | `unknown`, 구체적 타입 정의 |
| `React.FC` | children 자동 포함, 제네릭 어려움 | 일반 함수로 컴포넌트 정의 |
| `Array<타입>` | 가독성 저하 | `타입[]` 형식 사용 |
| `function` 선언 | 호이스팅 이슈, 일관성 저하 | 화살표 함수 사용 |
| `enum` | 번들 크기 증가, Tree-shaking 불가 | `as const` 객체 사용 |
| `if(value)` 같은 falsy 체크 | 의도 불명확, 예상치 못한 동작 | `value !== null`, `array.length > 0` 구체적 조건 |
| 중괄호 생략 | 버그 발생 위험 | 항상 중괄호 사용 |
| 상대 경로 import | 파일 이동 시 문제, 구조 파악 어려움 | 절대 경로 사용 |
| 매직 넘버 및 매직 스트링 | 오타 위험, 의미 불분명, 유지보수 어려움 | 상수 정의, 타입 정의, 특히 api 경로 및 router 경로 |

## 📝 주석 및 문서화 컨벤션

| 구분 | 형식 | 예시 | 설명 |
| --- | --- | --- | --- |
| **함수 문서화** | JSDoc | `/** @param userId - 사용자 ID */` | 매개변수, 반환값 설명 |
| **TODO 주석** | 키워드 명시 | `// TODO: 페이지네이션 추가하기` | 추후 작업 사항 추적 |
| **BUG 주석** | 키워드 명시 | `// BUG: 모바일에서 스크롤 문제` | 알려진 버그 추적 |
| **일반 주석** | 의도 설명 | `// 사용자 권한 확인 후 렌더링` | 코드의 의도 명확화 |
