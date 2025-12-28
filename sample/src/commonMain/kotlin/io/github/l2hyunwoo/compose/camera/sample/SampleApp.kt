package io.github.l2hyunwoo.compose.camera.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.l2hyunwoo.compose.camera.rememberCameraPermissionManager

/**
 * Sample app entry point composable.
 * Handles permission requests and displays camera/gallery screens.
 */
@Composable
fun SampleApp() {
    val permissionManager = rememberCameraPermissionManager()
    var hasPermission by remember { mutableStateOf(false) }
    var permissionChecked by remember { mutableStateOf(false) }
    var showGallery by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val result = permissionManager.requestCameraPermissions()
        hasPermission = result.cameraGranted
        permissionChecked = true
    }

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        when {
            !permissionChecked -> {
                // Loading state while checking permissions
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("권한 확인 중...", color = MaterialTheme.colorScheme.onBackground)
                }
            }
            hasPermission -> {
                if (showGallery) {
                    GalleryScreen(
                        onBack = { showGallery = false },
                        onItemClick = { /* TODO: Show media detail */ }
                    )
                } else {
                    CameraScreen(
                        onGalleryClick = { showGallery = true }
                    )
                }
            }
            else -> {
                // Permission denied - show settings button
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { permissionManager.openAppSettings() }) {
                        Text("카메라 권한 설정 열기")
                    }
                }
            }
        }
    }
}
