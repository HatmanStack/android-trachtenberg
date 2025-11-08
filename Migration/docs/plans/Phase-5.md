# Phase 5: Settings & State Persistence

## Phase Goal

Implement a functional Settings screen with a hint toggle switch, ensure proper state persistence using AsyncStorage, and validate that the hint setting correctly controls hint visibility in the Practice screen. This phase completes the core feature set by connecting the settings UI to the application state.

**Success Criteria:**
- Settings screen displays with hint toggle switch
- Toggle state persists across app sessions
- Changing hint setting immediately affects Practice screen
- Settings screen matches Material Design patterns
- Navigation to/from Settings works smoothly
- AsyncStorage integration is robust and handles errors gracefully

**Estimated tokens:** ~12,000

---

## Prerequisites

- Phase 4 completed (hint system fully functional)
- Zustand store with `hintsEnabled` state
- AsyncStorage persistence middleware configured
- React Native Paper Switch component available
- Settings screen placeholder from Phase 1

---

## Tasks

### Task 1: Implement Settings Screen UI

**Goal:** Build the Settings screen with a hint toggle switch using React Native Paper components.

**Files to Modify:**
- `Migration/expo-project/src/screens/SettingsScreen.tsx` - Replace placeholder with full implementation

**Prerequisites:**
- Phase 1 Task 7 completed (navigation structure created)
- React Native Paper installed

**Implementation Steps:**

1. Design Settings screen layout:
   - Use Paper `List` components for settings items
   - Single setting: "Show Hints" with Switch component
   - Clean, simple interface matching Android's minimal settings

2. Implement `SettingsScreen.tsx`:
   ```typescript
   import React from 'react';
   import { ScrollView, StyleSheet } from 'react-native';
   import { List, Switch, Divider } from 'react-native-paper';
   import { useAppStore } from '@store/appStore';
   import { SPACING } from '@theme/constants';

   export const SettingsScreen: React.FC = () => {
     const hintsEnabled = useAppStore((state) => state.hintsEnabled);
     const setHintsEnabled = useAppStore((state) => state.setHintsEnabled);

     const handleHintToggle = () => {
       setHintsEnabled(!hintsEnabled);
     };

     return (
       <ScrollView style={styles.container}>
         <List.Section>
           <List.Subheader>Practice Settings</List.Subheader>
           <List.Item
             title="Show Hints"
             description="Display step-by-step calculation hints during practice"
             left={(props) => <List.Icon {...props} icon="lightbulb-outline" />}
             right={() => (
               <Switch
                 value={hintsEnabled}
                 onValueChange={handleHintToggle}
               />
             )}
           />
           <Divider />
         </List.Section>

         {/* Future settings can be added here */}
       </ScrollView>
     );
   };

   const styles = StyleSheet.create({
     container: {
       flex: 1,
     },
   });
   ```

3. Add accessibility:
   - Proper labels for screen readers
   - Sufficient touch target sizes
   - Clear descriptions

**Verification Checklist:**
- [ ] Settings screen renders with hint toggle
- [ ] Toggle switch works (changes state)
- [ ] Layout uses Material Design List components
- [ ] Accessible labels and descriptions
- [ ] Responsive on different screen sizes

**Testing Instructions:**
- Navigate to Settings screen
- Toggle the hint switch
- Verify switch state changes visually
- Check Zustand store state updates
- Test on web and mobile
- Test with screen reader (accessibility)

**Commit Message Template:**
```
feat(screens): implement Settings screen with hint toggle

- Created Settings screen UI with Paper List components
- Added hint toggle switch
- Integrated with Zustand store
- Added proper accessibility labels
```

**Estimated tokens:** ~2,500

---

### Task 2: Verify State Persistence

**Goal:** Ensure hint setting persists correctly across app sessions using AsyncStorage.

**Files to Modify:**
- None (validation only, persistence already configured in Phase 1)

**Prerequisites:**
- Phase 1 Task 6 completed (AsyncStorage middleware configured)
- Task 1 completed (Settings screen working)

**Implementation Steps:**

