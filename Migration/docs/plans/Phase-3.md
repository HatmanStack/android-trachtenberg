# Phase 3: Practice Mode & Quiz Logic

## Phase Goal

Implement the Practice screen with random multiplication problem generation, multiple-choice answer interface, answer validation, and progressive answer building (digit-by-digit). This phase ports the core quiz functionality from Android's `PracticeActivity.java`, excluding the hint system (which will be added in Phase 4).

**Success Criteria:**
- Random 4-digit × 3-digit multiplication problems generate correctly
- Four answer buttons display with one correct answer and three unique incorrect answers
- Answer validation works correctly (right/wrong feedback)
- Progressive answer building (building answer right-to-left, one digit at a time)
- New problem generates automatically after completing current problem
- State persists across app reloads (current equation, progress)
- UI matches Android app functionality (without hints for now)

**Estimated tokens:** ~25,000

---

## Prerequisites

- Phase 1 completed (infrastructure ready)
- Phase 2 completed (tutorial system working, navigation to Practice screen functional)
- Understanding of the Android practice logic:
  - `operatorEquation()` - Problem generation
  - `buttonQuestion()` - Multiple choice setup
  - `pickAnswer()` - Answer validation and progression
- Zustand store ready to add practice state

---

## Tasks

### Task 1: Extend Zustand Store with Practice State

**Goal:** Add all necessary state for practice mode to the Zustand store.

**Files to Modify:**
- `Migration/expo-project/src/store/appStore.ts` - Add practice state and actions

**Prerequisites:**
- Phase 1 Task 6 completed (store initialized)
- Read `PracticeActivity.java` lines 38-62 (state variables)

**Implementation Steps:**

1. Add practice state to `AppState` interface:
   ```typescript
   // Practice state
   currentEquation: string;         // e.g., "1234 * 567"
   currentAnswer: string;           // Complete answer as string
   answerProgress: string;          // Partial answer displayed
   indexCount: number;              // Current digit position
   firstCharRemainder: number;      // Carry from previous digit
   answerChoices: number[];         // Four button values [0-9]
   correctAnswerIndex: number;      // Which button (0-3) is correct
   ```

2. Add practice actions to the store:
   ```typescript
   // Practice actions
   generateNewProblem: () => void;
   submitAnswer: (buttonIndex: number) => { isCorrect: boolean; isComplete: boolean };
   resetPractice: () => void;
   ```

3. Update persistence configuration:
   - Add `currentEquation`, `currentAnswer`, `answerProgress`, `indexCount` to `partialize` function
   - This matches Android's `onSaveInstanceState()` behavior

4. Initialize practice state with empty/default values

**Verification Checklist:**
- [ ] Practice state variables added to interface
- [ ] Practice actions defined
- [ ] State persists across reloads (selected fields)
- [ ] TypeScript types complete
- [ ] No compilation errors

**Testing Instructions:**
- Verify store compiles and exports correctly
- Test that practice state initializes with default values
- Verify persistence configuration includes practice fields
- Use `useAppStore.getState()` to inspect initial state

**Commit Message Template:**
```
feat(store): add practice mode state and actions

- Added practice state variables to Zustand store
- Defined practice action signatures
- Configured persistence for practice progress
- Initialized with default values
```

**Estimated tokens:** ~2,000

---

### Task 2: Implement Problem Generation Logic

**Goal:** Port the `operatorEquation()` method from Android to generate random 4-digit × 3-digit multiplication problems.

**Files to Create:**
- `Migration/expo-project/src/utils/problemGenerator.ts` - Problem generation utilities

**Prerequisites:**
- Task 1 completed (practice state added to store)
- Read `PracticeActivity.java` lines 175-184 (`operatorEquation()` method)

**Implementation Steps:**

