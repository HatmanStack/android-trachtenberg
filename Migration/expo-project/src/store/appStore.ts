import { create } from 'zustand';
import { persistMiddleware } from './middleware/persistMiddleware';

interface AppState {
  // Settings
  hintsEnabled: boolean;
  hintHelpShown: boolean;

  // Tutorial state
  tutorialPage: number;

  // Practice state (initialized in Phase 3)
  currentEquation: string;
  currentAnswer: string;
  answerProgress: string;
  indexCount: number;

  // Actions
  setHintsEnabled: (enabled: boolean) => void;
  setHintHelpShown: (shown: boolean) => void;
  setTutorialPage: (page: number) => void;
}

export const useAppStore = create<AppState>()(
  persistMiddleware(
    (set) => ({
      // Initial state
      hintsEnabled: false,
      hintHelpShown: false,
      tutorialPage: 0,
      currentEquation: '',
      currentAnswer: '',
      answerProgress: '',
      indexCount: 0,

      // Actions
      setHintsEnabled: (enabled) => set({ hintsEnabled: enabled }),
      setHintHelpShown: (shown) => set({ hintHelpShown: shown }),
      setTutorialPage: (page) => set({ tutorialPage: page }),
    }),
    {
      name: 'trachtenberg-app-storage',
      partialize: (state) => ({
        hintsEnabled: state.hintsEnabled,
        hintHelpShown: state.hintHelpShown,
      }),
    }
  )
);
