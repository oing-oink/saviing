import { RouterProvider } from 'react-router-dom';
import QueryProvider from './providers/QueryProvider';
import ErrorBoundaryProvider from './providers/ErrorBoundaryProvider';
import { router } from './router/routes';
import AppLayout from './layouts/AppLayout';

const App = () => {
  return (
    <ErrorBoundaryProvider>
      <QueryProvider>
        <AppLayout>
          <RouterProvider router={router} />
        </AppLayout>
      </QueryProvider>
    </ErrorBoundaryProvider>
  );
};

export default App;
