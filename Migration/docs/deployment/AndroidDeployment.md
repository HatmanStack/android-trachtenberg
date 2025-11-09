# Android Deployment Guide

## Overview

This document describes how to build and deploy the Trachtenberg Multiplication app for Android using EAS Build.

## Prerequisites

- Expo account (sign up at https://expo.dev)
- EAS CLI installed: `npm install -g eas-cli`
- Android device or emulator for testing
- (Optional) Google Play Console account for publishing

## Build Configuration

### eas.json Configuration

The Android build is configured in `eas.json`:

```json
{
  "build": {
    "production": {
      "android": {
        "buildType": "apk"
      }
    }
  }
}
```

### app.json Configuration

Android-specific settings in `app.json`:

```json
{
  "expo": {
    "android": {
      "package": "com.trachtenberg.multiplication",
      "adaptiveIcon": {
        "foregroundImage": "./assets/adaptive-icon.png",
        "backgroundColor": "#9fa8da"
      },
      "edgeToEdgeEnabled": true,
      "predictiveBackGestureEnabled": false
    }
  }
}
```

## Building the APK

### 1. Login to EAS

```bash
eas login
```

### 2. Configure the Project (First Time)

```bash
eas build:configure
```

This command:
- Links your project to your Expo account
- Creates or updates `eas.json`
- Sets up Android-specific configuration

### 3. Build the APK

```bash
cd Migration/expo-project
eas build --platform android --profile production
```

**Build Process:**
1. Code is uploaded to EAS Build servers
2. Dependencies are installed
3. Native code is compiled
4. APK is generated and signed
5. Build artifact is made available for download

**Build Time:** Typically 5-15 minutes

### 4. Monitor the Build

You can monitor the build progress:
- In the terminal (shows real-time logs)
- On the Expo dashboard: https://expo.dev/accounts/[your-account]/projects/trachtenberg-multiplication/builds

### 5. Download the APK

Once the build completes:

```bash
# The CLI will provide a download URL
# Or download from the Expo dashboard
```

## Installing the APK

### On Physical Device

#### Method 1: Direct Download
1. Open the build URL on your Android device
2. Download the APK
3. Install (you may need to enable "Install from Unknown Sources")

#### Method 2: ADB Install
```bash
# Connect device via USB with USB debugging enabled
adb install path/to/app.apk
```

### On Emulator

```bash
adb -e install path/to/app.apk
```

## Testing the Build

### Functional Testing

Test all features on the APK:

1. **Tutorial Flow**
   - Navigate through all 17 tutorial pages
   - Verify content and highlighting
   - Check page transitions

2. **Practice Mode**
   - Generate problems
   - Test answer submission
   - Verify correct/wrong feedback
   - Test problem completion

3. **Hints System**
   - Enable hints in settings
   - Test hint progression
   - Verify hint calculations match expected values

4. **Settings**
   - Toggle hints on/off
   - Verify settings persist after app restart

5. **Navigation**
   - Test tab navigation
   - Test back button behavior
   - Verify deep linking (if implemented)

### Performance Testing

- App launches quickly (< 3 seconds)
- Smooth animations (60fps)
- No frame drops during interaction
- Minimal memory usage

### Compatibility Testing

Test on various Android versions:
- Android 12 (API 31)
- Android 13 (API 33)
- Android 14 (API 34)

Test on different screen sizes:
- Phone (small, medium, large)
- Tablet

## Publishing to Google Play

### 1. Prepare Store Listing

Create assets:
- App icon (512x512 PNG)
- Feature graphic (1024x500 PNG)
- Screenshots (phone and tablet)
- App description
- Privacy policy

### 2. Generate AAB (Android App Bundle)

For Google Play, use AAB format instead of APK:

Update `eas.json`:
```json
{
  "build": {
    "production": {
      "android": {
        "buildType": "app-bundle"
      }
    }
  }
}
```

Build:
```bash
eas build --platform android --profile production
```

### 3. Submit to Google Play

#### Manual Upload:
1. Go to Google Play Console
2. Create a new app or select existing
3. Upload the AAB file
4. Complete store listing
5. Submit for review

#### Using EAS Submit:
```bash
eas submit --platform android --profile production
```

## App Signing

### Automatic Signing (Recommended)

EAS Build handles signing automatically:
- Generates signing keys
- Stores keys securely
- Signs your builds

### Manual Signing

If you need custom signing:

1. Generate keystore:
```bash
keytool -genkeypair -v -storetype PKCS12 -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
```

2. Configure in `eas.json`:
```json
{
  "build": {
    "production": {
      "android": {
        "credentialsSource": "local"
      }
    }
  }
}
```

3. Provide keystore when prompted during build

## Troubleshooting

### Build Fails

**Error: "Gradle build failed"**
- Check build logs for specific errors
- Verify all dependencies are compatible
- Check for syntax errors in configuration files

**Error: "Out of memory"**
- Increase heap size in gradle.properties
- Remove unused dependencies

### Installation Fails

**Error: "App not installed"**
- Enable "Install from Unknown Sources"
- Check if an older version is installed (uninstall first)
- Verify APK is not corrupted

**Error: "INSTALL_FAILED_VERSION_DOWNGRADE"**
- Uninstall existing app first
- Or increment version code in app.json

### Runtime Issues

**App crashes on launch**
- Check crash logs: `adb logcat`
- Verify all assets are included
- Check for missing permissions

**Features not working**
- Verify native modules are included
- Check console logs in development build
- Test on different Android versions

## Version Management

### Incrementing Version

Update in `app.json`:

```json
{
  "expo": {
    "version": "1.0.1",  // User-facing version
    "android": {
      "versionCode": 2   // Internal version code (must increment)
    }
  }
}
```

**Rules:**
- `version`: User-facing (e.g., "1.0.0", "1.1.0", "2.0.0")
- `versionCode`: Integer that must always increase

## CI/CD Integration

### GitHub Actions Example

Create `.github/workflows/build-android.yml`:

```yaml
name: Build Android

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

      - name: Build Android
        run: |
          cd Migration/expo-project
          eas build --platform android --profile production --non-interactive
```

## Monitoring and Analytics

### Crash Reporting

Consider integrating:
- Sentry
- Firebase Crashlytics
- Bugsnag

### Analytics

Consider adding:
- Google Analytics for Firebase
- Mixpanel
- Amplitude

## Over-the-Air (OTA) Updates

Use Expo Updates for minor changes without rebuilding:

```bash
# Publish an update
eas update --branch production --message "Fix for tutorial navigation"
```

**Note:** OTA updates only work for JavaScript/asset changes, not native code changes.

## Beta Testing

### Internal Testing

Use EAS Build's internal distribution:

```bash
eas build --platform android --profile preview
```

Share the build URL with testers.

### Google Play Internal Testing

1. Upload AAB to Google Play Console
2. Create internal testing track
3. Add tester emails
4. Testers receive access via email

## Security Considerations

1. **Code Obfuscation**: Enable ProGuard for release builds
2. **API Keys**: Never commit keys to version control
3. **HTTPS Only**: Ensure all network requests use HTTPS
4. **Permissions**: Only request necessary permissions

## Support

- EAS Build docs: https://docs.expo.dev/build/introduction/
- Expo forums: https://forums.expo.dev/
- Android developer docs: https://developer.android.com/

## Version History

- v1.0.0 (2025-11-09): Initial release configuration
