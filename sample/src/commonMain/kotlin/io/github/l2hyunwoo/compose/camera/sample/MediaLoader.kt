package io.github.l2hyunwoo.compose.camera.sample

/**
 * Media item representing a captured photo or video.
 */
data class MediaItem(
    val uri: String,
    val isVideo: Boolean,
    val dateAdded: Long,
    val displayName: String
)

/**
 * Platform-specific media loader for loading captured photos and videos.
 */
expect class MediaLoader {
    suspend fun loadMedia(): List<MediaItem>
}

/**
 * Create a platform-specific media loader.
 */
@androidx.compose.runtime.Composable
expect fun rememberMediaLoader(): MediaLoader

/**
 * Platform-specific media thumbnail composable.
 */
@androidx.compose.runtime.Composable
expect fun MediaThumbnailImage(
    item: MediaItem,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
)
