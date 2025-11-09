# iOS Deployment Guide

## Overview

This document describes how to build and deploy the Trachtenberg Multiplication app for iOS using EAS Build.

## Prerequisites

- Expo account (sign up at https://expo.dev)
- EAS CLI installed: `npm install -g eas-cli`
- Apple Developer account ($99/year) - **Required for device builds**
- Xcode Command Line Tools (if building locally)
- iOS device or simulator for testing

## Build Configuration

### eas.json Configuration

The iOS build is configured in `eas.json`:

```json
{
  "build": {
    "production": {
      "ios": {
        "buildConfiguration": "Release"
      }
    }
  }
}
```

### app.json Configuration

iOS-specific settings in `app.json`:

```json
{
  "expo": {
    "ios": {
      "supportsTablet": true,
      "bundleIdentifier": "com.trachtenberg.multiplication",
      "buildNumber": "1"
    }
  }
}
```

## Apple Developer Account Setup

### 1. Register Bundle Identifier

1. Go to https://developer.apple.com/account/
2. Navigate to Certificates, Identifiers & Profiles
3. Click Identifiers → +
4. Select App IDs
5. Register: `com.trachtenberg.multiplication`

### 2. App Store Connect

1. Go to https://appstoreconnect.apple.com/
2. My Apps → +
3. Create new app with bundle ID

## Building the IPA

### 1. Login to EAS

```bash
eas login
```

### 2. Configure the Project (First Time)

```bash
eas build:configure
```

### 3. Build the IPA

```bash
cd Migration/expo-project
eas build --platform ios --profile production
```

**During the build, EAS will:**
1. Ask for your Apple ID credentials
2. Handle provisioning profiles automatically
3. Sign the app with your developer certificate
4. Build and package the IPA

**Build Time:** Typically 10-20 minutes

### 4. Monitor the Build

Monitor progress:
- In the terminal
- On Expo dashboard: https://expo.dev

### 5. Download the IPA

Once complete, download from the provided URL or Expo dashboard.

## Installing the IPA

### On Physical Device

#### Method 1: TestFlight (Recommended)
```bash
# Submit to TestFlight
eas submit --platform ios --profile production
```

Then:
1. Testers receive email invite
2. Install TestFlight app
3. Install your app via TestFlight

#### Method 2: Ad Hoc Distribution
1. Register device UDID in Apple Developer Portal
2. Build with ad-hoc profile
3. Install via Xcode or Apple Configurator

### On Simulator

```bash
# Build for simulator
eas build --platform ios --profile development

# Install on simulator
xcrun simctl install booted path/to/app.app
```

## Testing the Build

### Functional Testing

Test all features on iOS:

1. **Tutorial Flow**
   - Navigate through all 17 tutorial pages
   - Verify content and highlighting
   - Test swipe gestures
   - Check page transitions

2. **Practice Mode**
   - Generate problems
   - Test answer submission
   - Verify feedback animations
   - Test problem completion

3. **Hints System**
   - Enable hints in settings
   - Test hint progression
   - Verify calculations

4. **Settings**
   - Toggle hints
   - Verify persistence

5. **Navigation**
   - Test tab navigation
   - Test swipe-back gesture
   - Verify deep linking

### iOS-Specific Testing

1. **Safe Area**
   - Test on devices with notch (iPhone X and later)
   - Verify content doesn't overlap with status bar/home indicator

2. **Dark Mode**
   - If supported, test in both light and dark modes

3. **Accessibility**
   - Test with VoiceOver
   - Verify dynamic type support
   - Check contrast ratios

4. **Multitasking** (iPad)
   - Test Split View
   - Test Slide Over

### Performance Testing

- App launches quickly (< 3 seconds)
- Smooth 60fps animations
- No memory warnings
- Efficient battery usage

### Compatibility Testing

Test on various iOS versions:
- iOS 15
- iOS 16
- iOS 17

Test on different devices:
- iPhone SE (small screen)
- iPhone 14/15 (standard)
- iPhone 14/15 Plus (large)
- iPhone 14/15 Pro Max (largest)
- iPad (if supporting tablet)

## Publishing to App Store

### 1. Prepare App Store Listing

Required assets:
- App icon (1024x1024 PNG)
- Screenshots:
  - iPhone 6.7" display (required)
  - iPhone 6.5" display
  - iPad Pro 12.9" (if supporting iPad)
- App preview videos (optional but recommended)
- App description (max 4,000 characters)
- Keywords
- Support URL
- Privacy policy URL

### 2. Build for Production

```bash
eas build --platform ios --profile production
```

### 3. Submit to App Store

#### Using EAS Submit:
```bash
eas submit --platform ios --profile production
```

You'll need:
- Apple ID
- App-specific password (generate at appleid.apple.com)

#### Manual Upload:
1. Download IPA from EAS Build
2. Use Xcode or Transporter app to upload
3. Go to App Store Connect
4. Complete app information
5. Submit for review

### 4. App Review Process

Apple's review typically takes:
- 24-48 hours for initial review
- Faster for updates

Common rejection reasons:
- Incomplete functionality
- Crashes
- Misleading screenshots
- Missing privacy policy

## App Signing

### Automatic Signing (Recommended)

EAS Build manages:
- Distribution certificates
- Provisioning profiles
- App Store Connect API keys

### Manual Signing

If you need custom signing:

1. Create certificates and profiles in Apple Developer Portal
2. Download and configure
3. Update `eas.json`:

```json
{
  "build": {
    "production": {
      "ios": {
        "credentialsSource": "local"
      }
    }
  }
}
```

## Troubleshooting

### Build Fails

**Error: "Code signing failed"**
- Verify Apple Developer account is active
- Check bundle identifier matches registered ID
- Ensure certificates are valid

**Error: "Provisioning profile doesn't match"**
- Delete and regenerate profiles in EAS
- Verify device UDIDs are registered

### Installation Fails

**Error: "Unable to install"**
- Check device iOS version compatibility
- Verify provisioning profile includes device UDID
- Ensure bundle ID matches

### TestFlight Issues

**Build not appearing in TestFlight**
- Check build is marked for TestFlight distribution
- Verify "Missing Compliance" is answered
- Wait up to 30 minutes for processing

### Runtime Issues

**App crashes on launch**
- Check crash logs in Xcode (Window → Devices and Simulators)
- Verify all required frameworks are included
- Test on different iOS versions

**Features not working**
- Check Info.plist permissions
- Verify entitlements
- Test on actual device (not just simulator)

## Version Management

### Incrementing Version

Update in `app.json`:

```json
{
  "expo": {
    "version": "1.0.1",  // CFBundleShortVersionString
    "ios": {
      "buildNumber": "2"  // CFBundleVersion (must increment)
    }
  }
}
```

**Rules:**
- `version`: User-facing (e.g., "1.0.0", "1.1.0")
- `buildNumber`: Internal build number (must always increase)
- Can have multiple builds with same version (e.g., 1.0.0 with build numbers 1, 2, 3)

## CI/CD Integration

### GitHub Actions Example

Create `.github/workflows/build-ios.yml`:

```yaml
name: Build iOS

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2

      - name: Setup Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '18'

      - name: Setup Expo
        uses: expo/expo-github-action@v7
        with:
          expo-version: latest
          token: ${{ secrets.EXPO_TOKEN }}

      - name: Install dependencies
        run: |
          cd Migration/expo-project
          npm ci

      - name: Build iOS
        run: |
          cd Migration/expo-project
          eas build --platform ios --profile production --non-interactive

      - name: Submit to TestFlight
        run: |
          cd Migration/expo-project
          eas submit --platform ios --profile production --non-interactive
```

## Beta Testing

### TestFlight Internal Testing

1. Build and submit via EAS
2. In App Store Connect:
   - Go to TestFlight tab
   - Add internal testers
3. Testers receive email invitation
4. Install via TestFlight app

### TestFlight External Testing

1. Complete app information
2. Add up to 10,000 external testers
3. Submit for Beta App Review (required)
4. Once approved, testers can install

## Over-the-Air (OTA) Updates

Use Expo Updates for JavaScript changes:

```bash
eas update --branch production --message "Bug fixes"
```

**Limitations:**
- Only for JavaScript/assets
- Not for native code changes
- Requires rebuilding for native changes

## Monitoring and Analytics

### Crash Reporting

Recommended services:
- Sentry
- Firebase Crashlytics
- Bugsnag

### Analytics

Consider:
- Firebase Analytics
- Mixpanel
- Amplitude

### Performance Monitoring

- Xcode Instruments
- Firebase Performance Monitoring
- New Relic

## Privacy and Permissions

### Required Info.plist Entries

None required for this app (no camera, location, etc.)

### Privacy Manifest

Create `PrivacyInfo.xcprivacy` if using:
- User tracking
- Required reasons API
- Third-party SDKs

### App Tracking Transparency

If tracking users:
```xml
<key>NSUserTrackingUsageDescription</key>
<string>Your data is used to provide a better experience.</string>
```

## App Store Optimization (ASO)

### Keywords

Research and select relevant keywords:
- "multiplication"
- "math practice"
- "Trachtenberg"
- "mental math"
- "arithmetic"

### Screenshots

Best practices:
- Show actual app screens
- Highlight key features
- Add captions
- Use device frames
- Localize if possible

### Ratings and Reviews

Encourage users to rate:
```typescript
import * as StoreReview from 'expo-store-review';

// After user completes tutorial or problem
if (await StoreReview.isAvailableAsync()) {
  await StoreReview.requestReview();
}
```

## Localization (Future)

To support multiple languages:

1. Add to `app.json`:
```json
{
  "expo": {
    "locales": {
      "es": "./locales/es.json",
      "fr": "./locales/fr.json"
    }
  }
}
```

2. Create translation files
3. Rebuild app

## Security Considerations

1. **Certificate Pinning**: For sensitive data
2. **Jailbreak Detection**: Optional security layer
3. **Code Obfuscation**: Protect proprietary algorithms
4. **Secure Storage**: Use Keychain for sensitive data

## Support Resources

- EAS Build docs: https://docs.expo.dev/build/introduction/
- Apple Developer: https://developer.apple.com/
- App Store Connect: https://appstoreconnect.apple.com/
- TestFlight: https://developer.apple.com/testflight/

## Version History

- v1.0.0 (2025-11-09): Initial release configuration