1. Create `src/utils/problemGenerator.ts`:
   ```typescript
   import { PracticeProblem } from '@types';

   /**
    * Generates a random multiplication problem
    * First number: 4 digits (1000-9999)
    * Second number: 3 digits (100-999)
    * Matches Android's operatorEquation() logic
    */
   export function generateProblem(): PracticeProblem {
     let firstNumber = Math.floor(Math.random() * 10000);
     let secondNumber = Math.floor(Math.random() * 1000);

     // Ensure minimum digit counts (match Java logic lines 179-182)
     while (firstNumber < 1000 || secondNumber < 100) {
       firstNumber = Math.floor(Math.random() * 10000);
       secondNumber = Math.floor(Math.random() * 1000);
     }

     const answer = firstNumber * secondNumber;

     return {
       firstNumber,
       secondNumber,
       answer,
     };
   }

   /**
    * Formats a problem as an equation string
    */
   export function formatEquation(problem: PracticeProblem): string {
     return `${problem.firstNumber} * ${problem.secondNumber}`;
   }
   ```

2. Ensure randomness is sufficient:
   - `Math.random()` is adequate for this use case
   - Consider seeding for testing purposes (optional)

3. Add validation helper:
   ```typescript
   /**
    * Validates a problem meets digit requirements
    */
   export function isValidProblem(problem: PracticeProblem): boolean {
     return (
       problem.firstNumber >= 1000 &&
       problem.firstNumber < 10000 &&
       problem.secondNumber >= 100 &&
       problem.secondNumber < 1000
     );
   }
   ```

**Verification Checklist:**
- [ ] `generateProblem()` creates valid problems
- [ ] First number is always 4 digits (1000-9999)
- [ ] Second number is always 3 digits (100-999)
- [ ] Answer is calculated correctly
- [ ] `formatEquation()` formats properly
- [ ] Validation helper works

**Testing Instructions:**
- Write unit tests:
  ```typescript
  describe('generateProblem', () => {
    it('generates 4-digit by 3-digit problems', () => {
      for (let i = 0; i < 100; i++) {
        const problem = generateProblem();
        expect(problem.firstNumber).toBeGreaterThanOrEqual(1000);
        expect(problem.firstNumber).toBeLessThan(10000);
        expect(problem.secondNumber).toBeGreaterThanOrEqual(100);
        expect(problem.secondNumber).toBeLessThan(1000);
        expect(problem.answer).toBe(problem.firstNumber * problem.secondNumber);
      }
    });
  });
  ```
- Test `formatEquation()` output format
- Test validation helper

**Commit Message Template:**
```
feat(utils): implement problem generation logic

- Created generateProblem function for random problems
- Ported Android operatorEquation logic to TypeScript
- Added equation formatting and validation helpers
- Added comprehensive unit tests
```

**Estimated tokens:** ~2,500

---

### Task 3: Implement Multiple Choice Logic

**Goal:** Port the `buttonQuestion()` method to generate four answer choices (one correct, three incorrect).

**Files to Create:**
- `Migration/expo-project/src/utils/answerChoices.ts` - Answer choice generation

**Prerequisites:**
- Task 2 completed (problem generation working)
- Read `PracticeActivity.java` lines 188-216 (`buttonQuestion()` method)

**Implementation Steps:**