1. Test persistence flow:
   - Enable hints in Settings
   - Close and reopen app
   - Verify hints remain enabled

2. Verify `partialize` function includes `hintsEnabled`:
   - Review `src/store/appStore.ts` persistence configuration
   - Ensure `hintsEnabled` is in the persisted state

3. Test error handling:
   - Simulate AsyncStorage failures (if possible)
   - Verify app doesn't crash
   - Verify fallback to default values

4. Check AsyncStorage data manually:
   ```typescript
   import AsyncStorage from '@react-native-async-storage/async-storage';

   // Debug: View stored data
   AsyncStorage.getItem('trachtenberg-app-storage').then(console.log);
   ```

**Verification Checklist:**
- [ ] Hint setting persists across app restarts
- [ ] `hintsEnabled` is in persisted state
- [ ] AsyncStorage errors handled gracefully
- [ ] Default values used if storage fails
- [ ] No console errors related to persistence

**Testing Instructions:**
- Enable hints → Close app → Reopen → Verify enabled
- Disable hints → Close app → Reopen → Verify disabled
- Clear AsyncStorage → Reopen → Verify default (disabled)
- Check browser DevTools (web) or debug AsyncStorage (mobile)

**Commit Message Template:**
```
test(persistence): verify hint setting persistence

- Tested AsyncStorage persistence for hint setting
- Verified state persists across sessions
- Tested error handling
- Confirmed default values work
```

**Estimated tokens:** ~1,500

---

### Task 3: Implement Real-Time Hint Visibility Updates

**Goal:** Ensure changing hint setting in Settings immediately affects the Practice screen (real-time reactivity).

**Files to Modify:**
- `Migration/expo-project/src/screens/PracticeScreen.tsx` - Ensure proper reactivity

**Prerequisites:**
- Task 1 completed (Settings screen working)
- Phase 4 Task 7 completed (hint UI in Practice screen)

**Implementation Steps:**

1. Verify Zustand reactivity:
   - Practice screen should already subscribe to `hintsEnabled` via `useAppStore`
   - Changes to `hintsEnabled` should automatically re-render Practice screen

2. Test real-time updates:
   - Open Practice screen
   - Navigate to Settings (keep Practice in stack/background)
   - Toggle hints
   - Return to Practice
   - Verify hint display appears/disappears

3. Add transition handling:
   - When hints are disabled, hide hint UI immediately
   - When hints are enabled, show hint UI and initialize hints if needed

4. Optional: Add listener for preference changes (if using stack navigation):
   ```typescript
   import { useFocusEffect } from '@react-navigation/native';

   useFocusEffect(
     React.useCallback(() => {
       // Refresh hint state when screen regains focus
       if (hintsEnabled && move === 0) {
         nextHint(); // Initialize first hint
       }
     }, [hintsEnabled])
   );
   ```

**Verification Checklist:**
- [ ] Changing hint setting updates Practice screen immediately
- [ ] Hint UI shows/hides based on setting
- [ ] No manual refresh needed
- [ ] Works on both web (tabs) and mobile (stack)

**Testing Instructions:**
- Start practice problem
- Navigate to Settings and enable hints
- Return to Practice → Hints should appear
- Navigate to Settings and disable hints
- Return to Practice → Hints should disappear
- Verify on web and mobile

**Commit Message Template:**
```
feat(reactivity): ensure real-time hint visibility updates

- Verified Zustand reactivity for hint setting
- Tested immediate UI updates when setting changes
- Added focus listener for state refresh
- Works across navigation modes
```

**Estimated tokens:** ~2,000

---

### Task 4: Add Settings Navigation from All Screens

**Goal:** Ensure Settings is accessible from Learn and Practice screens on all platforms.

**Files to Modify:**
- `Migration/expo-project/src/navigation/TabNavigator.tsx` - Settings tab already present
- `Migration/expo-project/src/navigation/StackNavigator.tsx` - Verify Settings screen included
- `Migration/expo-project/src/screens/LearnScreen.tsx` - Add Settings button (optional)

**Prerequisites:**
- Phase 1 Task 7 completed (navigation structure created)
- All screens implemented

