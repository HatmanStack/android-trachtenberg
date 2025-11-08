# Trachtenberg Multiplication App - Android to React Native (Expo) Migration Plan

## Feature Overview

This migration plan covers the complete port of the **Trachtenberg Multiplication** educational Android app to a modern React Native (Expo) application. The Trachtenberg system is a method of rapid mental calculation developed by Russian engineer Jakow Trachtenberg. This app teaches users the algorithm through interactive tutorials and practice problems with step-by-step hints.

**Core Features to Migrate:**
- **Tutorial System**: 17-step interactive walkthrough teaching the Trachtenberg multiplication algorithm using a hardcoded example (123456 × 789)
- **Practice Mode**: Randomly generated multiplication problems (4-digit × 3-digit numbers) with multiple-choice answer selection
- **Hint System**: Complex step-by-step calculation guidance that implements the actual Trachtenberg algorithm, showing which digits to multiply and tracking carries
- **Settings**: Toggle to enable/disable the hint system
- **Cross-Platform**: Web-first application with buildable Android and iOS versions

**Out of Scope:**
- AdMob integration (explicitly excluded per requirements)

## Prerequisites

### Development Environment
- **Node.js**: v18+ (LTS recommended)
- **npm** or **yarn**: Latest stable version
- **Expo CLI**: Install globally via `npm install -g expo-cli`
- **Git**: For version control and branch management
- **Code Editor**: VS Code recommended with React Native and TypeScript extensions

### Accounts & Services
- **Expo Account**: Free account at expo.dev for EAS Build
- **EAS CLI**: Install via `npm install -g eas-cli`
- **Self-Hosted Environment**: Web server infrastructure for hosting the production web build

### Knowledge Requirements
- TypeScript fundamentals
- React and React Hooks
- React Native core components
- Expo SDK and tooling
- Zustand state management
- React Native Paper components
- AsyncStorage API
- React Navigation (for hybrid navigation)

### Testing Environment
- Modern web browser (Chrome/Firefox/Safari)
- Android device or emulator (Android Studio)
- iOS simulator (macOS only) or physical device
- Mobile device with Expo Go app (for development testing)

## Phase Summary

| Phase | Goal | Estimated Tokens |
|-------|------|------------------|
| **Phase 0** | Foundation & Architecture Decisions | ~5,000 |
| **Phase 1** | Project Setup & Core Infrastructure | ~15,000 |
| **Phase 2** | Tutorial System Migration | ~20,000 |
| **Phase 3** | Practice Mode & Quiz Logic | ~25,000 |
| **Phase 4** | Hint System Implementation | ~30,000 |
| **Phase 5** | Settings & State Management | ~12,000 |
| **Phase 6** | Navigation & Platform-Specific UX | ~18,000 |
| **Phase 7** | Animations & Visual Polish | ~15,000 |
| **Phase 8** | Testing, Build Configuration & Deployment | ~20,000 |
| **TOTAL** | | **~160,000 tokens** |

## Navigation

- **[Phase 0: Foundation & Architecture](./Phase-0.md)** - Core decisions, tech stack, patterns
- **[Phase 1: Project Setup & Core Infrastructure](./Phase-1.md)** - Expo initialization, dependencies, folder structure
- **[Phase 2: Tutorial System Migration](./Phase-2.md)** - Tutorial content, pagination, text highlighting
- **[Phase 3: Practice Mode & Quiz Logic](./Phase-3.md)** - Problem generation, multiple choice, answer validation
- **[Phase 4: Hint System Implementation](./Phase-4.md)** - Complex Trachtenberg algorithm, move tracking, digit highlighting
- **[Phase 5: Settings & State Management](./Phase-5.md)** - Zustand store, AsyncStorage, preference persistence
- **[Phase 6: Navigation & Platform-Specific UX](./Phase-6.md)** - Hybrid navigation (web tabs, mobile stack)
- **[Phase 7: Animations & Visual Polish](./Phase-7.md)** - Fade animations, React Native Paper theming
- **[Phase 8: Testing, Build Configuration & Deployment](./Phase-8.md)** - Unit/integration tests, EAS Build, web export

## Migration Approach

This plan follows a **layered approach**, building from infrastructure to UI to complex logic:

1. **Foundation First**: Establish project structure, dependencies, and architectural patterns
2. **Content Migration**: Port tutorial arrays and static content to TypeScript
3. **Simple to Complex**: Build Tutorial → Practice → Hints (increasing complexity)
4. **Test-Driven**: Write tests before implementing core algorithms (especially the hint system)
5. **Iterative Validation**: Each phase includes verification steps before moving forward
6. **Platform Progressive**: Start with web, ensure mobile compatibility throughout

## Key Technical Challenges

1. **Hint Algorithm Complexity**: The original Java `practiceHint()`, `setIndex()`, and `setMove()` methods contain intricate logic with hardcoded arrays and conditional flows. These must be ported precisely while maintaining testability.

2. **Text Highlighting**: The Android app uses `SpannableString` to highlight specific digit positions. This must be replicated using React Native Text components with inline styles.

3. **State Synchronization**: SharedPreferences in Android must map to AsyncStorage in React Native, with Zustand managing runtime state and ensuring persistence.

4. **Cross-Platform Navigation**: Implementing hybrid navigation (tabs on web, stack on mobile) requires platform detection and conditional rendering.

5. **Animation Timing**: ObjectAnimator sequences must be translated to React Native Animated API or Reanimated library.

## Success Criteria

A successful migration will:
- ✅ Replicate all tutorial content accurately (17 steps)
- ✅ Generate random practice problems matching the original algorithm
- ✅ Implement the complete hint system with correct Trachtenberg calculations
- ✅ Provide smooth animations and modern Material Design UI
- ✅ Persist settings across sessions
- ✅ Work seamlessly on web, Android, and iOS
- ✅ Pass comprehensive unit and integration tests
- ✅ Deploy successfully to self-hosted web infrastructure
- ✅ Build successfully via EAS for mobile platforms

## Development Workflow

1. **Branch Strategy**: All work occurs in the `claude/trachtenberg-android-to-expo-migration-*` branch
2. **Commit Conventions**: Use conventional commits (feat:, fix:, test:, refactor:, etc.)
3. **Atomic Commits**: Commit after each significant task completion
4. **Testing First**: Write tests before implementing complex algorithms (TDD for hint system)
5. **Code Review Points**: End of each phase serves as a natural review checkpoint

## Notes

- The Migration directory is isolated from the existing Android codebase
- Original Java files are read-only references, never modified
- All new code resides in `Migration/`
- Tutorial content uses the same hardcoded example: "123456 × 789"
- The hint system is the most complex component—allocate appropriate time and testing

---

**Ready to begin?** Start with [Phase 0: Foundation & Architecture](./Phase-0.md)
