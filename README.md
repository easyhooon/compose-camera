# Compose Camera

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.9.3-blueviolet.svg?style=flat)](https://www.jetbrains.com/lp/compose-multiplatform/)

A robust, feature-rich camera library for Compose Multiplatform supporting Android and iOS. Built with CameraX on Android and AVFoundation on iOS.

## Features

- 📱 **Cross-Platform**: Unified API for Android and iOS
- 📸 **Camera Preview**: High-performance camera preview using native views
- 🖼️ **Image Capture**: Capture high-quality photos with flash support
- 🎥 **Video Recording**: Record videos with audio
- 🔄 **Lens Control**: Switch between Front and Back cameras
- 🔦 **Flash Control**: Torch, On, Off, Auto modes
- ✋ **Permission Handling**: Built-in, platform-independent permission manager
- 🧩 **Plugin Architecture**: Extensible design for frame processing and custom features

| Platform | Status | Implementation |
|----------|--------|----------------|
| Android  | ✅ Ready | CameraX + CameraXViewfinder (Compose) |
| iOS      | ✅ Ready | AVFoundation + UIKitView |

## Installation

> 🚀 **Note**: Maven Central publication is coming soon.

Currently, this library can be used by including it locally in your project:

```kotlin
// settings.gradle.kts
include(":library")
```

```kotlin
// build.gradle.kts (commonMain)
dependencies {
    implementation(project(":library"))
}
```

## Setup

### Android (`AndroidManifest.xml`)

Add necessary permissions:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<!-- For saving to gallery on older Android versions -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
```

### iOS (`Info.plist`)

Add usage descriptions and high-refresh rate support:

```xml
<key>NSCameraUsageDescription</key>
<string>This app needs camera access to capture photos.</string>
<key>NSMicrophoneUsageDescription</key>
<string>This app needs microphone access to record videos.</string>

<!-- Important for smooth preview on iPhone Pro models -->
<key>CADisableMinimumFrameDurationOnPhone</key>
<true/>
```

## Usage

### 1. Permission Handling

Use the platform-independent `rememberCameraPermissionManager` to handle permissions easily.

```kotlin
@Composable
fun CameraScreen() {
    val permissionManager = rememberCameraPermissionManager()
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        val result = permissionManager.requestCameraPermissions()
        if (result.cameraGranted) {
            // Permission granted
        } else {
            // Permission denied
        }
    }
}
```

### 2. Camera Preview & Controls

```kotlin
@Composable
fun MyCameraScreen() {
    var cameraController by remember { mutableStateOf<CameraController?>(null) }
    // Camera Configuration State
    var config by remember { mutableStateOf(CameraConfiguration()) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            configuration = config,
            onCameraControllerReady = { controller ->
                cameraController = controller
            }
        )
        
        // Example Controls
        Button(onClick = { 
            // Switch Lens
            val newLens = if (config.lens == CameraLens.BACK) CameraLens.FRONT else CameraLens.BACK
            config = config.copy(lens = newLens)
            cameraController?.setLens(newLens)
        }) {
            Text("Switch Camera")
        }
        
        Button(onClick = {
            // Capture Photo
            scope.launch {
                val result = cameraController?.takePicture()
                when(result) {
                    is ImageCaptureResult.Success -> {
                        println("Image saved: ${result.filePath}")
                    }
                    is ImageCaptureResult.Error -> {
                         println("Error: ${result.exception}")
                    }
                }
            }
        }) {
            Text("Capture")
        }
    }
}
```

### 3. Video Recording

```kotlin
// Start Recording
val recording = cameraController?.startRecording()

// Stop Recording
scope.launch {
    val result = recording?.stop()
    when(result) {
        is VideoRecordingResult.Success -> {
            println("Video saved to: ${result.uri}")
        }
        else -> { /* Handle error */ }
    }
}
```



## License

```
Apache License 2.0
```
