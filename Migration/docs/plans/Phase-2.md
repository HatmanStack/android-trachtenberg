# Phase 2: Tutorial System Migration

## Phase Goal

Migrate the complete tutorial system from the Android app to React Native. This includes converting the XML string arrays to TypeScript constants, implementing the tutorial navigation logic, creating the text highlighting component for digit emphasis, and building a fully functional Learn screen that teaches the Trachtenberg multiplication method across 17 interactive steps.

**Success Criteria:**
- Tutorial content accurately migrated from `array.xml` to TypeScript
- HighlightedText component renders text with specific character highlighting
- Learn screen displays tutorial content with proper formatting
- Navigation between tutorial steps works (Next/Back buttons)
- Text highlighting matches Android app behavior (highlights specific digits)
- Tutorial progresses from step 0 to 16, then navigates to Practice screen
- Responsive layout works on web and mobile

**Estimated tokens:** ~20,000

---

## Prerequisites

- Phase 1 completed (infrastructure set up)
- Access to Android source files:
  - `../app/src/main/res/values/array.xml` (tutorial content)
  - `../app/src/main/java/portfolio/trachtenberg/LearnActivity.java` (tutorial logic)
- Understanding of SpannableString highlighting → React Native Text conversion
- Zustand store initialized with `tutorialPage` state

---

## Tasks

### Task 1: Migrate Tutorial Content to TypeScript

**Goal:** Convert the three string arrays from `array.xml` to TypeScript constant arrays with proper typing.

**Files to Create:**
- `Migration/expo-project/src/data/tutorialContent.ts` - Tutorial content constants

**Prerequisites:**
- Phase 1 Task 4 completed (folder structure created)
- Read `../app/src/main/res/values/array.xml`

**Implementation Steps:**

1. Read the tutorial content from `../app/src/main/res/values/array.xml`:
   - `explanationTextList` (18 items)
   - `answerTextList` (18 items)
   - `bottomArrowList` (18 items)

2. Create `src/data/tutorialContent.ts` with:
   - Import `TutorialStep` interface from `@types`
   - Create constant array `tutorialSteps` with 18 objects (indices 0-17)
   - Each object should have `id`, `explanation`, `answer`, `bottomArrow` properties
   - Preserve exact text from XML, including newlines (`\n`)

3. Ensure proper TypeScript typing:
   ```typescript
   export const tutorialSteps: readonly TutorialStep[] = [
     { id: 0, explanation: "...", answer: "", bottomArrow: "" },
     // ... all 18 steps
   ] as const;
   ```

4. Export helper constants:
   ```typescript
   export const TUTORIAL_EQUATION = "123456 x 789"; // Hardcoded equation
   export const TUTORIAL_STEP_COUNT = tutorialSteps.length;
   ```

**Verification Checklist:**
- [ ] All 18 tutorial steps migrated accurately
- [ ] Text matches exactly (including special characters, newlines)
- [ ] TypeScript types are correct (no `any`)
- [ ] Exported as readonly array to prevent mutations
- [ ] Helper constants exported

**Testing Instructions:**
- Import `tutorialSteps` in a test file
- Verify `tutorialSteps.length === 18`
- Spot-check several steps against the XML source
- Verify TypeScript compilation succeeds
- Test import in a component: `import { tutorialSteps } from '@data/tutorialContent';`

**Commit Message Template:**
```
feat(data): migrate tutorial content from Android

- Converted XML string arrays to TypeScript constants
- Migrated all 18 tutorial steps with exact text
- Added tutorial constants and helper values
- Implemented type-safe readonly array
```

**Estimated tokens:** ~2,500

---

### Task 2: Create HighlightedText Component

