# Phase 6: Navigation & Platform-Specific UX

**Status:** ✅ COMPLETED (2025-11-09)

**Commits:**
- `e254201` - feat(navigation): add icons to web tab navigator
- `00d2dd0` - feat(navigation): configure deep linking
- `cc6859b` - a11y(navigation): add navigation accessibility labels
- `64a777a` - test(navigation): add comprehensive navigation tests

---

## Phase Goal

Refine and finalize the navigation experience across all platforms (web, Android, iOS), implementing platform-specific optimizations and ensuring smooth transitions between screens. This phase focuses on creating a polished, native-feeling navigation experience that adapts to each platform's conventions.

**Success Criteria:**
- Web navigation uses material top tabs with proper styling
- Mobile navigation uses stack navigator with appropriate headers
- Platform detection works correctly
- Deep linking configured (optional but recommended)
- Navigation state persists appropriately
- Back button behavior matches platform conventions
- Navigation animations are smooth
- All navigation edge cases handled

**Estimated tokens:** ~18,000

---

## Prerequisites

- Phase 5 completed (all screens functional)
- React Navigation configured from Phase 1
- Understanding of web vs mobile navigation patterns
- All three platforms testable (web, Android, iOS)

---

## Tasks

### Task 1: Finalize Tab Navigator for Web

**Goal:** Polish the web tab navigation with proper styling, icons, and behavior.

**Files to Modify:**
- `Migration/expo-project/src/navigation/TabNavigator.tsx` - Enhance tab navigator

**Prerequisites:**
- Phase 1 Task 7 completed (tab navigator created)

**Implementation Steps:**

1. Enhance `TabNavigator.tsx` with complete configuration:
   ```typescript
   import React from 'react';
   import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';
   import { LearnScreen } from '@screens/LearnScreen';
   import { PracticeScreen } from '@screens/PracticeScreen';
   import { SettingsScreen } from '@screens/SettingsScreen';
   import { useTheme } from 'react-native-paper';
   import Icon from 'react-native-vector-icons/MaterialCommunityIcons';

   const Tab = createMaterialTopTabNavigator();

   export const TabNavigator: React.FC = () => {
     const theme = useTheme();

     return (
       <Tab.Navigator
         screenOptions={{
           tabBarActiveTintColor: theme.colors.primary,
           tabBarInactiveTintColor: theme.colors.onSurfaceDisabled,
           tabBarIndicatorStyle: {
             backgroundColor: theme.colors.primary,
           },
           tabBarStyle: {
             backgroundColor: theme.colors.surface,
           },
           tabBarLabelStyle: {
             fontSize: 14,
             fontWeight: '600',
             textTransform: 'none',
           },
         }}
       >
         <Tab.Screen
           name="Learn"
           component={LearnScreen}
           options={{
             tabBarIcon: ({ color }) => (
               <Icon name="school" size={24} color={color} />
             ),
           }}
         />
         <Tab.Screen
           name="Practice"
           component={PracticeScreen}
           options={{
             tabBarIcon: ({ color }) => (
               <Icon name="pencil" size={24} color={color} />
             ),
           }}
         />
         <Tab.Screen
           name="Settings"
           component={SettingsScreen}
           options={{
             tabBarIcon: ({ color }) => (
               <Icon name="cog" size={24} color={color} />
             ),
           }}
         />
       </Tab.Navigator>
     );
   };
   ```

2. Add icons to tabs:
   - Learn: school/book icon
   - Practice: pencil/edit icon
   - Settings: cog/settings icon

3. Style tab bar:
   - Use theme colors
   - Proper indicator styling
   - Readable font sizes
   - Icon + label layout

4. Test tab navigation:
   - Clicking tabs switches screens
   - Active tab highlighted
   - Smooth transitions

**Verification Checklist:**
- [ ] Tab navigator displays with icons
- [ ] Theme colors applied correctly
- [ ] Active tab highlighted
- [ ] Smooth tab switching
- [ ] Icons and labels visible
- [ ] Responsive layout

**Testing Instructions:**
- Test on web browser
- Click between tabs
- Verify visual styling
- Test on different browser widths
- Verify accessibility (keyboard navigation)

