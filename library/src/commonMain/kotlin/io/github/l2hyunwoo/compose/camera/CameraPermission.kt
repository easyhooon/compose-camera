package io.github.l2hyunwoo.compose.camera

/**
 * Camera-related permissions that may need to be requested.
 */
enum class CameraPermission {
    /** Camera access for preview and capture */
    CAMERA,
    /** Microphone access for video recording with audio */
    MICROPHONE,
    /** Storage access for saving photos/videos (Android legacy, API < 29) */
    STORAGE
}

/**
 * Status of a permission request.
 */
enum class PermissionStatus {
    /** Permission has been granted */
    GRANTED,
    /** Permission has been denied */
    DENIED,
    /** Permission has not been requested yet (iOS only) */
    NOT_DETERMINED
}

/**
 * Result of requesting camera permissions.
 */
data class PermissionResult(
    val permissions: Map<CameraPermission, PermissionStatus>
) {
    /** Returns true if all requested permissions were granted */
    val allGranted: Boolean
        get() = permissions.values.all { it == PermissionStatus.GRANTED }

    /** Returns true if camera permission is granted */
    val cameraGranted: Boolean
        get() = permissions[CameraPermission.CAMERA] == PermissionStatus.GRANTED

    /** Returns true if microphone permission is granted */
    val microphoneGranted: Boolean
        get() = permissions[CameraPermission.MICROPHONE] == PermissionStatus.GRANTED
}

/**
 * Platform-specific permission manager for camera-related permissions.
 * 
 * Use [createPermissionManager] to create an instance.
 */
expect class CameraPermissionManager {
    /**
     * Check the current status of a permission without requesting it.
     */
    suspend fun checkPermission(permission: CameraPermission): PermissionStatus

    /**
     * Request a single permission.
     * @return The resulting permission status after the request
     */
    suspend fun requestPermission(permission: CameraPermission): PermissionStatus

    /**
     * Request all camera-related permissions (camera and microphone).
     * @return Map of permission to its status
     */
    suspend fun requestCameraPermissions(): PermissionResult

    /**
     * Open the system app settings page where user can manually grant permissions.
     */
    fun openAppSettings()
}

/**
 * Create a platform-specific [CameraPermissionManager] instance.
 * 
 * This is a Composable function that provides proper context handling
 * on each platform automatically.
 */
@androidx.compose.runtime.Composable
expect fun rememberCameraPermissionManager(): CameraPermissionManager