1. Create `src/utils/answerChoices.ts`:
   ```typescript
   import { AnswerChoice } from '@types';

   /**
    * Generates four answer choices for a multiple choice quiz
    * @param correctDigit - The correct digit (0-9)
    * @returns Array of 4 answer choices with one correct
    */
   export function generateAnswerChoices(correctDigit: number): {
     choices: number[];
     correctIndex: number;
   } {
     // Random position for correct answer (0-3)
     const correctIndex = Math.floor(Math.random() * 4);

     const choices: number[] = new Array(4).fill(-1);
     choices[correctIndex] = correctDigit;

     // Fill remaining positions with unique incorrect digits
     for (let i = 0; i < 4; i++) {
       if (i === correctIndex) continue;

       let incorrectDigit = Math.floor(Math.random() * 10);

       // Ensure uniqueness (match Java logic lines 203-206)
       while (choices.includes(incorrectDigit)) {
         incorrectDigit = Math.floor(Math.random() * 10);
       }

       choices[i] = incorrectDigit;
     }

     return { choices, correctIndex };
   }

   /**
    * Extracts a digit from a number at a specific position (right-to-left)
    * @param number - The number to extract from
    * @param position - Position from right (0 = rightmost)
    */
   export function getDigitAtPosition(number: number, position: number): number {
     const str = number.toString();
     const index = str.length - 1 - position;
     if (index < 0) return 0;
     return parseInt(str[index], 10);
   }
   ```

2. Match Android logic exactly:
   - Correct answer placed at random index (0-3)
   - Three incorrect answers are unique and different from correct answer
   - All four choices are different from each other

**Verification Checklist:**
- [ ] `generateAnswerChoices()` creates 4 unique choices
- [ ] Correct answer is at a random position
- [ ] All choices are valid digits (0-9)
- [ ] No duplicate choices
- [ ] `getDigitAtPosition()` extracts correct digit

**Testing Instructions:**
- Write unit tests:
  ```typescript
  describe('generateAnswerChoices', () => {
    it('generates 4 unique choices', () => {
      const { choices, correctIndex } = generateAnswerChoices(5);
      expect(choices.length).toBe(4);
      expect(new Set(choices).size).toBe(4); // All unique
      expect(choices[correctIndex]).toBe(5);
    });

    it('places correct answer at random position', () => {
      const positions = new Set();
      for (let i = 0; i < 20; i++) {
        const { correctIndex } = generateAnswerChoices(7);
        positions.add(correctIndex);
      }
      expect(positions.size).toBeGreaterThan(1); // Randomized
    });
  });

  describe('getDigitAtPosition', () => {
    it('extracts digits from right to left', () => {
      expect(getDigitAtPosition(12345, 0)).toBe(5);
      expect(getDigitAtPosition(12345, 1)).toBe(4);
      expect(getDigitAtPosition(12345, 4)).toBe(1);
    });
  });
  ```

**Commit Message Template:**
```
feat(utils): implement answer choice generation

- Created generateAnswerChoices function
- Ported Android buttonQuestion logic to TypeScript
- Added getDigitAtPosition helper for digit extraction
- Ensured uniqueness of answer choices
- Added comprehensive unit tests
```

**Estimated tokens:** ~3,000

---

### Task 4: Implement Answer Validation Logic

**Goal:** Port the core answer validation logic from `pickAnswer()`, handling correct/incorrect answers and progressive answer building.

**Files to Create:**
- `Migration/expo-project/src/utils/answerValidator.ts` - Answer validation logic

**Prerequisites:**
- Task 2 and Task 3 completed
- Read `PracticeActivity.java` lines 342-417 (`pickAnswer()` method)

**Implementation Steps:**

1. Create `src/utils/answerValidator.ts`:
   ```typescript
   /**
    * Validates a user's answer for the current digit
    * @param selectedIndex - Index of button pressed (0-3)
    * @param correctIndex - Index of correct answer (0-3)
    * @param currentAnswer - Full answer string
    * @param indexCount - Current digit position (0 = rightmost)
    * @returns Validation result with next state
    */
   export interface ValidationResult {
     isCorrect: boolean;
     isComplete: boolean;
     newIndexCount: number;
     newAnswerProgress: string;
     newRemainder: number;
   }

   export function validateAnswer(
     selectedIndex: number,
     correctIndex: number,
     currentAnswer: string,
     indexCount: number,
     currentRemainder: number
   ): ValidationResult {
     const isCorrect = selectedIndex === correctIndex;

     if (!isCorrect) {
       return {
         isCorrect: false,
         isComplete: false,
         newIndexCount: indexCount,
         newAnswerProgress: currentAnswer.substring(
           currentAnswer.length - indexCount
         ),
         newRemainder: currentRemainder,
       };
     }

     // Correct answer - advance to next digit
     const newIndexCount = indexCount + 1;
     const isComplete = newIndexCount >= currentAnswer.length;

     // Extract partial answer for display
     const newAnswerProgress = currentAnswer.substring(
       currentAnswer.length - newIndexCount
     );

     // Calculate remainder/carry for next digit
     // This will be fully implemented in Phase 4 with hint system
     const newRemainder = 0; // Placeholder for now

     return {
       isCorrect: true,
       isComplete,
       newIndexCount,
       newAnswerProgress,
       newRemainder,
     };
   }
   ```