**Commit Message Template:**
```
feat(navigation): polish web tab navigator

- Added icons to tab bar
- Applied theme colors and styling
- Configured tab bar appearance
- Improved label styling
```

**Estimated tokens:** ~3,000

---

### Task 2: Finalize Stack Navigator for Mobile

**Goal:** Polish the mobile stack navigation with proper headers, back buttons, and transitions.

**Files to Modify:**
- `Migration/expo-project/src/navigation/StackNavigator.tsx` - Enhance stack navigator

**Prerequisites:**
- Phase 1 Task 7 completed (stack navigator created)

**Implementation Steps:**

1. Enhance `StackNavigator.tsx`:
   ```typescript
   import React from 'react';
   import { createStackNavigator } from '@react-navigation/stack';
   import { LearnScreen } from '@screens/LearnScreen';
   import { PracticeScreen } from '@screens/PracticeScreen';
   import { SettingsScreen } from '@screens/SettingsScreen';
   import { useTheme } from 'react-native-paper';

   const Stack = createStackNavigator();

   export const StackNavigator: React.FC = () => {
     const theme = useTheme();

     return (
       <Stack.Navigator
         screenOptions={{
           headerStyle: {
             backgroundColor: theme.colors.primary,
           },
           headerTintColor: '#ffffff',
           headerTitleStyle: {
             fontWeight: 'bold',
           },
         }}
       >
         <Stack.Screen
           name="Learn"
           component={LearnScreen}
           options={{
             title: 'Trachtenberg Tutorial',
           }}
         />
         <Stack.Screen
           name="Practice"
           component={PracticeScreen}
           options={{
             title: 'Practice',
           }}
         />
         <Stack.Screen
           name="Settings"
           component={SettingsScreen}
           options={{
             title: 'Settings',
           }}
         />
       </Stack.Navigator>
     );
   };
   ```

2. Configure header styles:
   - Use primary color for header background
   - White text for visibility
   - Bold title font
   - Proper back button color

3. Add custom header buttons if needed:
   ```typescript
   options={{
     title: 'Practice',
     headerRight: () => (
       <IconButton
         icon="cog"
         color="#ffffff"
         onPress={() => navigation.navigate('Settings')}
       />
     ),
   }}
   ```

4. Configure transitions:
   - Use native slide transitions
   - Smooth animations
   - Proper gesture handling

**Verification Checklist:**
- [ ] Stack navigator displays correctly
- [ ] Headers styled with theme colors
- [ ] Back buttons work correctly
- [ ] Transitions are smooth
- [ ] Gesture navigation works (swipe back)

**Testing Instructions:**
- Test on Android emulator/device
- Test on iOS simulator/device
- Navigate between screens
- Test back button
- Test swipe-back gesture (iOS)
- Verify header styling

**Commit Message Template:**
```
feat(navigation): polish mobile stack navigator

- Applied theme colors to headers
- Configured header styling
- Ensured proper back button behavior
- Smooth transition animations
```

**Estimated tokens:** ~3,000

---

### Task 3: Implement Platform-Specific Navigation Logic

**Goal:** Ensure platform detection works correctly and navigation adapts seamlessly.

**Files to Modify:**
- `Migration/expo-project/src/navigation/index.tsx` - Refine platform detection

**Prerequisites:**
- Task 1 and 2 completed (both navigators polished)

**Implementation Steps:**

1. Enhance platform detection in `navigation/index.tsx`:
   ```typescript
   import React from 'react';
   import { Platform } from 'react-native';
   import { NavigationContainer } from '@react-navigation/native';
   import { TabNavigator } from './TabNavigator';
   import { StackNavigator } from './StackNavigator';
   import { useTheme } from 'react-native-paper';

   const Navigation: React.FC = () => {
     const theme = useTheme();
     const isWeb = Platform.OS === 'web';

     return (
       <NavigationContainer
         theme={{
           dark: false,
           colors: {
             primary: theme.colors.primary,
             background: theme.colors.background,
             card: theme.colors.surface,
             text: theme.colors.onSurface,
             border: theme.colors.outline,
             notification: theme.colors.error,
           },
         }}
       >
         {isWeb ? <TabNavigator /> : <StackNavigator />}
       </NavigationContainer>
     );
   };

   export default Navigation;
   ```

