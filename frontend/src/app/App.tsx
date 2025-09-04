import { RouterProvider } from "react-router-dom";
import QueryProvider from "./providers/QueryProvider";
import ErrorBoundaryProvider from "./providers/ErrorBoundaryProvider";
import { router } from "./router/routes";

export default function App() {
  return (
    <ErrorBoundaryProvider>
      <QueryProvider>
        <RouterProvider router={router} />
      </QueryProvider>
    </ErrorBoundaryProvider>
  );
}