2. Note: Remainder calculation is simplified for now - full logic in Phase 4

**Verification Checklist:**
- [ ] Function validates correct vs incorrect answers
- [ ] Progressive answer building works
- [ ] Completion detection works
- [ ] TypeScript types complete

**Testing Instructions:**
- Write unit tests:
  ```typescript
  describe('validateAnswer', () => {
    it('returns isCorrect true when indices match', () => {
      const result = validateAnswer(2, 2, '12345', 0, 0);
      expect(result.isCorrect).toBe(true);
    });

    it('returns isCorrect false when indices differ', () => {
      const result = validateAnswer(1, 2, '12345', 0, 0);
      expect(result.isCorrect).toBe(false);
    });

    it('builds answer progressively', () => {
      let result = validateAnswer(0, 0, '12345', 0, 0);
      expect(result.newAnswerProgress).toBe('5');
      expect(result.newIndexCount).toBe(1);

      result = validateAnswer(0, 0, '12345', 1, 0);
      expect(result.newAnswerProgress).toBe('45');
      expect(result.newIndexCount).toBe(2);
    });

    it('detects completion', () => {
      const result = validateAnswer(0, 0, '12345', 4, 0);
      expect(result.isComplete).toBe(true);
    });
  });
  ```

**Commit Message Template:**
```
feat(utils): implement answer validation logic

- Created validateAnswer function
- Ported core pickAnswer logic from Android
- Implemented progressive answer building
- Added completion detection
- Added unit tests for validation
```

**Estimated tokens:** ~3,000

---

### Task 5: Implement Practice Store Actions

**Goal:** Wire up the practice logic to Zustand store actions, making the practice state fully functional.

**Files to Modify:**
- `Migration/expo-project/src/store/appStore.ts` - Implement practice actions

**Prerequisites:**
- Tasks 1-4 completed (utilities ready)

**Implementation Steps:**

1. Implement `generateNewProblem` action:
   ```typescript
   import { generateProblem, formatEquation } from '@utils/problemGenerator';
   import { generateAnswerChoices, getDigitAtPosition } from '@utils/answerChoices';

   generateNewProblem: () => {
     const problem = generateProblem();
     const equation = formatEquation(problem);
     const answer = problem.answer.toString();

     // Get first digit (rightmost)
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
     });
   },
   ```

2. Implement `submitAnswer` action:
   ```typescript
   import { validateAnswer } from '@utils/answerValidator';
   import { getDigitAtPosition } from '@utils/answerChoices';

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

     // If complete, generate new problem
     if (result.isComplete) {
       setTimeout(() => {
         get().generateNewProblem();
       }, 2000); // Delay for user to see complete answer
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
   ```

3. Implement `resetPractice` action:
   ```typescript
   resetPractice: () => {
     set({
       currentEquation: '',
       currentAnswer: '',
       answerProgress: '',
       indexCount: 0,
       firstCharRemainder: 0,
       answerChoices: [],
       correctAnswerIndex: -1,
     });
   },
   ```

