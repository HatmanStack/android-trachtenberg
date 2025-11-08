# Phase 0: Foundation & Architecture

## Phase Goal

Establish the architectural foundation, technical decisions, and shared conventions that will guide all subsequent phases. This phase documents the "why" behind our technology choices and provides patterns that the implementation engineer will follow throughout the migration.

**Success Criteria:**
- Clear architecture decision records (ADRs) for all major technical choices
- Documented code organization and naming conventions
- Defined testing strategy applicable across all phases
- Identified common pitfalls and how to avoid them

**Estimated tokens:** ~5,000

---

## Prerequisites

- Reviewed the original Android (Java) codebase in `../app/src/main/java/portfolio/trachtenberg/`
- Read through tutorial content in `../app/src/main/res/values/array.xml`
- Understanding of the Trachtenberg multiplication algorithm
- Familiarity with React Native and Expo ecosystem

---

## Architecture Decision Records (ADRs)

### ADR-001: UI Component Library - React Native Paper

**Decision:** Use React Native Paper for UI components.

**Rationale:**
- Provides Material Design 3 components out of the box
- Excellent web support (critical for web-first requirement)
- Built-in theming system for consistent styling
- Active maintenance and good documentation
- Lower learning curve for engineers familiar with Material Design
- Components are accessible and follow platform conventions

**Alternatives Considered:**
- NativeBase: Good but heavier bundle size
- Custom components: More control but significant development overhead

**Implications:**
- All UI components should use Paper components when available
- Custom styling should extend Paper's theme system
- Follow Material Design patterns for interactions

---

### ADR-002: State Management - Zustand

**Decision:** Use Zustand for global state management.

**Rationale:**
- Minimal boilerplate compared to Redux
- Excellent TypeScript support
- Small bundle size (~1KB)
- Simple API reduces learning curve
- No provider wrapping needed
- Supports middleware for persistence integration
- Performant with selective re-renders

**Alternatives Considered:**
- Context API: Suitable but can cause performance issues with frequent updates
- Redux Toolkit: Industry standard but overkill for this app's complexity

**State Structure:**
```typescript
interface AppState {
  // Settings
  hintsEnabled: boolean;
  setHintsEnabled: (enabled: boolean) => void;

  // Tutorial state
  tutorialPage: number;
  setTutorialPage: (page: number) => void;

  // Practice state
  currentEquation: string;
  currentAnswer: string;
  answerProgress: string;
  indexCount: number;
  move: number;
  moveCount: number;
  remainderHint: number;
  firstCharRemainder: number;

  // Actions
  generateNewProblem: () => void;
  submitAnswer: (buttonIndex: number) => boolean;
  nextHint: () => void;
  resetPractice: () => void;
}
```

**Implications:**
- Single store file: `src/store/appStore.ts`
- All state mutations go through defined actions
- State persistence handled via middleware connecting to AsyncStorage

---

### ADR-003: Data Persistence - AsyncStorage

**Decision:** Use `@react-native-async-storage/async-storage` for data persistence.

**Rationale:**
- Industry standard for React Native key-value storage
- Works across web, iOS, and Android
- Simple async API
- No encryption needed (only storing user preferences, not sensitive data)
- Integrates cleanly with Zustand via middleware

**Alternatives Considered:**
- expo-secure-store: Unnecessary encryption overhead for non-sensitive data
- expo-sqlite: Overkill for simple key-value storage

**Data to Persist:**
```typescript
{
  "hintsEnabled": boolean,
  "hintHelpShown": boolean,
  "tutorialCompleted": boolean
}
```

**Implications:**
- Initialize AsyncStorage on app startup
- Hydrate Zustand store from AsyncStorage
- Persist state changes automatically via middleware
- Handle storage errors gracefully with fallback to defaults

---

### ADR-004: Navigation Strategy - React Navigation (Hybrid)

**Decision:** Use React Navigation with platform-specific navigation patterns.

**Rationale:**
- Most mature React Native navigation library
- Excellent web support
- Platform-specific navigation possible via conditional rendering
- Deep linking support
- TypeScript support for type-safe navigation

**Navigation Structure:**

**Web (Primary Target):**
```
Tab Navigator (Material Top Tabs)
├── Learn Tab
├── Practice Tab
└── Settings Tab
```

**Mobile (Android/iOS):**
```
Stack Navigator
├── Learn Screen (initial)
├── Practice Screen
└── Settings Screen
```

