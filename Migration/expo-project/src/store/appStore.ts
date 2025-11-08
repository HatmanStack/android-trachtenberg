import { create } from 'zustand';
import { persistMiddleware } from './middleware/persistMiddleware';

interface AppState {
  // Settings
  hintsEnabled: boolean;
  hintHelpShown: boolean;

  // Tutorial state
  tutorialPage: number;

  // Practice state
  currentEquation: string;           // e.g., "1234 × 567"
  currentAnswer: string;             // Complete correct answer
  answerProgress: string;            // Partial answer built so far
  indexCount: number;                // Current digit position (0-based from right)
  firstCharRemainder: number;        // Carry from previous digit calculation
  answerChoices: number[];           // Four button values [0-9]
  correctAnswerIndex: number;        // Which button (0-3) is correct

  // Hint state (Phase 4)
  move: number;
  moveCount: number;
  remainderHint: number;

  // Actions
  setHintsEnabled: (enabled: boolean) => void;
  setHintHelpShown: (shown: boolean) => void;
  setTutorialPage: (page: number) => void;

  // Practice actions
  generateNewProblem: () => void;
  submitAnswer: (buttonIndex: number) => { isCorrect: boolean; isComplete: boolean };
  resetPractice: () => void;
}

export const useAppStore = create<AppState>()(
  persistMiddleware(
    (set) => ({
      // Initial state
      hintsEnabled: false,
      hintHelpShown: false,
      tutorialPage: 0,

      // Practice state initial values
      currentEquation: '',
      currentAnswer: '',
      answerProgress: '',
      indexCount: 0,
      firstCharRemainder: 0,
      answerChoices: [],
      correctAnswerIndex: 0,

      // Hint state (Phase 4)
      move: 0,
      moveCount: 0,
      remainderHint: 0,

      // Actions
      setHintsEnabled: (enabled) => set({ hintsEnabled: enabled }),
      setHintHelpShown: (shown) => set({ hintHelpShown: shown }),
      setTutorialPage: (page) => set({ tutorialPage: page }),

      // Practice actions (implementations in Phase 3 Task 2-4)
      generateNewProblem: () => {
        // Will be implemented in Task 2
        console.log('generateNewProblem not yet implemented');
      },

      submitAnswer: (buttonIndex: number) => {
        // Will be implemented in Task 4
        console.log('submitAnswer not yet implemented:', buttonIndex);
        return { isCorrect: false, isComplete: false };
      },

      resetPractice: () => {
        set({
          currentEquation: '',
          currentAnswer: '',
          answerProgress: '',
          indexCount: 0,
          firstCharRemainder: 0,
          answerChoices: [],
          correctAnswerIndex: 0,
          move: 0,
          moveCount: 0,
          remainderHint: 0,
        });
      },
    }),
    {
      name: 'trachtenberg-app-storage',
      partialize: (state) => ({
        // Settings
        hintsEnabled: state.hintsEnabled,
        hintHelpShown: state.hintHelpShown,
        // Practice progress (persist current problem)
        currentEquation: state.currentEquation,
        currentAnswer: state.currentAnswer,
        answerProgress: state.answerProgress,
        indexCount: state.indexCount,
        firstCharRemainder: state.firstCharRemainder,
      }),
    }
  )
);
