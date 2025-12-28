package io.github.l2hyunwoo.compose.camera.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.Photos.*

/**
 * iOS implementation of MediaLoader using Photos framework.
 */
@OptIn(ExperimentalForeignApi::class)
actual class MediaLoader {
    
    actual suspend fun loadMedia(): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()
        
        // Request authorization first
        val status = PHPhotoLibrary.authorizationStatus()
        if (status != PHAuthorizationStatusAuthorized) {
            return emptyList()
        }
        
        // Fetch assets from the camera roll
        val fetchOptions = PHFetchOptions().apply {
            sortDescriptors = listOf(
                NSSortDescriptor.sortDescriptorWithKey("creationDate", ascending = false)
            )
        }
        
        val result = PHAsset.fetchAssetsWithOptions(fetchOptions)
        
        for (i in 0 until result.count.toInt().coerceAtMost(50)) {
            val asset = result.objectAtIndex(i.toULong()) as? PHAsset ?: continue
            val isVideo = asset.mediaType == PHAssetMediaTypeVideo
            val dateAdded = (asset.creationDate?.timeIntervalSince1970 ?: 0.0).toLong()
            
            mediaItems.add(
                MediaItem(
                    uri = asset.localIdentifier,
                    isVideo = isVideo,
                    dateAdded = dateAdded,
                    displayName = "Media_$i"
                )
            )
        }
        
        return mediaItems
    }
}

@Composable
actual fun rememberMediaLoader(): MediaLoader {
    return remember { MediaLoader() }
}
