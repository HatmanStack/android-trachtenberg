# Phase 1: Project Setup & Core Infrastructure

## Phase Goal

Initialize the Expo project with all required dependencies, configure the development environment, set up the folder structure, and establish the foundational infrastructure (theming, navigation skeleton, state management, persistence layer). By the end of this phase, you'll have a runnable Expo app with a basic navigation structure and configured tooling.

**Success Criteria:**
- Expo project runs successfully on web, Android, and iOS
- All dependencies installed and configured
- Folder structure matches Phase 0 architecture
- TypeScript configured with strict mode
- React Native Paper theme set up
- Zustand store initialized with AsyncStorage middleware
- Navigation structure created (tab and stack navigators)
- Basic placeholder screens render correctly
- EAS Build configured for future mobile builds

**Estimated tokens:** ~15,000

---

## Prerequisites

- Completed Phase 0 review
- Node.js v18+ installed
- Expo CLI installed globally (`npm install -g expo-cli`)
- EAS CLI installed globally (`npm install -g eas-cli`)
- Git configured and working in `claude/trachtenberg-android-to-expo-migration-*` branch

---

## Tasks

### Task 1: Initialize Expo Project

**Goal:** Create a new Expo project with TypeScript template in the Migration directory.

**Files to Create:**
- `Migration/expo-project/` - Root Expo project directory
- `Migration/expo-project/package.json` - Dependency manifest
- `Migration/expo-project/tsconfig.json` - TypeScript configuration
- `Migration/expo-project/App.tsx` - Root application component
- `Migration/expo-project/app.json` - Expo configuration

**Prerequisites:**
- Working directory must be `Migration/`
- Expo CLI available

**Implementation Steps:**

