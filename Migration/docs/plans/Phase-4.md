# Phase 4: Hint System Implementation

## Phase Goal

Implement the complete Trachtenberg algorithm hint system that guides users through step-by-step multiplication calculations. This is the most complex phase, involving precise porting of the Android app's `practiceHint()`, `setIndex()`, and `setMove()` methods. The hint system shows which digits to multiply, accumulates partial sums, handles carries, and highlights relevant digits in the equation.

**Success Criteria:**
- Hint algorithm correctly calculates each step of Trachtenberg multiplication
- Move tracking system works (determines which digits to multiply at each step)
- Digit highlighting shows correct digits during each hint step
- Carry/remainder calculation works correctly across digits
- Hint progression matches Android app behavior exactly
- Hint visibility controlled by settings (toggle on/off)
- Comprehensive tests validate algorithm correctness against Android implementation

**Estimated tokens:** ~30,000

---

## Prerequisites

- Phase 3 completed (Practice mode working without hints)
- Deep understanding of Trachtenberg multiplication algorithm
- Android source files for reference:
  - `PracticeActivity.java` lines 219-267 (`practiceHint()`)
  - `PracticeActivity.java` lines 277-307 (`setIndex()`)
  - `PracticeActivity.java` lines 310-335 (`setMove()`)
  - `PracticeActivity.java` lines 364-376 (carry/remainder logic)
- Understanding of the move tracking arrays and conditional logic
- Zustand store with hint state variables

---

## Important: Test-Driven Development (TDD) for This Phase

**CRITICAL:** Due to the complexity of the hint algorithm, this phase MUST follow Test-Driven Development:

1. **Write tests FIRST** before implementing the algorithm
2. **Use known good inputs/outputs** from the Android app
3. **Test incrementally** - one function at a time
4. **Validate against Android** - manually test Android app to capture expected outputs

### TDD Workflow for Hint System:

```
For each function:
1. Write failing tests with expected behavior
2. Implement minimal code to pass tests
3. Refactor for clarity
4. Add more test cases
5. Repeat until function is complete
```

---

## Tasks

### Task 1: Extend Store with Hint State

**Goal:** Add all hint-related state variables to the Zustand store.

**Files to Modify:**
- `Migration/expo-project/src/store/appStore.ts` - Add hint state

**Prerequisites:**
- Phase 3 Task 1 completed (practice state added)
- Read `PracticeActivity.java` lines 38-62 for hint state variables

**Implementation Steps:**

1. Add hint state to `AppState` interface:
   ```typescript
   // Hint system state
   move: number;                    // Current hint step (0-24)
   moveCount: number;               // Total steps for current digit
   remainderHint: number;           // Accumulated hint value
   hintQuestion: string;            // Current hint question (e.g., "3 × 6")
   hintResult: string;              // Current hint result display
   hintHighlightIndices: number[];  // Indices to highlight in equation
   ```

2. Add hint actions:
   ```typescript
   // Hint actions
   nextHint: () => void;            // Advance to next hint step
   resetHints: () => void;          // Reset hint state for new digit
   ```

3. Initialize hint state with default values

4. Update persistence: hint state should NOT persist (reset each session)

**Verification Checklist:**
- [ ] Hint state variables added to interface
- [ ] Hint actions defined
- [ ] State initialized with defaults
- [ ] Hint state NOT persisted across sessions
- [ ] TypeScript compilation succeeds

**Testing Instructions:**
- Verify store compiles
- Check initial hint state values
- Ensure hint state is not in `partialize` function

**Commit Message Template:**
```
feat(store): add hint system state variables

- Added hint tracking state (move, moveCount, etc.)
- Defined hint action signatures
- Initialized with default values
- Configured to not persist hint state
```

**Estimated tokens:** ~1,500

---

### Task 2: Write Tests for Move Tracking Logic (TDD)

**Goal:** Write comprehensive tests for the move tracking system BEFORE implementing it.

**Files to Create:**
- `Migration/expo-project/__tests__/utils/hintMoveTracker.test.ts` - Move tracking tests

**Prerequisites:**
- Task 1 completed
- Understanding of `setMove()` and `setIndex()` logic from Android

**Implementation Steps:**

1. Analyze Android's move tracking:
   - `movesCount` array: `[0, 4, 9, 15, 20, 23, 24]` (lines 311)
   - Maps `indexCount` (digit position) to move number ranges
   - `movesIndexes` array: `[2, 2, 2, 1, 2, 1, 0, 2, 1, 0, 1, 0, 0]` (line 281)
   - Maps move number to which digits to multiply

