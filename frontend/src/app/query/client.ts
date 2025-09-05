import { QueryClient } from "@tanstack/react-query";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 60_000, // 데이터를 얼마나 오래 신선하다고 볼지 (1분)
      gcTime: 300_000, // 캐시 데이터를 얼마나 오래 보관할지 (5분)
      retry: 1, // 요청 실패 시 재시도 횟수
      refetchOnWindowFocus: false, // 브라우저 창을 다시 클릭했을 떄 자동 재요청 여부
    },
  },
});