1. Navigate to the `Migration/` directory (create if it doesn't exist)
2. Run Expo initialization command with TypeScript template and name the project `expo-project`
3. Review the generated files and folder structure
4. Update `app.json` with proper app metadata:
   - Name: "Trachtenberg Multiplication"
   - Slug: "trachtenberg-multiplication"
   - Version: "1.0.0"
   - Orientation: portrait (primary), landscape (supported)
   - Icon: Use default for now (will be updated in polish phase)
   - Splash screen: Configure with primary color from Android app (#9fa8da)
   - iOS bundle identifier: `com.trachtenberg.multiplication`
   - Android package: `com.trachtenberg.multiplication`
   - Platforms: web, ios, android

**Verification Checklist:**
- [ ] `Migration/expo-project/` directory exists
- [ ] `package.json` includes Expo SDK ~51.0.0
- [ ] TypeScript files compile without errors (`npx tsc --noEmit`)
- [ ] Default app runs with `npx expo start` and displays in Expo Go or web browser
- [ ] `app.json` contains correct app metadata

**Testing Instructions:**
- Run `npx expo start` in the `expo-project` directory
- Open on web (press `w`)
- Verify the default Expo template screen renders
- Test on mobile device with Expo Go app (optional but recommended)

**Commit Message Template:**
```
feat(setup): initialize Expo project with TypeScript

- Created expo-project directory with TypeScript template
- Configured app.json with app metadata
- Verified app runs on web and mobile
```

**Estimated tokens:** ~1,000

---

### Task 2: Install Core Dependencies

**Goal:** Install all required npm packages for the project (UI library, navigation, state management, persistence).

**Files to Modify:**
- `Migration/expo-project/package.json` - Updated with all dependencies

**Prerequisites:**
- Task 1 completed (Expo project initialized)

**Implementation Steps:**

1. Install React Native Paper and its peer dependencies:
   - `react-native-paper@^5.12.0`
   - `react-native-safe-area-context`
   - `react-native-vector-icons` (required by Paper)

2. Install navigation libraries:
   - `@react-navigation/native`
   - `@react-navigation/stack`
   - `@react-navigation/material-top-tabs`
   - `react-native-screens`
   - `react-native-tab-view` (peer dependency for material-top-tabs)
   - `react-native-pager-view` (peer dependency)

3. Install state management:
   - `zustand@^4.5.0`

4. Install persistence:
   - `@react-native-async-storage/async-storage@^1.23.0`

5. Install dev dependencies:
   - `@types/react`
   - `@types/react-native`
   - `@testing-library/react-native`
   - `@testing-library/jest-native`
   - `jest`

6. Run installation command to install all dependencies

**Verification Checklist:**
- [ ] All packages listed in `package.json` under `dependencies`
- [ ] Dev dependencies listed under `devDependencies`
- [ ] No peer dependency warnings in console
- [ ] `node_modules/` directory populated
- [ ] App still runs with `npx expo start` after installation

**Testing Instructions:**
- Run `npm install` or `yarn install`
- Check for any errors or warnings
- Run `npx expo start` to verify app still works
- Import a component from Paper to verify it's accessible: `import { Button } from 'react-native-paper';`

**Commit Message Template:**
```
feat(deps): install core dependencies

- Added React Native Paper for UI components
- Added React Navigation for routing
- Added Zustand for state management
- Added AsyncStorage for persistence
- Added testing libraries
```

**Estimated tokens:** ~1,500

---

### Task 3: Configure TypeScript Strict Mode

**Goal:** Set up strict TypeScript configuration to enforce type safety throughout the project.

**Files to Modify:**
- `Migration/expo-project/tsconfig.json` - TypeScript compiler options

**Prerequisites:**
- Task 1 completed (project initialized)

**Implementation Steps:**

1. Open `tsconfig.json`
2. Update `compilerOptions` to enable strict mode and additional checks:
   - `strict: true`
   - `noImplicitAny: true`
   - `strictNullChecks: true`
   - `strictFunctionTypes: true`
   - `noUnusedLocals: true`
   - `noUnusedParameters: true`
   - `noImplicitReturns: true`
   - `skipLibCheck: true`
   - `esModuleInterop: true`
   - `resolveJsonModule: true`

3. Add path aliases for cleaner imports:
   ```json
   "baseUrl": ".",
   "paths": {
     "@components/*": ["src/components/*"],
     "@screens/*": ["src/screens/*"],
     "@utils/*": ["src/utils/*"],
     "@store/*": ["src/store/*"],
     "@data/*": ["src/data/*"],
     "@theme/*": ["src/theme/*"],
     "@navigation/*": ["src/navigation/*"],
     "@types/*": ["src/types/*"]
   }
   ```

4. Verify TypeScript compiles without errors

**Verification Checklist:**
- [ ] `tsconfig.json` has `strict: true`
- [ ] Path aliases configured
- [ ] `npx tsc --noEmit` runs without errors
- [ ] IDE shows autocomplete for path aliases (e.g., `@components/`)

**Testing Instructions:**
- Run `npx tsc --noEmit` to check for type errors
- Create a test import using an alias to verify path resolution works
- Fix any type errors that surface from stricter checks

**Commit Message Template:**
```
config(typescript): enable strict mode and path aliases

- Enabled strict TypeScript checks for better type safety
- Configured path aliases for cleaner imports
- Verified compilation with no errors
```

**Estimated tokens:** ~800

---

### Task 4: Create Folder Structure

**Goal:** Set up the complete folder structure as defined in Phase 0.

**Files to Create:**
- `Migration/expo-project/src/components/` - Reusable UI components directory
- `Migration/expo-project/src/screens/` - Screen components directory
- `Migration/expo-project/src/navigation/` - Navigation setup directory
- `Migration/expo-project/src/store/` - Zustand store directory
- `Migration/expo-project/src/utils/` - Utility functions directory
- `Migration/expo-project/src/data/` - Static data directory
- `Migration/expo-project/src/theme/` - Theming directory
- `Migration/expo-project/src/types/` - TypeScript types directory
- `Migration/expo-project/__tests__/` - Test files directory
- `Migration/expo-project/assets/` - Assets directory (images, fonts)

**Prerequisites:**
- Task 1 completed (project initialized)

**Implementation Steps:**

1. Create the `src/` directory structure with all subdirectories
2. Create placeholder `.gitkeep` files in each empty directory (to ensure they're tracked by git)
3. Create `src/types/index.ts` with initial type definitions:
   ```typescript
   // Common types used across the app

   export interface TutorialStep {
     id: number;
     explanation: string;
     answer: string;
     bottomArrow: string;
   }

   export interface PracticeProblem {
     firstNumber: number;
     secondNumber: number;
     answer: number;
   }

   export interface AnswerChoice {
     value: number;
     isCorrect: boolean;
   }
   ```

4. Verify folder structure matches Phase 0 specification

**Verification Checklist:**
- [ ] All directories from Phase 0 exist
- [ ] `src/types/index.ts` created with basic types
- [ ] Directories tracked by git (`.gitkeep` files present or actual files)
- [ ] Structure visible in IDE file explorer

**Testing Instructions:**
- List directory structure (e.g., `tree src/` or `ls -R src/`)
- Verify all expected folders are present
- Import types from `@types` to verify path alias works

**Commit Message Template:**
```
feat(structure): create project folder structure

- Created src/ with all subdirectories
- Added initial type definitions
- Structured according to Phase 0 architecture
```

**Estimated tokens:** ~700

---

### Task 5: Set Up React Native Paper Theme

**Goal:** Configure React Native Paper with a custom theme matching the Android app's color scheme.

**Files to Create:**
- `Migration/expo-project/src/theme/paperTheme.ts` - Paper theme configuration
- `Migration/expo-project/src/theme/constants.ts` - Theme constants

**Prerequisites:**
- Task 2 completed (dependencies installed)
- Task 4 completed (folder structure created)

**Implementation Steps:**

1. Create `src/theme/constants.ts` with color values from the Android app:
   ```typescript
   export const COLORS = {
     primary: '#9fa8da',        // Material light blue (from Android)
     primaryDark: '#6f79a8',    // Darker blue
     accent: '#ef5350',         // Red for highlighting
     background: '#ffffff',
     surface: '#ffffff',
     text: '#000000',
     disabled: '#9e9e9e',
     placeholder: '#757575',
     backdrop: 'rgba(0, 0, 0, 0.5)',
   };

   export const SPACING = {
     xs: 4,
     sm: 8,
     md: 16,
     lg: 24,
     xl: 32,
   };

   export const FONT_SIZES = {
     small: 12,
     medium: 16,
     large: 20,
     xlarge: 24,
   };

   export const TUTORIAL_STEPS_COUNT = 17;
   export const MAX_DIGIT_COUNT = 7; // Max answer length
   ```

2. Create `src/theme/paperTheme.ts` configuring React Native Paper theme:
   ```typescript
   import { MD3LightTheme } from 'react-native-paper';
   import { COLORS } from './constants';

   export const paperTheme = {
     ...MD3LightTheme,
     colors: {
       ...MD3LightTheme.colors,
       primary: COLORS.primary,
       primaryContainer: COLORS.primaryDark,
       secondary: COLORS.accent,
       background: COLORS.background,
       surface: COLORS.surface,
       error: COLORS.accent,
     },
   };
   ```

3. Update `App.tsx` to wrap the app in `PaperProvider` with the custom theme:
   ```typescript
   import { PaperProvider } from 'react-native-paper';
   import { paperTheme } from './src/theme/paperTheme';

   export default function App() {
     return (
       <PaperProvider theme={paperTheme}>
         {/* App content */}
       </PaperProvider>
     );
   }
   ```

**Verification Checklist:**
- [ ] Theme constants file created with Android app colors
- [ ] Paper theme configured and exported
- [ ] `App.tsx` wrapped in `PaperProvider`
- [ ] Theme colors match Android app (#9fa8da primary, #ef5350 accent)
- [ ] App runs without errors

**Testing Instructions:**
- Run the app with `npx expo start`
- Import and render a Paper Button component to verify theming:
  ```typescript
  import { Button } from 'react-native-paper';
  <Button mode="contained">Test Button</Button>
  ```
- Verify button uses primary color (#9fa8da)

**Commit Message Template:**
```
feat(theme): configure React Native Paper theme

- Created theme constants with Android app colors
- Configured Paper theme with Material Design 3
- Wrapped app in PaperProvider
```

**Estimated tokens:** ~1,200

---

### Task 6: Initialize Zustand Store with AsyncStorage Middleware

**Goal:** Create the Zustand store with AsyncStorage persistence for settings and state.

**Files to Create:**
- `Migration/expo-project/src/store/appStore.ts` - Main Zustand store
- `Migration/expo-project/src/store/middleware/persistMiddleware.ts` - AsyncStorage persistence middleware

**Prerequisites:**
- Task 2 completed (Zustand and AsyncStorage installed)
- Task 4 completed (folder structure created)

**Implementation Steps:**

1. Create `src/store/middleware/persistMiddleware.ts`:
   ```typescript
   import AsyncStorage from '@react-native-async-storage/async-storage';
   import { StateCreator, StoreMutatorIdentifier } from 'zustand';

   // Type-safe persistence middleware
   export const persistMiddleware = <T,>(
     config: StateCreator<T>,
     options: { name: string; partialize?: (state: T) => Partial<T> }
   ): StateCreator<T> => {
     return (set, get, api) => {
       const { name, partialize = (state) => state } = options;

       // Load persisted state on initialization
       AsyncStorage.getItem(name).then((stored) => {
         if (stored) {
           const parsed = JSON.parse(stored);
           set(parsed);
         }
       });

       // Wrap set to persist on every change
       const persistentSet: typeof set = (partial, replace) => {
         set(partial, replace);
         const state = get();
         const toPersist = partialize(state);
         AsyncStorage.setItem(name, JSON.stringify(toPersist));
       };

       return config(persistentSet, get, api);
     };
   };
   ```

2. Create `src/store/appStore.ts` with initial state structure:
   ```typescript
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
   ```

**Verification Checklist:**
- [ ] `appStore.ts` created with initial state and actions
- [ ] Persistence middleware created and applied
- [ ] Only `hintsEnabled` and `hintHelpShown` are persisted (not tutorial page)
- [ ] Store exports `useAppStore` hook
- [ ] TypeScript types are correct (no `any` types)

**Testing Instructions:**
- Import `useAppStore` in a test component
- Toggle `hintsEnabled` and verify state updates
- Reload the app and verify `hintsEnabled` persists (check AsyncStorage)
- Use `console.log(useAppStore.getState())` to inspect state
- Test AsyncStorage manually:
  ```typescript
  import AsyncStorage from '@react-native-async-storage/async-storage';
  AsyncStorage.getItem('trachtenberg-app-storage').then(console.log);
  ```

**Commit Message Template:**
```
feat(store): initialize Zustand store with persistence

- Created Zustand store with initial state
- Implemented AsyncStorage persistence middleware
- Configured selective persistence for settings
- Added TypeScript types for store
```

**Estimated tokens:** ~2,000

---

### Task 7: Create Navigation Structure (Skeleton)

**Goal:** Set up React Navigation with both tab navigator (web) and stack navigator (mobile), along with placeholder screens.

**Files to Create:**
- `Migration/expo-project/src/navigation/index.tsx` - Main navigation entry point
- `Migration/expo-project/src/navigation/TabNavigator.tsx` - Tab navigator for web
- `Migration/expo-project/src/navigation/StackNavigator.tsx` - Stack navigator for mobile
- `Migration/expo-project/src/screens/LearnScreen.tsx` - Placeholder Learn screen
- `Migration/expo-project/src/screens/PracticeScreen.tsx` - Placeholder Practice screen
- `Migration/expo-project/src/screens/SettingsScreen.tsx` - Placeholder Settings screen

**Prerequisites:**
- Task 2 completed (navigation dependencies installed)
- Task 4 completed (folder structure created)

**Implementation Steps:**

1. Create placeholder screen components:
   - Each screen should be a simple functional component rendering a Paper `Surface` with a title
   - Use TypeScript navigation types for type safety
   - Export screens as default exports

2. Create `src/navigation/TabNavigator.tsx`:
   - Use `createMaterialTopTabNavigator` from `@react-navigation/material-top-tabs`
   - Configure tabs for Learn, Practice, Settings
   - Apply Paper theme colors to tab bar
   - Set initial route to "Learn"

3. Create `src/navigation/StackNavigator.tsx`:
   - Use `createStackNavigator` from `@react-navigation/stack`
   - Configure stack for Learn, Practice, Settings screens
   - Apply Paper theme colors to headers
   - Set initial route to "Learn"

4. Create `src/navigation/index.tsx`:
   - Import `Platform` from React Native
   - Conditionally render `TabNavigator` (web) or `StackNavigator` (mobile)
   - Wrap in `NavigationContainer`

5. Update `App.tsx` to use the navigation component:
   ```typescript
   import { PaperProvider } from 'react-native-paper';
   import { paperTheme } from './src/theme/paperTheme';
   import Navigation from './src/navigation';

   export default function App() {
     return (
       <PaperProvider theme={paperTheme}>
         <Navigation />
       </PaperProvider>
     );
   }
   ```

**Verification Checklist:**
- [ ] Three placeholder screens created (Learn, Practice, Settings)
- [ ] Tab navigator created for web
- [ ] Stack navigator created for mobile
- [ ] Main navigation component switches based on platform
- [ ] App runs and displays navigation
- [ ] Can navigate between screens on web (tabs)
- [ ] Can navigate between screens on mobile (stack with back button)

**Testing Instructions:**
- Run app on web: verify tabs appear and navigation works
- Run app on mobile (Expo Go): verify stack navigation with headers
- Test platform switching:
  - Web: Should see material top tabs
  - Mobile: Should see stack headers with back buttons
- Verify theming: tabs/headers should use primary color

**Commit Message Template:**
```
feat(navigation): create hybrid navigation structure

- Created tab navigator for web platform
- Created stack navigator for mobile platforms
- Implemented platform-specific navigation rendering
- Added placeholder screens for Learn, Practice, Settings
```

**Estimated tokens:** ~3,000

---

### Task 8: Configure EAS Build

**Goal:** Set up EAS Build configuration for future Android and iOS builds.

**Files to Create:**
- `Migration/expo-project/eas.json` - EAS Build configuration

**Prerequisites:**
- Task 1 completed (Expo project initialized)
- EAS CLI installed globally

**Implementation Steps:**

1. Run `eas build:configure` in the `expo-project` directory to generate initial `eas.json`

2. Customize `eas.json` with appropriate build profiles:
   ```json
   {
     "build": {
       "development": {
         "developmentClient": true,
         "distribution": "internal",
         "ios": {
           "simulator": true
         }
       },
       "preview": {
         "distribution": "internal",
         "android": {
           "buildType": "apk"
         }
       },
       "production": {
         "android": {
           "buildType": "apk"
         },
         "ios": {
           "buildType": "archive"
         }
       }
     },
     "submit": {
       "production": {}
     }
   }
   ```

3. Verify EAS account is linked:
   - Run `eas whoami` to check logged-in account
   - If not logged in, run `eas login`

4. Do NOT run builds yet - this is just configuration for Phase 8

**Verification Checklist:**
- [ ] `eas.json` created with development, preview, and production profiles
- [ ] EAS account linked (`eas whoami` shows username)
- [ ] Configuration includes both Android and iOS settings
- [ ] `eas.json` committed to git

**Testing Instructions:**
- Run `eas build:configure` (should detect existing config and skip if already configured)
- Run `eas whoami` to verify authentication
- Review `eas.json` to ensure profiles are correct
- Do NOT trigger actual builds yet (saves build minutes)

**Commit Message Template:**
```
config(eas): configure EAS Build for mobile platforms

- Generated eas.json with build profiles
- Configured development, preview, and production builds
- Set up Android APK and iOS archive builds
```

**Estimated tokens:** ~1,000

---

### Task 9: Create Basic README for Project

**Goal:** Document the expo-project setup for future reference and other developers.

**Files to Create:**
- `Migration/expo-project/README.md` - Project documentation

**Prerequisites:**
- All previous tasks completed

**Implementation Steps:**

1. Create `README.md` in the `expo-project` directory
2. Include sections:
   - Project overview
   - Installation instructions
   - Running the app (web, iOS, Android)
   - Available scripts
   - Project structure overview
   - Testing commands
   - Build commands (for later phases)

**Verification Checklist:**
- [ ] README created with clear setup instructions
- [ ] Installation steps documented
- [ ] Run commands documented for all platforms
- [ ] Project structure overview included

**Testing Instructions:**
- Read through README as if you're a new developer
- Verify all commands mentioned are correct
- Test that installation steps work on a fresh clone

**Commit Message Template:**
```
docs(readme): create project documentation

- Added installation and setup instructions
- Documented available scripts
- Included project structure overview
```

**Estimated tokens:** ~800

---

### Task 10: Verify Complete Infrastructure

**Goal:** Run comprehensive verification tests to ensure all infrastructure is working correctly together.

**Files to Modify:**
- None (verification only)

**Prerequisites:**
- All previous tasks in Phase 1 completed

**Implementation Steps:**

1. Start the development server and verify all platforms:
   - Web: Should load with tab navigation
   - iOS: Should load in simulator with stack navigation
   - Android: Should load in emulator with stack navigation

2. Verify theming:
   - Check that primary color (#9fa8da) appears in navigation
   - Check that Paper components render with theme

3. Verify state management:
   - Add a toggle in Settings screen to test `hintsEnabled` state
   - Verify state persists after app reload

4. Verify TypeScript:
   - Run `npx tsc --noEmit` - should have no errors
   - Verify path aliases work by importing a component using alias

5. Verify dependencies:
   - Run `npm ls` or `yarn list` to check for dependency conflicts
   - Ensure no peer dependency warnings

6. Run initial test command (should pass even with no tests):
   - `npm test` or `yarn test`

**Verification Checklist:**
- [ ] App runs on web without errors
- [ ] App runs on iOS simulator without errors (if macOS)
- [ ] App runs on Android emulator without errors
- [ ] Navigation works on all platforms
- [ ] Theming is applied correctly
- [ ] State persists across app reloads
- [ ] TypeScript compilation succeeds
- [ ] No dependency warnings
- [ ] Test command runs successfully

**Testing Instructions:**
- Complete the verification steps above methodically
- Document any issues encountered
- If issues found, revisit relevant tasks to fix them
- Ensure clean state before proceeding to Phase 2

**Commit Message Template:**
```
test(infra): verify complete infrastructure setup

- Tested app on web, iOS, and Android platforms
- Verified navigation, theming, and state management
- Confirmed TypeScript compilation
- Validated dependency installation
```

**Estimated tokens:** ~1,500

---

## Phase Verification

Before proceeding to Phase 2, ensure:

- [ ] Expo project initialized and running on all target platforms
- [ ] All core dependencies installed and configured
- [ ] TypeScript strict mode enabled with no compilation errors
- [ ] Folder structure matches Phase 0 specification
- [ ] React Native Paper theme configured with Android app colors
- [ ] Zustand store initialized with AsyncStorage persistence
- [ ] Hybrid navigation structure created (tabs for web, stack for mobile)
- [ ] Placeholder screens render correctly
- [ ] EAS Build configured (but not yet executed)
- [ ] Project README documentation complete
- [ ] All tasks committed with conventional commit messages

**Known Limitations:**
- Screens are placeholders with no real content yet
- Store only has basic settings state (practice state added in Phase 3)
- No actual Trachtenberg logic implemented yet
- No tests written yet (added in Phase 4 for hint algorithm)

**Integration Points:**
- Phase 2 will populate the Learn screen with tutorial content
- Phase 3 will implement Practice screen logic
- Phase 4 will add hint system to Practice screen
- Phase 5 will expand Settings screen
- Phase 7 will add animations to all screens

---

**Next Phase:** [Phase 2: Tutorial System Migration](./Phase-2.md)