2. Create test file with expected behaviors:
   ```typescript
   import { getMoveRange, getDigitIndices } from '@utils/hintMoveTracker';

   describe('Move Tracking System', () => {
     describe('getMoveRange', () => {
       test('indexCount 0 should return move 0-0, moveCount 0', () => {
         const result = getMoveRange(0);
         expect(result.startMove).toBe(0);
         expect(result.moveCount).toBe(0);
       });

       test('indexCount 1 should return move 1-3, moveCount 3', () => {
         const result = getMoveRange(1);
         expect(result.startMove).toBe(1);
         expect(result.moveCount).toBe(3);
       });

       test('indexCount 2 should return move 4-8, moveCount 8', () => {
         const result = getMoveRange(2);
         expect(result.startMove).toBe(4);
         expect(result.moveCount).toBe(8);
       });

       // Add tests for all indexCount values 0-6
     });

     describe('getDigitIndices', () => {
       test('move 0 should select digit indices [2, 2]', () => {
         const result = getDigitIndices(0);
         expect(result.firstStringIndex).toBe(2);
         expect(result.secondStringIndex).toBe(2);
       });

       test('move 1 should select digit indices [2, 2]', () => {
         const result = getDigitIndices(1);
         expect(result.firstStringIndex).toBe(2);
         expect(result.secondStringIndex).toBe(2);
       });

       // Add tests for all move values 0-23
       // Based on movesIndexes array logic
     });
   });
   ```

3. Create test cases for edge cases:
   - Move numbers at boundaries
   - Index count transitions
   - Out of range values (should handle gracefully)

4. **DO NOT IMPLEMENT YET** - just write the tests

