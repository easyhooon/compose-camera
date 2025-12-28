package io.github.l2hyunwoo.compose.camera

/**
 * Camera lens selection
 */
enum class CameraLens {
    FRONT,
    BACK
}

/**
 * Flash mode for image capture
 */
enum class FlashMode {
    OFF,
    ON,
    AUTO,
    TORCH
}

/**
 * Image format for captured photos
 */
enum class ImageFormat {
    JPEG,
    PNG
}

/**
 * Video quality settings
 */
enum class VideoQuality {
    SD,      // 480p
    HD,      // 720p
    FHD,     // 1080p
    UHD      // 4K
}

/**
 * Directory for saving captured media
 */
enum class Directory {
    PICTURES,
    MOVIES,
    DCIM,
    CACHE
}
