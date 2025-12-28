package io.github.l2hyunwoo.compose.camera

/**
 * Represents the current state of the camera
 */
sealed class CameraState {
    /**
     * Camera is initializing
     */
    data object Initializing : CameraState()

    /**
     * Camera is ready and preview is active
     */
    data class Ready(
        val currentLens: CameraLens,
        val flashMode: FlashMode,
        val isRecording: Boolean = false,
        val zoomRatio: Float = 1.0f
    ) : CameraState()

    /**
     * Camera encountered an error
     */
    data class Error(val exception: CameraException) : CameraState()
}

/**
 * Camera-related exceptions
 */
sealed class CameraException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    /**
     * Camera permission was not granted
     */
    class PermissionDenied : CameraException("Camera permission denied")

    /**
     * No camera device available
     */
    class NoCameraAvailable : CameraException("No camera device available")

    /**
     * Camera initialization failed
     */
    class InitializationFailed(cause: Throwable? = null) :
        CameraException("Camera initialization failed", cause)

    /**
     * Image capture failed
     */
    class CaptureFailed(cause: Throwable? = null) :
        CameraException("Image capture failed", cause)

    /**
     * Video recording failed
     */
    class RecordingFailed(cause: Throwable? = null) :
        CameraException("Video recording failed", cause)

    /**
     * Unknown camera error
     */
    class Unknown(message: String, cause: Throwable? = null) :
        CameraException(message, cause)
}