**Verification Checklist:**
- [ ] Test file created with comprehensive test cases
- [ ] Tests cover all indexCount values (0-6)
- [ ] Tests cover all move values (0-23)
- [ ] Edge cases included
- [ ] Tests currently FAIL (implementation doesn't exist yet)

**Testing Instructions:**
- Run `npm test` - tests should fail (no implementation)
- Verify test structure is correct
- Review tests against Android logic

**Commit Message Template:**
```
test(hint): write tests for move tracking logic (TDD)

- Created comprehensive test suite for move tracking
- Defined expected behavior for getMoveRange
- Defined expected behavior for getDigitIndices
- Tests currently fail (no implementation)
```

**Estimated tokens:** ~3,000

---

### Task 3: Implement Move Tracking Logic

**Goal:** Implement the `setMove()` and `setIndex()` logic to pass the tests written in Task 2.

**Files to Create:**
- `Migration/expo-project/src/utils/hintMoveTracker.ts` - Move tracking implementation

**Prerequisites:**
- Task 2 completed (tests written)
- Read `PracticeActivity.java` lines 277-335

**Implementation Steps:**

1. Create `src/utils/hintMoveTracker.ts`:
   ```typescript
   /**
    * Move tracking arrays from Android implementation
    */
   const MOVES_COUNT = [0, 4, 9, 15, 20, 23, 24];
   const MOVES_INDEXES = [2, 2, 2, 1, 2, 1, 0, 2, 1, 0, 1, 0, 0];

   /**
    * Determines the move range for a given digit position
    * Ports Android's setMove() logic
    * @param indexCount - Current digit position (0-6)
    * @returns Start move and total move count for this digit
    */
   export function getMoveRange(indexCount: number): {
     startMove: number;
     moveCount: number;
   } {
     // Implementation based on lines 314-328
     // For indexCount > 0, find the corresponding range
   }

   /**
    * Determines which digit indices to multiply for a given move
    * Ports Android's setIndex() logic
    * @param move - Current move number (0-23)
    * @returns Indices in the first and second strings
    */
   export function getDigitIndices(move: number): {
     firstStringIndex: number;
     secondStringIndex: number;
   } {
     // Implementation based on lines 278-306
     // Uses MOVES_INDEXES array and complex conditional logic
   }

   /**
    * Determines if this move should be displayed
    * Some moves are skipped based on hint preference
    * @param move - Current move number
    * @param previousMove - Previous move number (for +4 detection)
    * @param hintsEnabled - Whether hints are enabled
    * @returns Whether to display this hint step
    */
   export function shouldShowHint(
     move: number,
     previousMove: number,
     hintsEnabled: boolean
   ): boolean {
     // Implementation based on line 288: if (move == i + 4 && sharedPreferences.getBoolean(HINT, false)) continue;
   }
   ```

2. Port the complex conditional logic exactly from Android:
   - The `for` loop structure (lines 284-306)
   - The `movesIndexes` array lookups
   - The conditional calculations for `fsIndex` and `ssIndex`

3. Run tests and iterate until all pass:
   - Implement `getMoveRange()` first
   - Then implement `getDigitIndices()`
   - Fix failing tests incrementally

**Verification Checklist:**
- [ ] `getMoveRange()` implemented and passes tests
- [ ] `getDigitIndices()` implemented and passes tests
- [ ] Logic matches Android implementation exactly
- [ ] All tests from Task 2 pass
- [ ] No TypeScript errors

**Testing Instructions:**
- Run `npm test`
- Verify all move tracking tests pass
- Manually compare outputs to Android app for sample problems
- Test edge cases

**Commit Message Template:**
```
feat(utils): implement move tracking logic

- Implemented getMoveRange function
- Implemented getDigitIndices function
- Ported Android setMove and setIndex logic exactly
- All TDD tests now passing
```

**Estimated tokens:** ~4,000

---

### Task 4: Write Tests for Hint Calculation Logic (TDD)

**Goal:** Write tests for the core hint calculation before implementing it.

**Files to Create:**
- `Migration/expo-project/__tests__/utils/hintCalculator.test.ts` - Hint calculation tests

**Prerequisites:**
- Task 3 completed (move tracking working)
- Read `PracticeActivity.java` lines 219-267 (`practiceHint()`)

**Implementation Steps:**

1. Analyze Android's `practiceHint()` logic:
   - Extracts two digits from equation based on indices
   - Multiplies them
   - Determines which digit (units or tens) to use
   - Accumulates the result
   - Formats hint question and result strings

2. Create comprehensive test cases:
   ```typescript
   import { calculateHintStep } from '@utils/hintCalculator';

   describe('Hint Calculator', () => {
     const sampleEquation = '1234 * 567';

     describe('calculateHintStep', () => {
       test('move 0: should multiply correct digits and return units', () => {
         const result = calculateHintStep(
           sampleEquation,
           0,  // move
           0   // current remainder
         );
         expect(result.question).toBe('4 * 7');
         expect(result.digitToAdd).toBe(8); // 28 → units digit
         expect(result.highlightIndices).toEqual([3, 9]);
       });

       test('move 1: should handle tens digit extraction', () => {
         const result = calculateHintStep(
           sampleEquation,
           1,  // move
           0   // current remainder
         );
         // Expected values based on Android logic
       });

       test('should accumulate remainder correctly', () => {
         const result = calculateHintStep(
           sampleEquation,
           0,
           5  // existing remainder
         );
         expect(result.newRemainder).toBe(5 + 8); // 13
       });

       // Add tests for all move types
       // Add tests for moves that use units vs tens digits
       // Add tests for moves at boundaries (0, 3, 8, 14, etc.)
     });
   });
   ```

3. Create test cases based on Android's conditional logic:
   - Moves that use units digit: 0, 1, 3, 4, 6, 8, 9, 11, 13, 16, 18, 21 (line 250)
   - Moves that use tens digit: all others
   - Moves that complete a digit: 0, 3, 8, 14, 19, 22, 23 (line 259)

4. Tests should FAIL initially (no implementation yet)

**Verification Checklist:**
- [ ] Test file created with comprehensive cases
- [ ] Tests cover units digit extraction
- [ ] Tests cover tens digit extraction
- [ ] Tests cover remainder accumulation
- [ ] Tests cover hint formatting
- [ ] Tests cover digit highlighting
- [ ] Tests currently fail

**Testing Instructions:**
- Run `npm test` - should fail
- Verify test structure matches Android logic
- Review test cases with Android source

**Commit Message Template:**
```
test(hint): write tests for hint calculation logic (TDD)

- Created comprehensive test suite for hint calculation
- Defined expected behavior for calculateHintStep
- Covered units/tens digit extraction cases
- Tests currently fail (no implementation)
```

**Estimated tokens:** ~3,500

---

### Task 5: Implement Hint Calculation Logic

**Goal:** Implement the `practiceHint()` logic to pass tests from Task 4.

**Files to Create:**
- `Migration/expo-project/src/utils/hintCalculator.ts` - Hint calculation implementation

**Prerequisites:**
- Task 4 completed (tests written)
- Read `PracticeActivity.java` lines 219-267

**Implementation Steps:**

1. Create `src/utils/hintCalculator.ts`:
   ```typescript
   import { getDigitIndices } from './hintMoveTracker';

   /**
    * Moves that should use the units digit from multiplication result
    */
   const UNITS_DIGIT_MOVES = [0, 1, 3, 4, 6, 8, 9, 11, 13, 16, 18, 21];

   /**
    * Moves that complete a digit (no "+" suffix)
    */
   const COMPLETE_DIGIT_MOVES = [0, 3, 8, 14, 19, 22, 23];

   /**
    * Calculates a single hint step
    * Ports Android's practiceHint() logic
    * @param equation - The current equation string (e.g., "1234 * 567")
    * @param move - Current move number
    * @param currentRemainder - Accumulated remainder from previous steps
    * @returns Hint step information
    */
   export interface HintStepResult {
     question: string;           // e.g., "3 × 6"
     digitToAdd: number;         // The digit to add to remainder
     newRemainder: number;       // Updated remainder
     resultDisplay: string;      // e.g., "8 + "
     highlightIndices: number[]; // Indices to highlight in equation
   }

   export function calculateHintStep(
     equation: string,
     move: number,
     currentRemainder: number
   ): HintStepResult {
     // 1. Split equation into first and second numbers
     const parts = equation.split(' * ');
     const firstString = parts[0];
     const secondString = parts[2]; // Account for " * " split

     // 2. Get digit indices for this move
     const { firstStringIndex, secondStringIndex } = getDigitIndices(move);

     // 3. Extract the two digits
     const firstChar = firstString[firstStringIndex];
     const secondChar = secondString[secondStringIndex];

     // 4. Create question string
     const question = `${firstChar} * ${secondChar}`;

     // 5. Calculate multiplication result
     const product = parseInt(firstChar, 10) * parseInt(secondChar, 10);
     const productString = product.toString().padStart(2, '0');

     // 6. Determine which digit to use (units or tens)
     const useUnits = UNITS_DIGIT_MOVES.includes(move);
     const digitToAdd = parseInt(
       useUnits ? productString[1] : productString[0],
       10
     );

     // 7. Update remainder
     const newRemainder = currentRemainder + digitToAdd;

     // 8. Format result display
     const isComplete = COMPLETE_DIGIT_MOVES.includes(move);
     const resultDisplay = digitToAdd + (isComplete ? '' : ' + ');

     // 9. Calculate highlight indices (adjust for " * " in equation)
     const highlightIndices = [
       firstStringIndex,
       secondStringIndex + 7, // Offset for " * " (line 237 in Android)
     ];

     return {
       question,
       digitToAdd,
       newRemainder,
       resultDisplay,
       highlightIndices,
     };
   }
   ```

2. Test incrementally:
   - Run tests after each piece of logic
   - Debug failing tests
   - Compare outputs to Android app

3. Validate edge cases:
   - Leading zeros in products (e.g., 1 × 2 = 02)
   - Boundary moves
   - Large remainder values

**Verification Checklist:**
- [ ] `calculateHintStep()` implemented
- [ ] Digit extraction logic correct
- [ ] Units vs tens selection correct
- [ ] Remainder calculation correct
- [ ] Result formatting correct
- [ ] Highlight indices correct
- [ ] All tests from Task 4 pass

**Testing Instructions:**
- Run `npm test`
- Verify all hint calculation tests pass
- Manually test with sample equations from Android
- Compare outputs digit-by-digit

**Commit Message Template:**
```
feat(utils): implement hint calculation logic

- Implemented calculateHintStep function
- Ported Android practiceHint logic exactly
- Correct units/tens digit selection
- Proper remainder accumulation
- All TDD tests passing
```

**Estimated tokens:** ~4,500

---

### Task 6: Integrate Hint System with Store Actions

**Goal:** Wire hint logic to Zustand store actions.

**Files to Modify:**
- `Migration/expo-project/src/store/appStore.ts` - Implement hint actions

**Prerequisites:**
- Tasks 3 and 5 completed (hint utilities ready)

**Implementation Steps:**

1. Implement `nextHint` action:
   ```typescript
   import { getDigitIndices, getMoveRange } from '@utils/hintMoveTracker';
   import { calculateHintStep } from '@utils/hintCalculator';

   nextHint: () => {
     const state = get();

     // Check if we've reached moveCount
     if (state.move >= state.moveCount) {
       return; // No more hints for this digit
     }

     // Calculate hint for current move
     const hintStep = calculateHintStep(
       state.currentEquation,
       state.move,
       state.remainderHint
     );

     // Update state
     set({
       move: state.move + 1,
       remainderHint: hintStep.newRemainder,
       hintQuestion: hintStep.question,
       hintResult: state.hintResult + hintStep.resultDisplay,
       hintHighlightIndices: hintStep.highlightIndices,
     });
   },
   ```

2. Update `generateNewProblem` to initialize hint state:
   ```typescript
   generateNewProblem: () => {
     // ... existing logic ...

     // Initialize hint state for first digit
     const { startMove, moveCount } = getMoveRange(0);

     set({
       // ... existing state ...
       move: startMove,
       moveCount: moveCount,
       remainderHint: 0,
       hintQuestion: '',
       hintResult: '',
       hintHighlightIndices: [],
     });
   },
   ```

3. Update `submitAnswer` to handle hint state progression:
   ```typescript
   submitAnswer: (buttonIndex: number) => {
     // ... existing validation ...

     if (result.isCorrect) {
       // Get carry digit for next position
       const carryDigit = Math.floor(state.remainderHint / 10);

       // Reset hints for next digit
       const { startMove, moveCount } = getMoveRange(result.newIndexCount);

       set({
         // ... existing state ...
         move: startMove,
         moveCount: moveCount,
         remainderHint: carryDigit,
         hintQuestion: '',
         hintResult: carryDigit > 0 ? `${carryDigit} + ` : '',
         hintHighlightIndices: [],
         firstCharRemainder: carryDigit,
       });
     }

     // ... rest of logic ...
   },
   ```

4. Implement `resetHints` action:
   ```typescript
   resetHints: () => {
     set({
       move: 0,
       moveCount: 0,
       remainderHint: 0,
       hintQuestion: '',
       hintResult: '',
       hintHighlightIndices: [],
     });
   },
   ```

**Verification Checklist:**
- [ ] `nextHint` action implemented
- [ ] Hint state initializes with new problems
- [ ] Hint state resets with new digits
- [ ] Carry calculation works correctly
- [ ] All actions compile without errors

**Testing Instructions:**
- Test hint progression manually:
  ```typescript
  const store = useAppStore.getState();
  store.generateNewProblem();
  console.log(store.hintQuestion); // Should show first hint
  store.nextHint();
  console.log(store.hintQuestion); // Should show next hint
  ```
- Verify hint state resets on new digit
- Test carry propagation

**Commit Message Template:**
```
feat(store): integrate hint system with store actions

- Implemented nextHint action
- Updated generateNewProblem to initialize hints
- Updated submitAnswer to handle hint progression
- Implemented resetHints action
- Added carry/remainder handling
```

**Estimated tokens:** ~4,000

---

### Task 7: Add Hint UI to Practice Screen

**Goal:** Add hint display components to the Practice screen and wire them to the store.

**Files to Create:**
- `Migration/expo-project/src/components/HintDisplay.tsx` - Hint display component

**Files to Modify:**
- `Migration/expo-project/src/screens/PracticeScreen.tsx` - Add hint UI

**Prerequisites:**
- Task 6 completed (hint actions in store)
- Phase 2 Task 2 completed (HighlightedText component available)

**Implementation Steps:**

1. Create `src/components/HintDisplay.tsx`:
   ```typescript
   import React from 'react';
   import { View, StyleSheet, TouchableOpacity } from 'react-native';
   import { Text, Surface } from 'react-native-paper';
   import { COLORS, SPACING } from '@theme/constants';

   interface HintDisplayProps {
     question: string;
     result: string;
     visible: boolean;
     onPress: () => void;
   }

   export const HintDisplay: React.FC<HintDisplayProps> = React.memo(
     ({ question, result, visible, onPress }) => {
       if (!visible) return null;

       return (
         <TouchableOpacity onPress={onPress} activeOpacity={0.7}>
           <Surface style={styles.container}>
             <Text variant="titleMedium" style={styles.question}>
               {question || 'Touch to see hint'}
             </Text>
             <Text variant="bodyLarge" style={styles.result}>
               {result}
             </Text>
           </Surface>
         </TouchableOpacity>
       );
     }
   );

   const styles = StyleSheet.create({
     container: {
       padding: SPACING.md,
       margin: SPACING.sm,
       borderRadius: 8,
     },
     question: {
       color: COLORS.primary,
       marginBottom: SPACING.xs,
     },
     result: {
       color: COLORS.text,
     },
   });
   ```

2. Update `PracticeScreen.tsx`:
   ```typescript
   import { HighlightedText } from '@components/HighlightedText';
   import { HintDisplay } from '@components/HintDisplay';

   export const PracticeScreen: React.FC = () => {
     const {
       currentEquation,
       answerProgress,
       answerChoices,
       hintsEnabled,
       hintQuestion,
       hintResult,
       hintHighlightIndices,
       move,
       moveCount,
       generateNewProblem,
       submitAnswer,
       nextHint,
     } = useAppStore();

     // ... existing code ...

     const handleHintPress = () => {
       if (move < moveCount) {
         nextHint();
       }
     };

     return (
       <View style={styles.container}>
         <Surface style={styles.equationSurface}>
           {hintsEnabled && hintHighlightIndices.length > 0 ? (
             <HighlightedText
               text={currentEquation}
               highlightIndices={hintHighlightIndices}
               highlightColor={COLORS.accent}
               style={styles.equation}
             />
           ) : (
             <Text variant="headlineLarge" style={styles.equation}>
               {currentEquation}
             </Text>
           )}
         </Surface>

         <HintDisplay
           question={hintQuestion}
           result={hintResult}
           visible={hintsEnabled}
           onPress={handleHintPress}
         />

         {/* Rest of UI */}
       </View>
     );
   };
   ```

3. Add hint help toast (first time user clicks hint):
   - Import `Toast` from Paper or use React Native `ToastAndroid`/`Alert`
   - Show "Touch hint to get next step" on first hint click
   - Store in `hintHelpShown` state

**Verification Checklist:**
- [ ] HintDisplay component created
- [ ] Hint UI integrated into Practice screen
- [ ] Equation highlights when hints are shown
- [ ] Clicking hint area advances to next hint
- [ ] Hint display only visible when hints enabled
- [ ] First-time help message shown

**Testing Instructions:**
- Enable hints in Settings
- Generate a problem
- Click hint area and verify it advances
- Verify equation highlights correct digits
- Verify hint question and result display
- Disable hints and verify hint UI hidden

**Commit Message Template:**
```
feat(ui): add hint display to Practice screen

- Created HintDisplay component
- Integrated hint UI into Practice screen
- Added equation highlighting for hints
- Implemented hint advancement on click
- Added first-time help message
```

**Estimated tokens:** ~4,000

---

### Task 8: Enforce Hint Requirement Before Answer

**Goal:** Prevent users from answering until they've viewed all hints (when hints are enabled).

**Files to Modify:**
- `Migration/expo-project/src/screens/PracticeScreen.tsx` - Add hint enforcement
- `Migration/expo-project/src/components/AnswerButton.tsx` - Add disabled state styling

**Prerequisites:**
- Task 7 completed (hint UI added)
- Read `PracticeActivity.java` lines 347-351 (hint enforcement logic)

**Implementation Steps:**

1. Add validation in `handleAnswerPress`:
   ```typescript
   const handleAnswerPress = (buttonIndex: number) => {
     const state = useAppStore.getState();

     // Enforce hint viewing when hints enabled
     if (state.hintsEnabled && state.move < 9) {
       // Show toast message
       Alert.alert(
         'View Hints',
         'Touch the Hint to Receive More Hints',
         [{ text: 'OK' }]
       );
       return;
     }

     // Proceed with answer submission
     const result = submitAnswer(buttonIndex);
     // ... rest of logic
   };
   ```

2. Alternative: disable buttons until hints viewed:
   ```typescript
   const areButtonsDisabled = hintsEnabled && move < 9;

   <AnswerButton
     value={answerChoices[0]}
     onPress={() => handleAnswerPress(0)}
     disabled={areButtonsDisabled}
   />
   ```

3. Update AnswerButton to show disabled state:
   ```typescript
   // In AnswerButton.tsx
   <Button
     mode="contained"
     onPress={onPress}
     disabled={disabled}
     style={[styles.button, disabled && styles.buttonDisabled]}
     labelStyle={styles.label}
   >
     {value}
   </Button>
   ```

**Verification Checklist:**
- [ ] Hint enforcement logic implemented
- [ ] Users cannot answer before viewing hints (when enabled)
- [ ] Clear feedback when user tries to answer prematurely
- [ ] Buttons disabled or alert shown
- [ ] Enforcement only applies when hints enabled

**Testing Instructions:**
- Enable hints
- Generate problem
- Try clicking answer button immediately (should block)
- View hints (click 9 times)
- Try answering (should now work)
- Disable hints and verify buttons always work

**Commit Message Template:**
```
feat(practice): enforce hint viewing before answering

- Added hint requirement validation
- Disabled answer buttons until hints viewed
- Added user feedback for premature answers
- Matches Android hint enforcement logic
```

**Estimated tokens:** ~2,500

---

### Task 9: Write Comprehensive Integration Tests for Hint System

**Goal:** Create end-to-end tests for the complete hint flow.

**Files to Create:**
- `Migration/expo-project/__tests__/integration/hintSystem.test.ts` - Integration tests

**Prerequisites:**
- All previous tasks completed

**Implementation Steps:**

1. Write integration tests:
   ```typescript
   import { useAppStore } from '@store/appStore';
   import { act } from '@testing-library/react-hooks';

   describe('Hint System Integration', () => {
     beforeEach(() => {
       // Reset store
       useAppStore.setState({
         hintsEnabled: true,
         // ... reset state
       });
     });

     test('complete hint flow for first digit', () => {
       const store = useAppStore.getState();

       // Generate problem
       act(() => {
         store.generateNewProblem();
       });

       // Verify initial hint state
       expect(store.move).toBe(0);
       expect(store.hintQuestion).toBe('');

       // Advance through all hints for first digit
       for (let i = 0; i < store.moveCount; i++) {
         act(() => {
           store.nextHint();
         });
         expect(store.hintQuestion).toBeTruthy();
       }

       // Submit correct answer
       act(() => {
         const result = store.submitAnswer(store.correctAnswerIndex);
         expect(result.isCorrect).toBe(true);
       });

       // Verify hint state reset for next digit
       expect(store.move).toBeGreaterThan(0);
       expect(store.hintResult).toContain('+'); // Carry
     });

     test('hint calculation matches expected Trachtenberg algorithm', () => {
       // Test with known equation
       const store = useAppStore.getState();

       // Set up specific problem with known answer
       // Step through hints and verify each calculation
     });

     test('carry propagation across digits', () => {
       // Test that carries from one digit affect the next
     });
   });
   ```

2. Create validation tests against Android:
   - Use same input equations as Android
   - Verify hint outputs match Android exactly
   - Test edge cases (numbers with zeros, max values)

**Verification Checklist:**
- [ ] Integration tests cover complete hint flow
- [ ] Tests validate against known good outputs
- [ ] Carry propagation tested
- [ ] Edge cases covered
- [ ] All tests pass

**Testing Instructions:**
- Run `npm test`
- Verify all integration tests pass
- Compare outputs to Android app manually
- Test with various problem types

**Commit Message Template:**
```
test(hint): add comprehensive hint system integration tests

- Created end-to-end hint flow tests
- Validated against Android app outputs
- Tested carry propagation
- Tested edge cases
- All tests passing
```

**Estimated tokens:** ~3,000

---

## Phase Verification

Before proceeding to Phase 5, ensure:

- [ ] Hint system calculates correct steps for Trachtenberg algorithm
- [ ] Move tracking works correctly (determines which digits to multiply)
- [ ] Digit highlighting shows correct positions in equation
- [ ] Remainder/carry calculation works across digits
- [ ] Hint progression advances step-by-step
- [ ] Hint requirement enforced (must view hints before answering)
- [ ] Hint visibility controlled by settings toggle
- [ ] All tests pass (unit and integration)
- [ ] Hint output matches Android app exactly for sample problems
- [ ] No TypeScript errors

**Known Limitations:**
- Hint help message uses alert/toast (not custom UI)
- Animation for hint display not yet implemented (Phase 7)

**Integration Points:**
- Phase 5 will add Settings screen with hint toggle
- Phase 7 will add fade animations for hint appearance

---

**Next Phase:** [Phase 5: Settings & State Management](./Phase-5.md)