**Goal:** Build a reusable component that renders text with specific character positions highlighted in a different color (replicating Android's SpannableString behavior).

**Files to Create:**
- `Migration/expo-project/src/components/HighlightedText.tsx` - Highlighted text component
- `Migration/expo-project/src/utils/textHighlighter.ts` - Text highlighting utility functions

**Prerequisites:**
- Phase 1 Task 5 completed (theme configured)
- Understanding of React Native Text component nesting

**Implementation Steps:**

1. Create `src/utils/textHighlighter.ts` with helper functions:
   ```typescript
   /**
    * Splits text into segments with highlight information
    * @param text - The full text to process
    * @param highlightIndices - Array of character indices to highlight
    * @returns Array of segments with text and isHighlighted flag
    */
   export interface TextSegment {
     text: string;
     isHighlighted: boolean;
   }

   export function segmentText(
     text: string,
     highlightIndices: number[]
   ): TextSegment[] {
     // Implementation: split text into characters, mark highlighted ones
   }

   /**
    * Finds character indices in a string
    * @param text - The text to search
    * @param targets - Characters to find
    * @returns Array of indices where targets appear
    */
   export function findCharIndices(
     text: string,
     targets: string[]
   ): number[] {
     // Implementation: return indices of target characters
   }
   ```

2. Create `src/components/HighlightedText.tsx`:
   - Accept props: `text`, `highlightIndices`, `highlightColor`, `style`
   - Use `segmentText()` utility to split text
   - Render nested `<Text>` components
   - Apply highlight color to marked segments
   - Support multiline text

   ```typescript
   import React from 'react';
   import { Text, TextStyle, StyleProp } from 'react-native';
   import { segmentText } from '@utils/textHighlighter';

   interface HighlightedTextProps {
     text: string;
     highlightIndices?: number[];
     highlightColor: string;
     style?: StyleProp<TextStyle>;
   }

   export const HighlightedText: React.FC<HighlightedTextProps> = ({
     text,
     highlightIndices = [],
     highlightColor,
     style,
   }) => {
     // Implementation
   };
   ```

3. Optimize performance:
   - Use `React.memo()` to prevent unnecessary re-renders
   - Only recalculate segments when text or indices change

**Verification Checklist:**
- [ ] `textHighlighter.ts` utilities created and exported
- [ ] `HighlightedText` component created
- [ ] Component accepts all required props
- [ ] Highlighting works for single and multiple characters
- [ ] Works with multiline text
- [ ] Component is memoized for performance
- [ ] TypeScript types are complete

**Testing Instructions:**
- Create a test rendering with sample text and highlight indices
- Verify correct characters are highlighted
- Test with edge cases:
  - Empty highlightIndices (no highlighting)
  - Out-of-bounds indices (should be ignored)
  - Multiline text with newlines
- Visual test: Render "123456 x 789" with indices [2, 9] highlighted
  - Should highlight '3' and '9'

**Commit Message Template:**
```
feat(components): create HighlightedText component

- Implemented text highlighting utility functions
- Created reusable HighlightedText component
- Supports arbitrary character index highlighting
- Optimized with React.memo
```

**Estimated tokens:** ~3,000

---

### Task 3: Implement Tutorial Navigation Logic

**Goal:** Create custom hooks and utility functions to manage tutorial page navigation, matching the Android `learnPage` and `setLearnSteep()` logic.

**Files to Create:**
- `Migration/expo-project/src/hooks/useTutorialNavigation.ts` - Tutorial navigation hook

**Prerequisites:**
- Task 1 completed (tutorial content migrated)
- Zustand store with `tutorialPage` state

**Implementation Steps:**

1. Create `src/hooks/useTutorialNavigation.ts`:
   ```typescript
   import { useAppStore } from '@store/appStore';
   import { TUTORIAL_STEP_COUNT } from '@data/tutorialContent';

   export const useTutorialNavigation = () => {
     const tutorialPage = useAppStore((state) => state.tutorialPage);
     const setTutorialPage = useAppStore((state) => state.setTutorialPage);

     const goNext = () => {
       if (tutorialPage < TUTORIAL_STEP_COUNT - 1) {
         setTutorialPage(tutorialPage + 1);
       }
     };

     const goPrevious = () => {
       if (tutorialPage > 0) {
         setTutorialPage(tutorialPage - 1);
       }
     };

     const isFirstPage = tutorialPage === 0;
     const isLastPage = tutorialPage === TUTORIAL_STEP_COUNT - 1;

     return {
       currentPage: tutorialPage,
       goNext,
       goPrevious,
       isFirstPage,
       isLastPage,
     };
   };
   ```

2. Export hook for use in Learn screen

**Verification Checklist:**
- [ ] Hook created and exports navigation functions
- [ ] `goNext()` increments page (max: TUTORIAL_STEP_COUNT - 1)
- [ ] `goPrevious()` decrements page (min: 0)
- [ ] `isFirstPage` and `isLastPage` computed correctly
- [ ] Hook uses Zustand store for state

**Testing Instructions:**
- Write unit tests for the hook using `@testing-library/react-hooks`
- Test boundary conditions (first page, last page)
- Test that state updates when navigation functions are called
- Verify navigation doesn't go out of bounds

**Commit Message Template:**
```
feat(hooks): implement tutorial navigation hook

- Created useTutorialNavigation custom hook
- Implemented goNext and goPrevious functions
- Added boundary checks for first/last page
- Integrated with Zustand store
```

**Estimated tokens:** ~1,500

---

### Task 4: Implement Digit Highlighting Logic for Tutorial

**Goal:** Replicate the Android `setAccentColor()` method that highlights specific digits in the equation "123456 x 789" based on the tutorial step.

**Files to Create:**
- `Migration/expo-project/src/utils/tutorialHighlighter.ts` - Tutorial-specific highlighting logic

**Prerequisites:**
- Task 1 completed (tutorial content available)
- Task 2 completed (HighlightedText component created)
- Read `LearnActivity.java` lines 160-172 (`setAccentColor()` method)

**Implementation Steps:**

1. Analyze the Android `setAccentColor()` method:
   - It parses the `answerText` to extract which digits to highlight
   - Format: "... of X and Y" where X and Y are digits to highlight
   - Example: "6 of 6 and 9" means highlight '6' (first occurrence) and '9'

2. Create `src/utils/tutorialHighlighter.ts`:
   ```typescript
   import { TUTORIAL_EQUATION } from '@data/tutorialContent';

   /**
    * Extracts highlight indices from tutorial answer text
    * Based on Android's setAccentColor() logic
    * @param answerText - The answer text from tutorial step
    * @returns Array of character indices to highlight in the equation
    */
   export function getTutorialHighlightIndices(answerText: string): number[] {
     if (!answerText || answerText === "") {
       return [];
     }

     // Parse "... of X and Y" format
     // Extract the two digits mentioned
     // Find their indices in TUTORIAL_EQUATION
     // Return array of indices

     // Implementation should match Java logic:
     // String[] numberString = answerString.split(" of ");
     // String letterString = numberString[1].substring(0, 1);
     // String second = numberString[1].substring(4, 5);
     // Find indexOf(letterString) and indexOf(second) in "123456 x 789"
   }
   ```

3. Handle edge cases:
   - Empty answer text (return empty array)
   - Answer text without " of " (return empty array)
   - Find first occurrence of each digit in equation string

**Verification Checklist:**
- [ ] Function created and exported
- [ ] Parsing logic matches Android implementation
- [ ] Returns correct indices for sample tutorial steps
- [ ] Handles edge cases (empty text, malformed text)
- [ ] TypeScript types complete

**Testing Instructions:**
- Write unit tests with sample answer texts from tutorial:
  - Input: "6 of 6 and 9" → Output: [5, 11] (indices in "123456 x 789")
  - Input: "5 of 5 and 6" → Output: [4, 5]
  - Input: "" → Output: []
- Verify indices point to correct characters in "123456 x 789"
- Test edge cases

**Commit Message Template:**
```
feat(utils): implement tutorial digit highlighting logic

- Created getTutorialHighlightIndices function
- Ported Android setAccentColor() logic to TypeScript
- Parses tutorial answer text to extract highlight positions
- Added unit tests for highlighting logic
```

**Estimated tokens:** ~2,000

---

### Task 5: Build Learn Screen UI

**Goal:** Implement the complete Learn screen with tutorial content display, navigation buttons, and text highlighting.

**Files to Modify:**
- `Migration/expo-project/src/screens/LearnScreen.tsx` - Replace placeholder with full implementation

**Prerequisites:**
- All previous tasks in Phase 2 completed
- React Native Paper components available
- Navigation set up from Phase 1

**Implementation Steps:**

1. Design screen layout matching Android `activity_main.xml`:
   - Top: Explanation text (large area)
   - Middle: Equation display (with highlighting)
   - Middle: Answer text (calculation steps)
   - Middle: Bottom arrow (answer progression)
   - Bottom: Next and Back buttons (left and right)

2. Implement `LearnScreen.tsx`:
   ```typescript
   import React from 'react';
   import { View, ScrollView, StyleSheet } from 'react-native';
   import { Text, Button, Surface } from 'react-native-paper';
   import { useTutorialNavigation } from '@hooks/useTutorialNavigation';
   import { tutorialSteps, TUTORIAL_EQUATION } from '@data/tutorialContent';
   import { getTutorialHighlightIndices } from '@utils/tutorialHighlighter';
   import { HighlightedText } from '@components/HighlightedText';
   import { useNavigation } from '@react-navigation/native';
   import { COLORS } from '@theme/constants';

   export const LearnScreen: React.FC = () => {
     const { currentPage, goNext, goPrevious, isFirstPage, isLastPage } =
       useTutorialNavigation();
     const navigation = useNavigation();

     const currentStep = tutorialSteps[currentPage];
     const highlightIndices = getTutorialHighlightIndices(currentStep.answer);

     const handleNext = () => {
       if (isLastPage) {
         // Navigate to Practice screen (like Android does)
         navigation.navigate('Practice');
       } else {
         goNext();
       }
     };

     return (
       // Implementation
     );
   };
   ```

3. Layout structure:
   - Use `ScrollView` for content (handles long explanation texts)
   - Use Paper `Surface` for elevated content sections
   - Use `HighlightedText` for equation display
   - Conditionally render equation/answer only when not empty
   - Style buttons to match Android vertical text (or adapt to modern design)

4. Handle navigation to Practice screen:
   - When user clicks Next on the last page (page 16), navigate to Practice
   - Match Android behavior in `setLearnSteep()` lines 152-156

5. Accessibility:
   - Add accessibility labels to buttons
   - Ensure text is readable (proper contrast, font sizes)

**Verification Checklist:**
- [ ] Screen displays current tutorial step content
- [ ] Explanation, answer, and bottom arrow render correctly
- [ ] Equation displays with proper highlighting when answer is not empty
- [ ] Next button advances to next step
- [ ] Back button goes to previous step
- [ ] Back button disabled/hidden on first page
- [ ] Next button navigates to Practice screen on last page
- [ ] Layout is responsive on different screen sizes
- [ ] Matches Android app's information hierarchy

**Testing Instructions:**
- Navigate through all 17 tutorial steps manually
- Verify content matches Android app for each step
- Test highlighting on steps with answer text
- Verify first step has no Back button or it's disabled
- Verify last step's Next button goes to Practice screen
- Test on web and mobile
- Test on different screen sizes (phone, tablet, desktop)

**Commit Message Template:**
```
feat(screens): implement Learn screen with tutorial

- Built complete tutorial UI with navigation
- Integrated HighlightedText for equation display
- Implemented page navigation with Next/Back buttons
- Added automatic navigation to Practice on completion
- Responsive layout for all screen sizes
```

**Estimated tokens:** ~5,000

---

### Task 6: Style Learn Screen to Match Design

**Goal:** Apply styling to make the Learn screen visually polished and consistent with React Native Paper theme.

**Files to Create:**
- `Migration/expo-project/src/screens/LearnScreen.styles.ts` - Screen-specific styles (optional, can be inline)

**Prerequisites:**
- Task 5 completed (Learn screen UI built)
- Paper theme configured

**Implementation Steps:**

1. Create styles using React Native StyleSheet:
   - Container styles (padding, spacing)
   - Text styles (font sizes, colors, alignment)
   - Button styles (width, height, positioning)
   - Equation display (center alignment, larger font)

2. Apply Material Design principles:
   - Use Paper's theme colors (primary, accent)
   - Proper spacing (use SPACING constants from theme)
   - Elevation for surfaces
   - Typography scale (use FONT_SIZES constants)

3. Responsive design:
   - Use flex layouts
   - Adjust font sizes for different screen sizes if needed
   - Ensure buttons are easily tappable (min 44x44 touch target)

4. Match Android visual hierarchy:
   - Explanation: larger text area at top
   - Equation: centered, prominent
   - Answer/Bottom Arrow: Medium emphasis
   - Buttons: Bottom corners (or modern button row)

**Verification Checklist:**
- [ ] Styles applied consistently
- [ ] Uses theme colors and constants
- [ ] Proper spacing and padding
- [ ] Readable typography
- [ ] Buttons meet minimum touch target size
- [ ] Visual hierarchy clear
- [ ] Looks polished on web and mobile

**Testing Instructions:**
- Visual review on multiple devices/sizes
- Compare to Android app screenshots
- Verify theme colors are used correctly
- Test touch targets on mobile (buttons easily tappable)

**Commit Message Template:**
```
style(screens): style Learn screen with Material Design

- Applied consistent spacing and typography
- Used theme colors and constants
- Implemented responsive layout
- Ensured accessibility (touch targets, contrast)
```

**Estimated tokens:** ~2,000

---

### Task 7: Add Tutorial State Persistence

**Goal:** Ensure tutorial progress (current page) is maintained across app reloads if desired, or resets appropriately.

**Files to Modify:**
- `Migration/expo-project/src/store/appStore.ts` - Update persistence configuration

**Prerequisites:**
- Task 3 completed (tutorial navigation implemented)
- Zustand store configured

**Implementation Steps:**

1. Decide on persistence strategy:
   - **Option A**: Do NOT persist tutorial page (always start at page 0) - matches Android behavior
   - **Option B**: Persist tutorial page (resume where left off)

2. Based on decision, update `partialize` function in store middleware:
   - If Option A: Ensure `tutorialPage` is NOT in partialize function (already correct)
   - If Option B: Add `tutorialPage` to persisted state

3. Add reset function to store (useful for Settings screen):
   ```typescript
   resetTutorial: () => set({ tutorialPage: 0 }),
   ```

**Verification Checklist:**
- [ ] Tutorial page persistence behavior defined and implemented
- [ ] Behavior matches Android app (resets on app restart)
- [ ] Reset function available if needed

**Testing Instructions:**
- Navigate to tutorial page 5
- Close and reopen app
- Verify page resets to 0 (or persists to 5 if Option B chosen)
- Test reset function if implemented

**Commit Message Template:**
```
feat(store): configure tutorial state persistence

- Defined tutorial page persistence strategy
- Ensured state resets on app restart (matching Android)
- Added tutorial reset function to store
```

**Estimated tokens:** ~800

---

### Task 8: Write Tests for Tutorial System

**Goal:** Create unit and integration tests for tutorial components and logic.

**Files to Create:**
- `Migration/expo-project/__tests__/utils/tutorialHighlighter.test.ts` - Highlight logic tests
- `Migration/expo-project/__tests__/components/HighlightedText.test.tsx` - Component tests
- `Migration/expo-project/__tests__/hooks/useTutorialNavigation.test.ts` - Hook tests
- `Migration/expo-project/__tests__/screens/LearnScreen.test.tsx` - Screen integration tests

**Prerequisites:**
- All previous tasks completed
- Testing libraries installed (Phase 1)

**Implementation Steps:**

1. Write tests for `tutorialHighlighter.ts`:
   - Test parsing of tutorial answer texts
   - Test index calculation for different digit pairs
   - Test edge cases (empty text, malformed text)

2. Write tests for `HighlightedText` component:
   - Test rendering with no highlights
   - Test rendering with single highlight
   - Test rendering with multiple highlights
   - Snapshot test for visual regression

3. Write tests for `useTutorialNavigation` hook:
   - Test initial state
   - Test goNext function
   - Test goPrevious function
   - Test boundary conditions (first/last page)

4. Write tests for `LearnScreen`:
   - Test rendering first page
   - Test navigation between pages
   - Test navigation to Practice screen on last page
   - Test Back button disabled on first page

**Verification Checklist:**
- [ ] All test files created
- [ ] Unit tests cover edge cases
- [ ] Integration tests cover user flows
- [ ] All tests pass (`npm test`)
- [ ] Test coverage adequate (aim for 80%+)

**Testing Instructions:**
- Run `npm test` or `yarn test`
- Verify all tests pass
- Check coverage report
- Fix any failing tests

**Commit Message Template:**
```
test(tutorial): add comprehensive tutorial system tests

- Added unit tests for highlight logic
- Added component tests for HighlightedText
- Added hook tests for useTutorialNavigation
- Added integration tests for LearnScreen
```

**Estimated tokens:** ~3,000

---

## Phase Verification

Before proceeding to Phase 3, ensure:

- [ ] Tutorial content accurately migrated from Android XML
- [ ] HighlightedText component works correctly
- [ ] Tutorial navigation functions properly (Next/Back)
- [ ] Digit highlighting matches Android behavior
- [ ] Learn screen displays all 17 tutorial steps correctly
- [ ] Navigation to Practice screen works on final step
- [ ] Styling is polished and responsive
- [ ] Tests pass for all tutorial components
- [ ] No TypeScript errors
- [ ] App runs on web, iOS, and Android

**Known Limitations:**
- Tutorial uses hardcoded equation ("123456 x 789")
- No dynamic content loading (content is in code)
- Practice screen still a placeholder (implemented in Phase 3)

**Integration Points:**
- Phase 3 will implement Practice screen that tutorial navigates to
- Phase 5 will potentially add a "Reset Tutorial" option in Settings
- Phase 7 will add page transition animations

---

## Review Feedback (Iteration 1)

### Overall Phase 2 Implementation

**Excellent work - implementation is comprehensive**

> **Positive findings:**
> - Tutorial content migrated accurately: All 18 steps present with correct text ✓
> - HighlightedText component implemented with proper logic ✓
> - Learn screen fully functional with navigation ✓
> - Tutorial highlighting logic correctly ported from Android ✓
> - Navigation to Practice screen works on completion ✓

### Tests Cannot Run (Inherited from Phase 1)

**Blocked by Phase 1 Jest configuration issue**

> **Consider:** Looking at your test files in `__tests__/`, you've written comprehensive tests for tutorial components. But can you actually run `npm test` successfully?
>
> **Think about:** The Phase 1 Jest configuration issue (missing `setupFilesAfterEnv`) is preventing ALL tests from running, including your Phase 2 tests. Should Phase 1 be fully fixed before considering Phase 2 complete?
>
> **Reflect:** Your tutorial tests in `__tests__/utils/tutorialHighlighter.test.ts` and `__tests__/components/HighlightedText.test.tsx` look well-written. But how can you verify they pass if Jest is misconfigured?

### Equation Symbol Enhancement

**Positive deviation from Android**

> **Observation:** The Android app uses `"123456 x 789"` (lowercase x) but your implementation uses `"123456 × 789"` (proper multiplication symbol ×). This is visible in:
>   - `src/data/tutorialContent.ts` line 5: `TUTORIAL_EQUATION = "123456 × 789"`
>   - `src/utils/problemGenerator.ts` line 42: Uses `×` in formatEquation
>
> **Think about:** Is this a deliberate improvement for better mathematical notation? The × symbol is more correct than x.
>
> **Reflect:** The "Known Limitations" section still mentions "123456 x 789" - should this be updated to reflect the actual implementation?

### Task 7: Tutorial State Persistence

**Small discrepancy in specification**

> **Consider:** Looking at Phase 2 Task 7 (lines 456-479), it discusses tutorial page persistence. The plan recommends "Option A: Do NOT persist tutorial page (always start at page 0)".
>
> **Think about:** In your `src/store/appStore.ts`, is `tutorialPage` included in the `partialize` function? What does this mean for persistence behavior?
>
> **Reflect:** Does the current implementation match the recommended Option A from the plan?

### Documentation Update Needed

**Known Limitations section**

> **Consider:** The "Known Limitations" section (line 673) says "Tutorial uses hardcoded equation ("123456 x 789")". But your actual implementation uses the proper multiplication symbol ×.
>
> **Think about:** Should this documentation be updated to match the implementation?

---

**Next Phase:** [Phase 3: Practice Mode & Quiz Logic](./Phase-3.md)