**Implementation Steps:**

1. Verify Settings tab exists in `TabNavigator.tsx` (web):
   - Should already be present from Phase 1
   - Accessible from any tab

2. Verify Settings screen in `StackNavigator.tsx` (mobile):
   - Should already be present from Phase 1
   - Add header buttons if needed

3. Optionally add Settings button to Learn screen header:
   ```typescript
   // In LearnScreen.tsx
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
   ```

4. Ensure consistent navigation experience:
   - Web: Settings tab always visible
   - Mobile: Settings accessible via menu or direct navigation

**Verification Checklist:**
- [ ] Settings accessible from Learn screen
- [ ] Settings accessible from Practice screen
- [ ] Navigation works on web (tab)
- [ ] Navigation works on mobile (stack)
- [ ] Consistent user experience across platforms

**Testing Instructions:**
- From Learn screen, navigate to Settings
- From Practice screen, navigate to Settings
- Test on web and mobile
- Verify back navigation works

**Commit Message Template:**
```
feat(navigation): ensure Settings accessible from all screens

- Verified Settings tab in web navigation
- Verified Settings screen in mobile stack
- Added optional header buttons
- Tested cross-platform navigation
```

**Estimated tokens:** ~1,500

---

### Task 5: Add Settings Screen Styling

**Goal:** Polish the Settings screen with consistent styling and Material Design patterns.

**Files to Modify:**
- `Migration/expo-project/src/screens/SettingsScreen.tsx` - Add styling

**Prerequisites:**
- Task 1 completed (Settings screen UI built)

**Implementation Steps:**

1. Apply Material Design styling:
   - Use theme colors for list items
   - Proper spacing and padding
   - Dividers between sections
   - Elevation for surfaces if needed

2. Create styles:
   ```typescript
   const styles = StyleSheet.create({
     container: {
       flex: 1,
       backgroundColor: '#f5f5f5', // Light background
     },
     section: {
       marginTop: SPACING.md,
     },
   });
   ```

3. Add empty state or future settings placeholder:
   ```typescript
   <List.Section style={styles.section}>
     <List.Subheader>About</List.Subheader>
     <List.Item
       title="Version"
       description="1.0.0"
       left={(props) => <List.Icon {...props} icon="information-outline" />}
     />
   </List.Section>
   ```

4. Ensure consistency with other screens:
   - Use same theme colors
   - Same padding and spacing
   - Same font sizes

**Verification Checklist:**
- [ ] Consistent styling with other screens
- [ ] Material Design principles applied
- [ ] Readable typography
- [ ] Proper spacing
- [ ] Professional appearance

**Testing Instructions:**
- Visual review on multiple devices
- Compare to Learn and Practice screens
- Verify theme colors used correctly
- Check spacing consistency

**Commit Message Template:**
```
style(settings): apply Material Design styling

- Added consistent theme colors
- Applied proper spacing and padding
- Used Material Design list patterns
- Polished visual appearance
```

**Estimated tokens:** ~1,500

---

### Task 6: Test AsyncStorage Error Handling

**Goal:** Ensure app handles AsyncStorage failures gracefully.

**Files to Create:**
- `Migration/expo-project/__tests__/store/persistence.test.ts` - Persistence error tests

**Prerequisites:**
- AsyncStorage middleware configured

**Implementation Steps:**

1. Create test file for persistence:
   ```typescript
   import AsyncStorage from '@react-native-async-storage/async-storage';
   import { useAppStore } from '@store/appStore';

   describe('AsyncStorage Persistence', () => {
     beforeEach(() => {
       // Clear storage
       AsyncStorage.clear();
     });

     test('loads persisted state on initialization', async () => {
       // Set storage data
       await AsyncStorage.setItem(
         'trachtenberg-app-storage',
         JSON.stringify({ hintsEnabled: true })
       );

       // Create new store instance
       // Verify hintsEnabled is true
     });

     test('handles missing storage gracefully', async () => {
       // Clear storage
       await AsyncStorage.clear();

       // Create store
       // Verify default values used
     });

     test('handles corrupted storage data', async () => {
       // Set invalid JSON
       await AsyncStorage.setItem(
         'trachtenberg-app-storage',
         'invalid-json'
       );

       // Create store
       // Verify defaults used, no crash
     });

     test('persists state changes', async () => {
       const store = useAppStore.getState();
       store.setHintsEnabled(true);

       // Wait for async persistence
       await new Promise((resolve) => setTimeout(resolve, 100));

       const stored = await AsyncStorage.getItem('trachtenberg-app-storage');
       expect(JSON.parse(stored!).hintsEnabled).toBe(true);
     });
   });
   ```

