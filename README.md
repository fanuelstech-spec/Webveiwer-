# WebView Pro - Advanced Android WebView Wrapper

WebView Pro is a modern, production-ready Android application built with Kotlin and Jetpack Compose. It seamlessly transforms your website into a native-feeling mobile experience with high performance and deep native integrations.

## Features

### WebView Enhancements
- **Pull-to-Refresh**: Native support for refreshing web content.
- **Progressive Loading**: Visual progress bar for loading feedback.
- **Offline Mode**: Custom error screen with auto-retry logic.
- **Cookie & Storage Persistence**: Keeps users logged in across sessions.
- **Advanced API Support**: IndexedDB, Service Workers, DOM Storage, and PWA features.

### Native Integrations
- **Biometric Authentication**: Native login support for sensitive pages.
- **File Chooser**: Integrated Android file picker for uploads (Images, Videos, Documents).
- **Download Manager**: Native handling of file downloads with notification support.
- **PDF Viewer**: Built-in support for viewing PDF files using Google Docs Viewer.
- **Intent Handling**: Automatic handling of Phone, SMS, Email, and Maps links.
- **Share API**: Easy content sharing from web to native.

### Security
- **Safe Browsing**: Enabled by default for secure navigation.
- **Screenshot Prevention**: Configurable flag to block screenshots on sensitive screens.
- **Secure Mixed Content**: Disabled by default to ensure only HTTPS content is loaded.
- **SSL Validation**: Proper handling of SSL errors and certificates.

## Installation & Configuration

### 1. Website URL
Update the `BASE_URL` in `app/src/main/java/com/example/data/AppConfig.kt` to point to your website.

```kotlin
const val BASE_URL = "https://yourwebsite.com"
```

### 2. Native Features
The app includes a JavaScript interface named `Android`. You can call native functions from your website:
- `Android.showToast("Message")`
- `Android.share("Text to share")`
- `Android.authenticate()` (Triggers Biometric Prompt)

### 3. Permissions
Required permissions (Camera, Location, Microphone, etc.) are pre-configured in `AndroidManifest.xml` and handled at runtime within the WebView.

## Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Pattern**: MVVM (Model-View-ViewModel)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)

## Performance Optimizations
- **Hardware Acceleration**: Enabled for smooth rendering.
- **Memory Management**: Optimized WebView settings to reduce overhead.
- **Startup Time**: Optimized with the official SplashScreen API.
