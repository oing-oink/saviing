import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react';

interface GlobalGameBackgroundContextType {
  isGameBackground: boolean;
  showGameBackground: () => void;
  hideGameBackground: () => void;
}

const GlobalGameBackgroundContext = createContext<GlobalGameBackgroundContextType | null>(null);

export const useGlobalGameBackground = () => {
  const context = useContext(GlobalGameBackgroundContext);
  if (!context) {
    throw new Error('useGlobalGameBackground must be used within a GlobalGameBackgroundProvider');
  }
  return context;
};

interface GlobalGameBackgroundProviderProps {
  children: ReactNode;
}

export const GlobalGameBackgroundProvider = ({ children }: GlobalGameBackgroundProviderProps) => {
  const [isGameBackground, setIsGameBackground] = useState(false);

  const showGameBackground = useCallback(() => setIsGameBackground(true), []);
  const hideGameBackground = useCallback(() => setIsGameBackground(false), []);

  const contextValue = useMemo(
    () => ({
      isGameBackground,
      showGameBackground,
      hideGameBackground,
    }),
    [isGameBackground, showGameBackground, hideGameBackground],
  );

  return (
    <GlobalGameBackgroundContext.Provider value={contextValue}>
      {children}
    </GlobalGameBackgroundContext.Provider>
  );
};
