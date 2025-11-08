import { create } from 'zustand';
import { persistMiddleware } from './middleware/persistMiddleware';
import { generateProblem, formatEquation } from '../utils/problemGenerator';
import { generateAnswerChoices, getDigitAtPosition } from '../utils/answerChoices';
import { validateAnswer } from '../utils/answerValidator';

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
    (set, get) => ({
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

      // Practice actions
      generateNewProblem: () => {
        const problem = generateProblem();
        const equation = formatEquation(problem);
        const answer = problem.answer.toString();

        // Get first digit (rightmost) for initial question
        const firstDigit = getDigitAtPosition(problem.answer, 0);
        const { choices, correctIndex } = generateAnswerChoices(firstDigit);

        set({
          currentEquation: equation,
          currentAnswer: answer,
          answerProgress: '',
          indexCount: 0,
          firstCharRemainder: 0,
          answerChoices: choices,
          correctAnswerIndex: correctIndex,
          move: 0,
          moveCount: 0,
          remainderHint: 0,
        });
      },

      submitAnswer: (buttonIndex: number) => {
        const state = get();
        const result = validateAnswer(
          buttonIndex,
          state.correctAnswerIndex,
          state.currentAnswer,
          state.indexCount,
          state.firstCharRemainder
        );

        if (!result.isCorrect) {
          return { isCorrect: false, isComplete: false };
        }

        // Update state for correct answer
        set({
          indexCount: result.newIndexCount,
          answerProgress: result.newAnswerProgress,
          firstCharRemainder: result.newRemainder,
        });

        // If complete, generate new problem after delay
        if (result.isComplete) {
          setTimeout(() => {
            get().generateNewProblem();
          }, 2000);
          return { isCorrect: true, isComplete: true };
        }

        // Generate new choices for next digit
        const nextDigit = getDigitAtPosition(
          parseInt(state.currentAnswer, 10),
          result.newIndexCount
        );
        const { choices, correctIndex } = generateAnswerChoices(nextDigit);

        set({
          answerChoices: choices,
          correctAnswerIndex: correctIndex,
        });

        return { isCorrect: true, isComplete: false };
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