2. Improve error handling in middleware if needed:
   - Add try-catch around AsyncStorage operations
   - Log errors without crashing app
   - Provide user feedback if persistence fails

**Verification Checklist:**
- [ ] Tests cover storage load scenarios
- [ ] Tests cover storage error scenarios
- [ ] App doesn't crash on storage errors
- [ ] Default values used on errors
- [ ] All tests pass

**Testing Instructions:**
- Run `npm test`
- Verify all persistence tests pass
- Manually corrupt storage and test app behavior
- Verify no crashes

**Commit Message Template:**
```
test(persistence): add AsyncStorage error handling tests

- Created comprehensive persistence tests
- Tested storage load and error scenarios
- Verified graceful error handling
- Ensured default values on failure
```

**Estimated tokens:** ~2,000

---

### Task 7: Write Tests for Settings Screen

**Goal:** Create tests for Settings screen UI and functionality.

**Files to Create:**
- `Migration/expo-project/__tests__/screens/SettingsScreen.test.tsx` - Settings screen tests

**Prerequisites:**
- Task 1 completed (Settings screen implemented)

**Implementation Steps:**

1. Write component tests:
   ```typescript
   import React from 'react';
   import { render, fireEvent } from '@testing-library/react-native';
   import { SettingsScreen } from '@screens/SettingsScreen';
   import { useAppStore } from '@store/appStore';

   describe('SettingsScreen', () => {
     test('renders hint toggle', () => {
       const { getByText } = render(<SettingsScreen />);
       expect(getByText('Show Hints')).toBeTruthy();
     });

     test('toggle switches hint state', () => {
       const { getByRole } = render(<SettingsScreen />);
       const toggle = getByRole('switch');

       const initialState = useAppStore.getState().hintsEnabled;
       fireEvent(toggle, 'valueChange', !initialState);

       expect(useAppStore.getState().hintsEnabled).toBe(!initialState);
     });

     test('displays hint description', () => {
       const { getByText } = render(<SettingsScreen />);
       expect(
         getByText(/step-by-step calculation hints/i)
       ).toBeTruthy();
     });
   });
   ```

2. Write integration tests:
   - Test navigation to Settings
   - Test that changes affect Practice screen

**Verification Checklist:**
- [ ] Component renders correctly
- [ ] Toggle switch works
- [ ] State updates on toggle
- [ ] All tests pass

**Testing Instructions:**
- Run `npm test`
- Verify Settings screen tests pass
- Check test coverage

**Commit Message Template:**
```
test(settings): add Settings screen tests

- Created Settings screen component tests
- Tested hint toggle functionality
- Verified state integration
- All tests passing
```

**Estimated tokens:** ~1,500

---

## Phase Verification

Before proceeding to Phase 6, ensure:

- [ ] Settings screen displays with hint toggle
- [ ] Toggle switch works and updates state
- [ ] Hint setting persists across app sessions
- [ ] Changing setting immediately affects Practice screen
- [ ] Settings accessible from all screens
- [ ] Styling is consistent and polished
- [ ] AsyncStorage errors handled gracefully
- [ ] All tests pass
- [ ] No console warnings or errors

**Known Limitations:**
- Only one setting (hints toggle) - future settings can be added easily
- No settings import/export functionality
- No reset to defaults button (can be added later)

**Integration Points:**
- Phase 6 will finalize navigation between all screens
- Phase 7 will add potential animations for settings changes

---

**Next Phase:** [Phase 6: Navigation & Platform-Specific UX](./Phase-6.md)