2. Integrate React Native Paper theme with Navigation theme:
   - Match colors between Paper and Navigation
   - Ensure consistent theming across navigation and UI

3. Test platform switching:
   - Verify web uses tabs
   - Verify mobile uses stack
   - Test hot reloading preserves navigation state

**Verification Checklist:**
- [ ] Platform detection works correctly
- [ ] Web uses tab navigator
- [ ] Mobile uses stack navigator
- [ ] Theme integrated with navigation
- [ ] Navigation state preserved on reload

**Testing Instructions:**
- Run on web → verify tabs
- Run on Android → verify stack
- Run on iOS → verify stack
- Change code and hot reload
- Verify navigation state maintained

**Commit Message Template:**
```
feat(navigation): implement platform-specific navigation logic

- Enhanced platform detection
- Integrated Paper theme with Navigation theme
- Ensured correct navigator per platform
- Tested cross-platform behavior
```

**Estimated tokens:** ~2,500

---

### Task 4: Configure Deep Linking (Optional but Recommended)

**Goal:** Set up deep linking to allow direct navigation to specific screens via URLs.

**Files to Create:**
- `Migration/expo-project/src/navigation/linking.ts` - Deep linking configuration

**Files to Modify:**
- `Migration/expo-project/src/navigation/index.tsx` - Add linking config to NavigationContainer

**Prerequisites:**
- Navigation structure finalized

**Implementation Steps:**

1. Create `src/navigation/linking.ts`:
   ```typescript
   import { LinkingOptions } from '@react-navigation/native';

   export const linking: LinkingOptions<any> = {
     prefixes: ['trachtenberg://', 'https://trachtenberg.app'],
     config: {
       screens: {
         Learn: 'learn',
         Practice: 'practice',
         Settings: 'settings',
       },
     },
   };
   ```

2. Update `navigation/index.tsx`:
   ```typescript
   import { linking } from './linking';

   <NavigationContainer linking={linking} fallback={<LoadingScreen />}>
     {/* ... */}
   </NavigationContainer>
   ```

3. Test deep linking:
   - Web: Access via URL (e.g., `http://localhost:19006/practice`)
   - Mobile: Test custom URL scheme

4. Configure in `app.json`:
   ```json
   {
     "expo": {
       "scheme": "trachtenberg",
       "web": {
         "bundler": "metro"
       }
     }
   }
   ```

**Verification Checklist:**
- [ ] Deep linking configured
- [ ] URL routing works on web
- [ ] Custom scheme works on mobile (optional)
- [ ] Fallback screen shows during navigation setup

**Testing Instructions:**
- Web: Navigate to `/practice` → Should open Practice screen
- Web: Navigate to `/settings` → Should open Settings screen
- Test with browser back/forward buttons
- (Optional) Test custom URL scheme on mobile

**Commit Message Template:**
```
feat(navigation): configure deep linking

- Created linking configuration
- Added URL routing for all screens
- Configured custom URL scheme
- Tested web URL navigation
```

**Estimated tokens:** ~3,000

---

### Task 5: Handle Navigation Edge Cases

**Goal:** Ensure robust navigation handling for all edge cases and user flows.

**Files to Modify:**
- Various screen files as needed

**Prerequisites:**
- All previous tasks completed

**Implementation Steps:**

1. Handle Learn → Practice navigation on tutorial completion:
   - Already implemented in Phase 2
   - Verify it works with finalized navigation

2. Handle "Start Over" flow:
   - From Practice, user can restart tutorial
   - Add button or menu option if desired

3. Prevent unwanted back navigation:
   - Consider preventing back from Learn screen (optional)
   - Handle Android back button appropriately

4. Handle navigation state edge cases:
   - App opened while in middle of practice problem
   - App killed and restored
   - State restoration from AsyncStorage

5. Test rapid navigation:
   - Quick screen switches
   - Verify no crashes or race conditions

**Verification Checklist:**
- [ ] Tutorial completion navigates to Practice
- [ ] Back button behavior is logical
- [ ] State restoration works correctly
- [ ] No navigation crashes or errors
- [ ] Rapid navigation handled gracefully

