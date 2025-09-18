import { RouterProvider } from 'react-router-dom';
import QueryProvider from './providers/QueryProvider';
import ErrorBoundaryProvider from './providers/ErrorBoundaryProvider';
import { router } from './router/routes';
import AppLayout from './layouts/AppLayout';
import { Toaster } from 'react-hot-toast';

const App = () => {
  return (
    <ErrorBoundaryProvider>
      <QueryProvider>
        <AppLayout>
          <RouterProvider router={router} />
          <Toaster position="bottom-center" reverseOrder={false} />
        </AppLayout>
      </QueryProvider>
    </ErrorBoundaryProvider>
  );
};

export default App;