**Verification Checklist:**
- [ ] `generateNewProblem` creates new problem and sets state
- [ ] `submitAnswer` validates and updates state correctly
- [ ] Answer progresses digit by digit
- [ ] New problem generates after completion
- [ ] `resetPractice` clears state
- [ ] No TypeScript errors

**Testing Instructions:**
- Test action calls manually:
  ```typescript
  const store = useAppStore.getState();
  store.generateNewProblem();
  console.log(store.currentEquation);
  const result = store.submitAnswer(store.correctAnswerIndex);
  console.log(result); // Should be { isCorrect: true, isComplete: false }
  ```
- Verify state updates after each action
- Test complete answer flow (all digits)

**Commit Message Template:**
```
feat(store): implement practice action logic

- Implemented generateNewProblem action
- Implemented submitAnswer with validation
- Added automatic problem generation on completion
- Implemented resetPractice action
- Integrated all practice utilities with store
```

**Estimated tokens:** ~4,000

---

### Task 6: Build Practice Screen UI

**Goal:** Create the Practice screen UI with equation display, answer progress, and four answer buttons.

**Files to Modify:**
- `Migration/expo-project/src/screens/PracticeScreen.tsx` - Replace placeholder

**Files to Create:**
- `Migration/expo-project/src/components/AnswerButton.tsx` - Custom answer button component

**Prerequisites:**
- Task 5 completed (store actions ready)
- React Native Paper available

**Implementation Steps:**

1. Create `src/components/AnswerButton.tsx`:
   ```typescript
   import React from 'react';
   import { StyleSheet } from 'react-native';
   import { Button } from 'react-native-paper';

   interface AnswerButtonProps {
     value: number;
     onPress: () => void;
     disabled?: boolean;
   }

   export const AnswerButton: React.FC<AnswerButtonProps> = React.memo(
     ({ value, onPress, disabled = false }) => {
       return (
         <Button
           mode="contained"
           onPress={onPress}
           disabled={disabled}
           style={styles.button}
           labelStyle={styles.label}
         >
           {value}
         </Button>
       );
     }
   );

   const styles = StyleSheet.create({
     button: {
       margin: 8,
       flex: 1,
       minHeight: 80,
     },
     label: {
       fontSize: 32,
       fontWeight: 'bold',
     },
   });
   ```

2. Implement `PracticeScreen.tsx`:
   ```typescript
   import React, { useEffect, useState } from 'react';
   import { View, StyleSheet } from 'react-native';
   import { Text, Surface } from 'react-native-paper';
   import { useAppStore } from '@store/appStore';
   import { AnswerButton } from '@components/AnswerButton';
   import { COLORS, SPACING, FONT_SIZES } from '@theme/constants';

   export const PracticeScreen: React.FC = () => {
     const {
       currentEquation,
       answerProgress,
       answerChoices,
       generateNewProblem,
       submitAnswer,
     } = useAppStore();

     const [feedbackText, setFeedbackText] = useState('');

     // Generate first problem on mount
     useEffect(() => {
       if (!currentEquation) {
         generateNewProblem();
       }
     }, []);

     const handleAnswerPress = (buttonIndex: number) => {
       const result = submitAnswer(buttonIndex);

       if (result.isCorrect) {
         setFeedbackText(result.isComplete ? 'Complete!' : 'Correct!');
       } else {
         setFeedbackText('Wrong');
       }

       // Clear feedback after delay
       setTimeout(() => setFeedbackText(''), 1000);
     };

     return (
       <View style={styles.container}>
         <Surface style={styles.equationSurface}>
           <Text variant="headlineLarge" style={styles.equation}>
             {currentEquation}
           </Text>
         </Surface>

         <Surface style={styles.progressSurface}>
           <Text variant="headlineMedium" style={styles.progress}>
             {answerProgress || '?'}
           </Text>
         </Surface>

         {feedbackText && (
           <Text
             variant="titleLarge"
             style={[
               styles.feedback,
               feedbackText === 'Wrong'
                 ? styles.feedbackWrong
                 : styles.feedbackCorrect,
             ]}
           >
             {feedbackText}
           </Text>
         )}

         <View style={styles.buttonGrid}>
           <View style={styles.buttonRow}>
             <AnswerButton
               value={answerChoices[0]}
               onPress={() => handleAnswerPress(0)}
             />
             <AnswerButton
               value={answerChoices[1]}
               onPress={() => handleAnswerPress(1)}
             />
           </View>
           <View style={styles.buttonRow}>
             <AnswerButton
               value={answerChoices[2]}
               onPress={() => handleAnswerPress(2)}
             />
             <AnswerButton
               value={answerChoices[3]}
               onPress={() => handleAnswerPress(3)}
             />
           </View>
         </View>
       </View>
     );
   };

   // StyleSheet implementation
   ```