**Implementation Pattern:**
```typescript
// src/navigation/index.tsx
const Navigation = () => {
  const isWeb = Platform.OS === 'web';

  return isWeb ? <TabNavigator /> : <StackNavigator />;
};
```

**Implications:**
- Two navigator components: `TabNavigator.tsx` and `StackNavigator.tsx`
- Shared screen components work with both navigators
- Platform-specific header configurations

---

### ADR-005: Tutorial Content Storage - TypeScript Constants

**Decision:** Store tutorial content as TypeScript constant arrays exported from a dedicated module.

**Rationale:**
- Simple, no external dependencies
- Type-safe with TypeScript interfaces
- Fast access (no async loading)
- Sufficient for static content that rarely changes
- Easy to version control and review changes
- Matches the simplicity of the original XML resources

**Content Structure:**
```typescript
// src/data/tutorialContent.ts

export interface TutorialStep {
  id: number;
  explanation: string;
  answer: string;
  bottomArrow: string;
}

export const tutorialSteps: TutorialStep[] = [
  {
    id: 0,
    explanation: "The Trachtenberg system is...",
    answer: "",
    bottomArrow: ""
  },
  // ... 17 steps total
];
```

**Implications:**
- Single source file: `src/data/tutorialContent.ts`
- Content changes require code changes and redeployment
- No need for loading states or error handling
- Can easily extend with additional metadata if needed

---

### ADR-006: Text Highlighting - Inline Styled Text Components

**Decision:** Use React Native `<Text>` components with inline style props for digit highlighting.

**Rationale:**
- Built-in, no external dependencies
- Performant for small text spans
- Works identically on web and mobile
- Simple to implement and maintain
- Type-safe with TypeScript

**Implementation Pattern:**
```typescript
const HighlightedText: React.FC<{
  text: string;
  highlightIndices: number[];
  highlightColor: string;
}> = ({ text, highlightIndices, highlightColor }) => {
  return (
    <Text>
      {text.split('').map((char, index) => (
        <Text
          key={index}
          style={highlightIndices.includes(index) ? { color: highlightColor } : {}}
        >
          {char}
        </Text>
      ))}
    </Text>
  );
};
```

**Implications:**
- Create reusable `HighlightedText` component in `src/components/`
- Pass highlight positions as arrays of indices
- Use theme colors for consistency

---

### ADR-007: Build & Deployment Strategy

**Decision:** Use custom build configuration with EAS Build for mobile, manual web export for self-hosting.

**Rationale:**
- **Web**: `expo export:web` provides optimized static build for self-hosting
- **Mobile**: EAS Build handles complex native code compilation and signing
- Flexibility to integrate with existing CI/CD pipelines
- Full control over deployment process

**Build Targets:**
- **Development**: Expo Go for rapid testing
- **Web Production**: Static bundle via `expo export:web`
- **Android Production**: APK/AAB via EAS Build
- **iOS Production**: IPA via EAS Build (requires Apple Developer account)

**Configuration Files:**
- `app.json` / `app.config.js`: Expo configuration
- `eas.json`: EAS Build profiles (development, preview, production)
- `.env` files: Environment-specific variables (if needed)

**Implications:**
- Set up EAS account and configure `eas.json` in Phase 1
- Document web build and deployment steps in Phase 8
- Test on all three platforms before final deployment

---

## Code Organization & Conventions

### Folder Structure

```
Migration/
├── expo-project/                 # Main Expo application
│   ├── src/
│   │   ├── components/           # Reusable UI components
│   │   │   ├── HighlightedText.tsx
│   │   │   ├── AnswerButton.tsx
│   │   │   └── ...
│   │   ├── screens/              # Screen components
│   │   │   ├── LearnScreen.tsx
│   │   │   ├── PracticeScreen.tsx
│   │   │   └── SettingsScreen.tsx
│   │   ├── navigation/           # Navigation setup
│   │   │   ├── index.tsx
│   │   │   ├── TabNavigator.tsx
│   │   │   └── StackNavigator.tsx
│   │   ├── store/                # Zustand store
│   │   │   └── appStore.ts
│   │   ├── utils/                # Utility functions and core logic
│   │   │   ├── trachtenberg.ts   # Core Trachtenberg algorithm
│   │   │   ├── problemGenerator.ts
│   │   │   └── textHighlighter.ts
│   │   ├── data/                 # Static data
│   │   │   └── tutorialContent.ts
│   │   ├── theme/                # Theming
│   │   │   └── paperTheme.ts
│   │   └── types/                # TypeScript types
│   │       └── index.ts
│   ├── __tests__/                # Test files (mirror src structure)
│   │   ├── utils/
│   │   │   └── trachtenberg.test.ts
│   │   └── ...
│   ├── assets/                   # Images, fonts, etc.
│   ├── App.tsx                   # Root component
│   ├── app.json                  # Expo config
│   ├── package.json
│   ├── tsconfig.json
│   └── eas.json                  # EAS Build config
└── docs/
    └── plans/                    # This directory
```

