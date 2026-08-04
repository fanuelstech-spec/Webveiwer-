# WeLink - Advanced Android WebView Wrapper

WeLink is a modern, production-ready Android application built with Kotlin and Jetpack Compose. It seamlessly transforms your website into a native-feeling mobile experience with high performance and deep native integrations.

## Features

### WebView Enhancements
- **Pull-to-Refresh**: Native support for refreshing web content.
- **Progressive Loading**: Visual progress bar for loading feedback.
- **Offline Mode**: Custom error screen with auto-retry logic.
- **Cookie & Storage Persistence**: Keeps users logged in across sessions.
- **Advanced API Support**: IndexedDB, Service Workers, DOM Storage, and PWA features.
- **In-App Browser**: External links automatically open in professional Chrome Custom Tabs.

### Native Integrations
- **App Links (Deep Linking)**: Clicking links to `welink.devfanuel.online` will open the app directly instead of the browser.
- **Native Music Player**: A high-performance background music player with native controls and UI.
- **Native Notifications**: Trigger system notifications directly from your website code.
- **Biometric Authentication**: Native login support for sensitive pages (Fingerprint/Face ID).
- **File Chooser**: Integrated Android file picker for uploads (Images, Videos, Documents).
- **Download Manager**: Native handling of file downloads with notification support.
- **PDF Viewer**: Built-in support for viewing PDF files using Google Docs Viewer.
- **Intent Handling**: Automatic handling of Phone, SMS, Email, and Maps links.
- **Share API**: Easy content sharing from web to native apps.

### Security
- **Safe Browsing**: Enabled by default for secure navigation.
- **Screenshot Prevention**: Configurable flag to block screenshots on sensitive screens.
- **Secure Mixed Content**: Disabled by default to ensure only HTTPS content is loaded.
- **SSL Validation**: Proper handling of SSL errors and certificates.

## Installation & Configuration

### 1. Website URL
The website URL is pre-configured to `https://welink.devfanuel.online/` in `app/src/main/java/com/example/data/AppConfig.kt`.

### 2. Front-End Integration (JavaScript Interface)
The app exposes a global `Android` object to your website. You can use the following methods in your JavaScript:

#### General Actions
```javascript
// Show a native Android toast message
Android.showToast("Hello from WeLink!");

// Open the native system share sheet
Android.share("Check out this link: https://welink.devfanuel.online");

// Trigger Biometric Authentication (Fingerprint/Face ID)
Android.authenticate();

// Trigger the Native QR Code Scanner
Android.scanQR();
```

#### Native Notifications
```javascript
// Send a native push-style notification to the user's phone
Android.sendNotification("New Message", "You have received a new update on WeLink!");
```

#### Native Music Player
```javascript
// Launch the native music player at the bottom of the app
// Param 1: Direct link to audio file (mp3, etc)
// Param 2: Title of the track
Android.playMusic("https://example.com/song.mp3", "Greatest Hits - Track 01");
```

### 3. Permissions
Required permissions (Camera, Location, Microphone, Notifications, etc.) are pre-configured in `AndroidManifest.xml` and handled at runtime within the WebView.

## Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Media Engine**: Media3 ExoPlayer
- **Pattern**: MVVM (Model-View-ViewModel)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)

## Performance Optimizations
- **Hardware Acceleration**: Enabled for smooth rendering.
- **Memory Management**: Optimized WebView settings to reduce overhead.
- **Startup Time**: Optimized with the official SplashScreen API.
