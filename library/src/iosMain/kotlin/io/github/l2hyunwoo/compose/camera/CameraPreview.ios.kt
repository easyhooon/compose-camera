@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.l2hyunwoo.compose.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.QuartzCore.CALayer
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView

/**
 * iOS implementation of CameraPreview using AVCaptureVideoPreviewLayer.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    configuration: CameraConfiguration,
    onCameraControllerReady: (CameraController) -> Unit
) {
    // Create and remember the camera controller
    val controller = remember(configuration) {
        IOSCameraController(initialConfiguration = configuration)
    }

    // Initialize camera
    LaunchedEffect(controller) {
        controller.initialize()
        onCameraControllerReady(controller)
    }

    // Cleanup on dispose
    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    // Create UIKit view with preview layer
    UIKitView(
        modifier = modifier,
        factory = {
            val previewView = UIView()

            // Create preview layer
            val previewLayer = AVCaptureVideoPreviewLayer(session = controller.captureSession)
            previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill

            // Add layer to view
            previewView.layer.addSublayer(previewLayer)

            previewView
        },
        update = { view ->
            // Update preview layer frame when view size changes
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)

            val sublayers = view.layer.sublayers
            if (sublayers != null && sublayers.isNotEmpty()) {
                val layer = sublayers.first() as? CALayer
                layer?.frame = view.bounds
            }

            CATransaction.commit()
        }
    )
}
