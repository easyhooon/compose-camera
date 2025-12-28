package io.github.l2hyunwoo.compose.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner

/**
 * Android implementation of CameraPreview using CameraX Compose Viewfinder.
 */
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    configuration: CameraConfiguration,
    onCameraControllerReady: (CameraController) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create and remember the camera controller
    val controller = remember(configuration) {
        AndroidCameraController(
            context = context,
            lifecycleOwner = lifecycleOwner,
            initialConfiguration = configuration
        )
    }

    // Initialize camera
    LaunchedEffect(controller) {
        controller.initialize()
        onCameraControllerReady(controller)
    }

    // Collect surface request
    val surfaceRequest by controller.surfaceRequest.collectAsState()

    // Cleanup on dispose
    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    // Render the camera preview
    Box(modifier = modifier) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