### Naming Conventions

- **Files**: PascalCase for components (`LearnScreen.tsx`), camelCase for utilities (`trachtenberg.ts`)
- **Components**: PascalCase with descriptive names (`HighlightedText`, `AnswerButton`)
- **Functions**: camelCase with verb prefixes (`generateProblem`, `validateAnswer`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_TUTORIAL_PAGES`, `HINT_COLOR`)
- **Interfaces/Types**: PascalCase with descriptive names (`TutorialStep`, `AppState`)

### TypeScript Standards

- **Strict Mode**: Enable strict TypeScript checks in `tsconfig.json`
- **Explicit Types**: Always define return types for functions
- **No `any`**: Avoid `any` type; use `unknown` or proper types
- **Interfaces Over Types**: Prefer `interface` for object shapes, `type` for unions/intersections

### Component Patterns

**Functional Components with Hooks:**
```typescript
import React from 'react';
import { View, Text } from 'react-native';

interface ExampleProps {
  title: string;
  onPress: () => void;
}

export const Example: React.FC<ExampleProps> = ({ title, onPress }) => {
  return (
    <View>
      <Text>{title}</Text>
    </View>
  );
};
```

**State Management:**
```typescript
// Use Zustand hooks
import { useAppStore } from '../store/appStore';

const MyComponent = () => {
  const hintsEnabled = useAppStore((state) => state.hintsEnabled);
  const setHintsEnabled = useAppStore((state) => state.setHintsEnabled);

  // Component logic
};
```

---

## Testing Strategy

### Philosophy

- **Test-Driven Development (TDD)**: Write tests BEFORE implementing complex logic (especially Trachtenberg algorithms)
- **Unit Tests**: Cover all utility functions and core logic
- **Integration Tests**: Test screen interactions and state flow
- **Snapshot Tests**: Validate UI component rendering

### Testing Framework

- **Jest**: Built-in with Expo, no additional setup
- **React Native Testing Library**: For component testing
- **@testing-library/react-hooks**: For testing custom hooks

### Test Coverage Goals

- **Utilities**: 100% coverage (critical algorithms)
- **Components**: 80%+ coverage
- **Screens**: Integration tests for key user flows

### Test File Naming

- Place tests in `__tests__/` directory mirroring `src/` structure
- Name test files: `<filename>.test.ts` or `<ComponentName>.test.tsx`

### Example Test Structure

```typescript
// __tests__/utils/trachtenberg.test.ts

import { generateProblem, validateAnswer, calculateHint } from '../../src/utils/trachtenberg';

describe('Trachtenberg Utility Functions', () => {
  describe('generateProblem', () => {
    it('should generate a 4-digit by 3-digit multiplication problem', () => {
      const problem = generateProblem();
      expect(problem.firstNumber).toBeGreaterThanOrEqual(1000);
      expect(problem.firstNumber).toBeLessThan(10000);
      expect(problem.secondNumber).toBeGreaterThanOrEqual(100);
      expect(problem.secondNumber).toBeLessThan(1000);
    });
  });

  describe('validateAnswer', () => {
    it('should return true for correct answer digit', () => {
      const result = validateAnswer('123', '456', 0, 8);
      expect(result).toBe(true);
    });
  });

  // More tests...
});
```

---

## Common Pitfalls to Avoid

### 1. **Platform-Specific Code Without Checks**

**Problem:** Using web-only or mobile-only APIs without platform detection.

**Solution:** Always use `Platform.OS` checks:
```typescript
import { Platform } from 'react-native';

if (Platform.OS === 'web') {
  // Web-specific code
} else {
  // Mobile code
}
```

### 2. **Mutating State Directly**

**Problem:** Directly modifying Zustand state instead of using actions.

**Solution:** Always use defined actions:
```typescript
// ❌ Wrong
state.hintsEnabled = true;

// ✅ Correct
setHintsEnabled(true);
```

### 3. **Async Storage Race Conditions**

**Problem:** Multiple simultaneous AsyncStorage operations causing inconsistent state.

**Solution:** Use Zustand middleware to queue persistence operations and await AsyncStorage calls properly.

### 4. **Hardcoded Values**

**Problem:** Hardcoding colors, sizes, or logic constants throughout code.

**Solution:** Define constants in theme or dedicated constants file:
```typescript
// src/theme/constants.ts
export const COLORS = {
  primary: '#9fa8da',
  accent: '#ef5350',
};

export const TUTORIAL_STEPS_COUNT = 17;
```

### 5. **Ignoring TypeScript Warnings**

**Problem:** Using `@ts-ignore` to bypass type errors.

**Solution:** Fix the underlying type issue or properly type the code. If absolutely necessary, use `@ts-expect-error` with a comment explaining why.

### 6. **Not Testing Edge Cases**

**Problem:** Only testing happy paths in the Trachtenberg algorithm.

**Solution:** Write tests for:
- Numbers with trailing zeros (e.g., 1000 × 100)
- Maximum values (9999 × 999)
- Carry operations across all digit positions
- First character remainder edge cases

### 7. **Tight Coupling to React Native Paper**

**Problem:** Using Paper components directly in business logic.

**Solution:** Keep Paper components at the UI layer only. Core logic should be framework-agnostic.

---

## Design Patterns

### 1. **Separation of Concerns**

- **Logic Layer** (`src/utils/`): Pure functions, no UI dependencies
- **State Layer** (`src/store/`): State management, actions
- **UI Layer** (`src/components/`, `src/screens/`): Presentation only

### 2. **Composition Over Inheritance**

Use composition for building complex components:
```typescript
<Screen>
  <Header title="Practice" />
  <Content>
    <ProblemDisplay />
    <AnswerButtons />
  </Content>
</Screen>
```

### 3. **Custom Hooks for Shared Logic**

Extract reusable logic into custom hooks:
```typescript
// src/hooks/useTutorialNavigation.ts
export const useTutorialNavigation = () => {
  const page = useAppStore((state) => state.tutorialPage);
  const setPage = useAppStore((state) => state.setTutorialPage);

  const next = () => setPage(Math.min(page + 1, 16));
  const previous = () => setPage(Math.max(page - 1, 0));

  return { page, next, previous };
};
```

### 4. **Error Boundaries**

Wrap screens in error boundaries to catch rendering errors:
```typescript
import { ErrorBoundary } from 'react-error-boundary';

<ErrorBoundary FallbackComponent={ErrorFallback}>
  <NavigationContainer>
    {/* App */}
  </NavigationContainer>
</ErrorBoundary>
```

---

## Performance Considerations

### 1. **Memoization**

Use `React.memo` for components that receive stable props:
```typescript
export const AnswerButton = React.memo<AnswerButtonProps>(({ value, onPress }) => {
  // Component implementation
});
```

### 2. **Selective Zustand Subscriptions**

Only subscribe to needed state slices:
```typescript
// ✅ Good - only re-renders when hintsEnabled changes
const hintsEnabled = useAppStore((state) => state.hintsEnabled);

// ❌ Bad - re-renders on any state change
const state = useAppStore();
```

### 3. **Lazy Loading (If Needed)**

For screens, use React Navigation's built-in lazy loading:
```typescript
const PracticeScreen = lazy(() => import('./screens/PracticeScreen'));
```

---

## Dependencies Version Matrix

Document exact versions of key dependencies for reproducibility:

| Package | Version | Purpose |
|---------|---------|---------|
| expo | ~51.0.0 | Expo framework |
| react-native | 0.74.x | React Native core |
| react-native-paper | ^5.12.0 | UI components |
| zustand | ^4.5.0 | State management |
| @react-native-async-storage/async-storage | ^1.23.0 | Persistence |
| react-navigation | ^6.x | Navigation |
| typescript | ^5.3.0 | Type safety |

---

## Phase Verification

Before proceeding to Phase 1, ensure:

- [ ] All ADRs are understood and agreed upon
- [ ] Folder structure is clear and makes sense
- [ ] Testing strategy is understood
- [ ] Common pitfalls are noted for reference
- [ ] Development environment prerequisites are available

---

**Next Phase:** [Phase 1: Project Setup & Core Infrastructure](./Phase-1.md)