3. Layout structure:
   - Top: Equation display (large, centered)
   - Middle: Answer progress (shows partial answer)
   - Middle: Feedback text (correct/wrong/complete)
   - Bottom: 2×2 grid of answer buttons

4. Feedback mechanism:
   - Show "Correct" or "Wrong" based on answer
   - Auto-hide after 1 second
   - Show "Complete!" when problem finished

**Verification Checklist:**
- [ ] Screen displays equation and answer progress
- [ ] Four answer buttons render correctly
- [ ] Clicking correct button advances to next digit
- [ ] Clicking incorrect button shows "Wrong" feedback
- [ ] Answer progress builds from right to left
- [ ] New problem generates after completion
- [ ] Layout is responsive

**Testing Instructions:**
- Navigate to Practice screen
- Verify equation displays
- Click answer buttons and verify feedback
- Complete a full problem and verify new one generates
- Test on different screen sizes
- Verify state persists if app is reloaded mid-problem

**Commit Message Template:**
```
feat(screens): implement Practice screen UI

- Created AnswerButton component
- Built Practice screen with equation and progress display
- Implemented 2x2 answer button grid
- Added correct/wrong feedback mechanism
- Integrated with practice store actions
```

**Estimated tokens:** ~5,000

---

### Task 7: Add Menu Navigation to Practice Screen

