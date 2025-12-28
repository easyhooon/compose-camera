package io.github.l2hyunwoo.compose.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Camera preview composable.
 * Displays the camera preview and provides a [CameraController] for camera operations.
 *
 * Example:
 * ```
 * var controller by remember { mutableStateOf<CameraController?>(null) }
 *
 * CameraPreview(
 *     modifier = Modifier.fillMaxSize(),
 *     configuration = CameraConfiguration(
 *         lens = CameraLens.BACK,
 *         flashMode = FlashMode.OFF
 *     ),
 *     onCameraControllerReady = { controller = it }
 * )
 *
 * // Use controller to take pictures, record video, etc.
 * controller?.takePicture()
 * ```
 *
 * @param modifier Modifier for the preview
 * @param configuration Camera configuration
 * @param onCameraControllerReady Callback invoked when the camera controller is ready
 */
@Composable
expect fun CameraPreview(
    modifier: Modifier = Modifier,
    configuration: CameraConfiguration = CameraConfiguration(),
    onCameraControllerReady: (CameraController) -> Unit = {}
)