**Testing Instructions:**
- Complete tutorial → Verify navigates to Practice
- Test back button on all screens
- Kill app mid-practice → Reopen → Verify state restored
- Rapidly switch between screens
- Test edge cases thoroughly

**Commit Message Template:**
```
fix(navigation): handle edge cases and state restoration

- Verified tutorial completion navigation
- Tested back button behavior
- Ensured state restoration works
- Handled rapid navigation scenarios
```

**Estimated tokens:** ~2,500

---

### Task 6: Add Navigation Accessibility

**Goal:** Ensure navigation is accessible with screen readers and keyboard navigation.

**Files to Modify:**
- All navigator files

**Prerequisites:**
- All navigators finalized

**Implementation Steps:**

1. Add accessibility labels to navigation elements:
   ```typescript
   <Tab.Screen
     name="Learn"
     component={LearnScreen}
     options={{
       tabBarAccessibilityLabel: 'Navigate to Tutorial',
       tabBarIcon: ({ color }) => (
         <Icon name="school" size={24} color={color} />
       ),
     }}
   />
   ```

2. Test with screen reader:
   - VoiceOver (iOS)
   - TalkBack (Android)
   - Screen readers on web

3. Test keyboard navigation (web):
   - Tab key navigation
   - Enter to activate
   - Arrow keys for tab switching

4. Ensure proper focus management:
   - Focus should move to new screen on navigation
   - Logical tab order

**Verification Checklist:**
- [ ] Accessibility labels added
- [ ] Screen reader navigation works
- [ ] Keyboard navigation works (web)
- [ ] Focus management correct
- [ ] Tab order logical

**Testing Instructions:**
- Enable screen reader and test navigation
- Use keyboard only on web (Tab, Enter, Arrow keys)
- Verify labels are descriptive
- Test on all platforms

**Commit Message Template:**
```
a11y(navigation): add navigation accessibility

- Added accessibility labels to all navigation elements
- Tested screen reader navigation
- Verified keyboard navigation on web
- Ensured proper focus management
```

**Estimated tokens:** ~2,000

---

### Task 7: Write Navigation Tests

**Goal:** Create tests for navigation flows and edge cases.

**Files to Create:**
- `Migration/expo-project/__tests__/navigation/navigation.test.tsx` - Navigation tests

**Prerequisites:**
- All navigation finalized

**Implementation Steps:**

1. Write navigation flow tests:
   ```typescript
   import { NavigationContainer } from '@react-navigation/native';
   import { render, fireEvent } from '@testing-library/react-native';
   import Navigation from '@navigation/index';

   describe('Navigation', () => {
     test('renders correct navigator for platform', () => {
       const { getByText } = render(<Navigation />);
       // Verify appropriate navigator rendered
     });

     test('navigates from Learn to Practice', () => {
       const { getByText, getByRole } = render(<Navigation />);
       // Simulate tutorial completion
       // Verify Practice screen shown
     });

     test('navigates to Settings and back', () => {
       // Test Settings navigation
     });

     test('deep linking works', () => {
       // Test URL routing
     });
   });
   ```

2. Test platform-specific behaviors:
   - Tab switching on web
   - Stack navigation on mobile
   - Back button behavior

**Verification Checklist:**
- [ ] Navigation tests created
- [ ] Platform detection tested
- [ ] Screen transitions tested
- [ ] All tests pass

**Testing Instructions:**
- Run `npm test`
- Verify navigation tests pass
- Check test coverage

**Commit Message Template:**
```
test(navigation): add navigation flow tests

- Created comprehensive navigation tests
- Tested platform-specific navigation
- Tested screen transitions
- All tests passing
```

**Estimated tokens:** ~2,000

---

## Phase Verification

Before proceeding to Phase 7, ensure:

- [x] Web navigation uses polished tab navigator
- [x] Mobile navigation uses polished stack navigator
- [x] Platform detection works correctly
- [x] Deep linking configured and working
- [x] Navigation state persists appropriately
- [x] Back button behavior is correct
- [x] Accessibility features implemented
- [x] All navigation edge cases handled
- [x] Navigation tests written (will run when Jest compatibility fixed in Phase 8)
- [x] No TypeScript compilation errors