**Goal:** Add a menu/header button to navigate to Settings (matching Android's menu functionality).

**Files to Modify:**
- `Migration/expo-project/src/screens/PracticeScreen.tsx` - Add navigation header options

**Prerequisites:**
- Task 6 completed (Practice screen UI built)
- React Navigation configured

**Implementation Steps:**

1. Add header configuration to Practice screen:
   ```typescript
   import { useNavigation, useLayoutEffect } from '@react-navigation/native';
   import { IconButton } from 'react-native-paper';

   export const PracticeScreen: React.FC = () => {
     const navigation = useNavigation();

     useLayoutEffect(() => {
       navigation.setOptions({
         headerRight: () => (
           <IconButton
             icon="cog"
             onPress={() => navigation.navigate('Settings')}
           />
         ),
       });
     }, [navigation]);

     // Rest of component...
   };
   ```

2. Alternatively, configure in navigator (if using stack navigator):
   - Add options in `StackNavigator.tsx` for Practice screen

3. Web tab navigation already has Settings tab available

**Verification Checklist:**
- [ ] Settings icon/button appears in header (mobile)
- [ ] Clicking button navigates to Settings screen
- [ ] Web version has Settings tab accessible
- [ ] Navigation works bidirectionally

**Testing Instructions:**
- On mobile: Verify header button appears and works
- On web: Verify Settings tab is accessible
- Navigate to Settings and back to Practice

**Commit Message Template:**
```
feat(navigation): add Settings navigation from Practice

- Added header button to navigate to Settings
- Configured for stack navigator on mobile
- Verified tab navigation works on web
```

**Estimated tokens:** ~1,500

---

### Task 8: Style Practice Screen

**Goal:** Polish the Practice screen styling to match Material Design and ensure good UX.

**Files to Modify:**
- `Migration/expo-project/src/screens/PracticeScreen.tsx` - Add/refine styles

**Prerequisites:**
- Task 6 completed (Practice screen UI built)

**Implementation Steps:**

1. Create comprehensive stylesheet:
   - Container with proper padding and spacing
   - Elevated surfaces for equation and progress
   - Large, readable fonts
   - Proper button sizing and spacing
   - Color-coded feedback (green for correct, red for wrong)

2. Ensure accessibility:
   - Sufficient contrast ratios
   - Large touch targets (buttons min 80x80)
   - Readable font sizes

3. Responsive design:
   - Adapt to different screen sizes
   - Landscape and portrait support
   - Tablet and desktop support

**Verification Checklist:**
- [ ] Consistent spacing and padding
- [ ] Readable typography
- [ ] Proper color usage (theme colors)
- [ ] Accessible (WCAG AA compliant)
- [ ] Responsive layout

**Testing Instructions:**
- Visual review on multiple devices
- Test in portrait and landscape
- Verify buttons are easily tappable
- Check color contrast

**Commit Message Template:**
```
style(screens): polish Practice screen styling

- Applied Material Design styling
- Ensured accessibility standards
- Implemented responsive layout
- Used theme colors consistently
```

**Estimated tokens:** ~2,000

---

### Task 9: Write Tests for Practice System

**Goal:** Create comprehensive tests for practice logic and UI.

**Files to Create:**
- `Migration/expo-project/__tests__/utils/problemGenerator.test.ts`
- `Migration/expo-project/__tests__/utils/answerChoices.test.ts`
- `Migration/expo-project/__tests__/utils/answerValidator.test.ts`
- `Migration/expo-project/__tests__/screens/PracticeScreen.test.tsx`
- `Migration/expo-project/__tests__/components/AnswerButton.test.tsx`

**Prerequisites:**
- All previous tasks completed

**Implementation Steps:**

1. Write tests for problem generation (Task 2)
2. Write tests for answer choices (Task 3)
3. Write tests for answer validation (Task 4)
4. Write component tests for AnswerButton
5. Write integration tests for PracticeScreen:
   - Test problem generation on mount
   - Test answer submission
   - Test progressive answer building
   - Test completion and new problem generation

**Verification Checklist:**
- [ ] All utility functions have unit tests
- [ ] Components have rendering tests
- [ ] Integration tests cover user flows
- [ ] All tests pass
- [ ] Code coverage meets targets (80%+)

**Testing Instructions:**
- Run `npm test`
- Verify all tests pass
- Check coverage report
- Fix any failing tests

**Commit Message Template:**
```
test(practice): add comprehensive practice system tests

- Added unit tests for problem generation
- Added unit tests for answer choice logic
- Added unit tests for answer validation
- Added component tests for AnswerButton
- Added integration tests for PracticeScreen
```

**Estimated tokens:** ~4,000

---

## Phase Verification

Before proceeding to Phase 4, ensure:

- [ ] Random problems generate correctly (4-digit × 3-digit)
- [ ] Answer choices are unique with one correct answer
- [ ] Answer validation works (correct/incorrect detection)
- [ ] Progressive answer building works (right-to-left)
- [ ] New problem generates after completion
- [ ] Practice screen UI is functional and styled
- [ ] State persists across app reloads
- [ ] Navigation to/from Settings works
- [ ] All tests pass
- [ ] No TypeScript errors

**Known Limitations:**
- Hint system not yet implemented (Phase 4)
- Remainder/carry calculation simplified (Phase 4)
- No animations yet (Phase 7)

**Integration Points:**
- Phase 4 will add the hint system to Practice screen
- Phase 5 will add hint toggle in Settings that affects Practice
- Phase 7 will add fade animations for feedback

---

## Review Feedback (Iteration 1)

### Overall Phase 3 Implementation

**Strong implementation with good test coverage**

> **Positive findings:**
> - Problem generation correctly implements 4-digit × 3-digit logic ✓
> - Answer choice generation ensures uniqueness and proper randomization ✓
> - Answer validation logic properly handles correct/incorrect answers ✓
> - Progressive answer building works right-to-left as specified ✓
> - Practice screen UI implemented with all required elements ✓
> - Store actions properly integrated with UI components ✓
> - Comprehensive tests written for all utilities ✓

### Tests Cannot Run (Critical Blocker from Phase 1)

**All Phase 3 tests blocked by Jest configuration**

> **Consider:** You've written excellent tests in `__tests__/utils/`:
>   - `problemGenerator.test.ts` (135 lines)
>   - `answerChoices.test.ts` (136 lines)
>   - `answerValidator.test.ts` (109 lines)
>
> **Think about:** But when you run `npm test`, do any of these tests actually execute successfully? Or do they all fail with AsyncStorage/Expo errors?
>
> **Reflect:** The Phase Verification checklist (line 945) says "All tests pass". Can you honestly check this box if Jest is misconfigured and no tests can run? Should Phase 1's Jest setup be fixed first?

### Task 2: Problem Generator Implementation

**Minor documentation inconsistency**

> **Consider:** Looking at `src/utils/problemGenerator.ts` line 16, there's a comment: "Matches Android's operatorEquation() logic (with bug fix)". And line 22 says "corrected from Android bug".
>
> **Think about:** What was the Android bug that you fixed? The original Android code (PracticeActivity.java lines 179-182) had a logic error in the while loop. Your TypeScript version fixes this.
>
> **Reflect:** Should this bug fix be documented more prominently? It's actually an improvement over the Android version. Perhaps add a comment explaining what the bug was?

### Task 4: Answer Validation - Remainder Calculation

**Simplified implementation noted**

> **Consider:** Looking at `src/utils/answerValidator.ts` line 47, you have a comment: "// This will be fully implemented in Phase 4 with hint system" and set `newRemainder = 0`.
>
> **Think about:** This is correct per the Phase 3 plan (line 117): "Remainder calculation is simplified for now". The validation comment matches the plan's intention.
>
> **Reflect:** Is this temporary simplification adequate for Phase 3's "without hints" requirement? The answer is yes - Phase 4 will complete this.

### Task 6: Practice Screen UI

**Missing feature - Navigation menu**

> **Consider:** Looking at Task 7 in the plan (starting line 793): "Add Menu Navigation to Practice Screen" with the goal to "Add a menu/header button to navigate to Settings".
>
> **Think about:** When you examine `src/screens/PracticeScreen.tsx`, do you see any header configuration with a Settings button? Or `useLayoutEffect` with `navigation.setOptions`?
>
> **Reflect:** The Android app (PracticeActivity.java lines 126-139) has a menu to navigate to Settings. Should your Practice screen have similar navigation capabilities?

### Equation Symbol Consistency

**Continuing the improvement from Phase 2**

> **Observation:** Your `formatEquation` function (problemGenerator.ts line 42) uses `×` symbol: `return "${problem.firstNumber} × ${problem.secondNumber}";`
>
> **Think about:** This matches the tutorial system's use of × (from Phase 2). Consistent mathematical notation across the app is good.
>
> **Reflect:** This improvement over Android's "x" should be documented somewhere - perhaps in a CHANGELOG or migration notes?

### Code Quality Observations

**Additional positive notes:**

- AnswerButton component properly memoized with React.memo ✓
- Store actions handle edge cases (complete answer, new problem) ✓
- Feedback mechanism (correct/wrong/complete) implemented correctly ✓
- Answer progress builds from right correctly (substring logic) ✓
- Problem generation tests include 100 iterations for randomness ✓

---

**Next Phase:** [Phase 4: Hint System Implementation](./Phase-4.md)
