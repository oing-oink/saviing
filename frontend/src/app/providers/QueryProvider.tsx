// 전역으로 queryClient(데이터 캐시/관리 객체)를 공금
import { QueryClientProvider } from "@tanstack/react-query";
import { queryClient } from "../query/client";

export default function QueryProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}