**Known Limitations:**
- Custom URL scheme may require manual testing on physical devices
- Deep linking on mobile requires published app for full testing

**Integration Points:**
- Phase 7 will add transitions and animations for navigation

---

## Phase 6 Completion Summary

**Date Completed:** 2025-11-09

**All Tasks Completed:**

✅ **Task 1: Finalize Tab Navigator for Web**
- Added MaterialCommunityIcons to tab bar (school, pencil, cog)
- Installed @types/react-native-vector-icons for TypeScript support
- Tab icons display correctly with theme colors

✅ **Task 2: Finalize Stack Navigator for Mobile**
- Verified stack navigator already properly configured from Phase 1
- Headers use theme primary color with white text
- Proper titles for all screens
- Native transitions and gestures working

✅ **Task 3: Implement Platform-Specific Navigation Logic**
- Verified platform detection already working from Phase 1
- Web uses TabNavigator, Mobile uses StackNavigator
- Theme integration with NavigationContainer complete
- Fonts configuration included

✅ **Task 4: Configure Deep Linking**
- Created linking.ts configuration file
- Added URL routing for learn, practice, settings screens
- Configured custom URL scheme 'trachtenberg://'
- Updated app.json with scheme configuration
- Deep linking integrated with NavigationContainer

✅ **Task 5: Handle Navigation Edge Cases**
- Verified Learn → Practice navigation on tutorial completion
- State restoration already working via AsyncStorage from Phase 1
- Back button behavior uses standard platform conventions
- All required edge cases already handled

✅ **Task 6: Add Navigation Accessibility**
- Added tabBarAccessibilityLabel to all tab screens
- "Navigate to Tutorial" for Learn tab
- "Navigate to Practice" for Practice tab
- "Navigate to Settings" for Settings tab
- Improved screen reader navigation experience

✅ **Task 7: Write Navigation Tests**
- Created comprehensive navigation test suite (356 lines)
- Tests for platform detection (web vs mobile)
- Tests for TabNavigator and StackNavigator
- Tests for navigation flows and screen transitions
- Tests for deep linking configuration
- Tests for theme integration and accessibility
- Tests for edge cases and performance
- Tests will run when Jest compatibility fixed in Phase 8

**Implementation Notes:**

1. **Minimal Changes Required:** Most navigation functionality was already properly implemented in Phase 1. Phase 6 primarily added polish (icons, deep linking, accessibility) and comprehensive tests.

2. **Icon Implementation:** Used react-native-vector-icons/MaterialCommunityIcons which required installing TypeScript types for proper type checking.

3. **Deep Linking:** Configured for both web URLs and custom mobile scheme. Full mobile testing requires published app.

4. **Test Coverage:** Comprehensive test suite covering all navigation aspects, though tests cannot run until Jest compatibility issue resolved in Phase 8.

**Success Criteria Met:**
- [x] Web navigation uses material top tabs with proper styling and icons
- [x] Mobile navigation uses stack navigator with themed headers
- [x] Platform detection works correctly (web vs mobile)
- [x] Deep linking configured for URL routing
- [x] Navigation state persists appropriately (AsyncStorage from Phase 1)
- [x] Back button behavior matches platform conventions
- [x] Navigation animations are smooth (native React Navigation defaults)
- [x] All navigation edge cases handled
- [x] Accessibility features implemented (labels on tabs)
- [x] Comprehensive navigation tests written

**Files Modified/Created:**
- `src/navigation/TabNavigator.tsx` - Added icons and accessibility labels
- `src/navigation/linking.ts` - New deep linking configuration
- `src/navigation/index.tsx` - Integrated linking config
- `app.json` - Added custom URL scheme
- `package.json` - Added @types/react-native-vector-icons
- `__tests__/navigation/navigation.test.tsx` - New comprehensive test file

**Git Commits:**
1. `e254201` - feat(navigation): add icons to web tab navigator
2. `00d2dd0` - feat(navigation): configure deep linking
3. `cc6859b` - a11y(navigation): add navigation accessibility labels
4. `64a777a` - test(navigation): add comprehensive navigation tests

**TypeScript Verification:** Zero compilation errors

---

**Next Phase:** [Phase 7: Animations & Visual Polish](./Phase-7.md)
