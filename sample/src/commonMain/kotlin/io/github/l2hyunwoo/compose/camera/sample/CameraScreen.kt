package io.github.l2hyunwoo.compose.camera.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.l2hyunwoo.compose.camera.*
import kotlinx.coroutines.launch

/**
 * Sample camera screen demonstrating the Compose Camera library.
 */
@Composable
fun CameraScreen(
    onGalleryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var cameraController by remember { mutableStateOf<CameraController?>(null) }
    var cameraConfig by remember { mutableStateOf(CameraConfiguration()) }
    val cameraState by cameraController?.cameraState?.collectAsState()
        ?: remember { mutableStateOf<CameraState>(CameraState.Initializing) }

    val scope = rememberCoroutineScope()
    var lastCaptureResult by remember { mutableStateOf<String?>(null) }
    var currentRecording by remember { mutableStateOf<VideoRecording?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        // Camera Preview
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            configuration = cameraConfig,
            onCameraControllerReady = { controller ->
                cameraController = controller
            }
        )

        // Status overlay
        when (val state = cameraState) {
            is CameraState.Initializing -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is CameraState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.exception.message}",
                        color = Color.Red
                    )
                }
            }
            is CameraState.Ready -> {
                // Ready - show controls
            }
        }

        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Flash mode button
            val flashIcon = when (cameraConfig.flashMode) {
                FlashMode.OFF -> "⚫"
                FlashMode.ON -> "⚡"
                FlashMode.AUTO -> "🅰️"
                FlashMode.TORCH -> "🔦"
            }

            Button(
                onClick = {
                    val newFlashMode = when (cameraConfig.flashMode) {
                        FlashMode.OFF -> FlashMode.ON
                        FlashMode.ON -> FlashMode.AUTO
                        FlashMode.AUTO -> FlashMode.OFF
                        FlashMode.TORCH -> FlashMode.OFF
                    }
                    cameraConfig = cameraConfig.copy(flashMode = newFlashMode)
                    cameraController?.setFlashMode(newFlashMode)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Text(flashIcon)
            }

            // Lens switch button
            Button(
                onClick = {
                    val newLens = when (cameraConfig.lens) {
                        CameraLens.BACK -> CameraLens.FRONT
                        CameraLens.FRONT -> CameraLens.BACK
                    }
                    cameraConfig = cameraConfig.copy(lens = newLens)
                    cameraController?.setLens(newLens)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Text("🔄")
            }
        }

        // Capture result toast
        lastCaptureResult?.let { result ->
            LaunchedEffect(result) {
                kotlinx.coroutines.delay(3000)
                lastCaptureResult = null
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(result, color = Color.White)
            }
        }

        // Bottom controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(32.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video record button
            val isRecording = (cameraState as? CameraState.Ready)?.isRecording == true

            Button(
                onClick = {
                    scope.launch {
                        if (isRecording && currentRecording != null) {
                            // Stop recording
                            when (val result = currentRecording?.stop()) {
                                is VideoRecordingResult.Success -> {
                                    lastCaptureResult = "🎬 Recorded ${result.durationMs / 1000}s"
                                }
                                is VideoRecordingResult.Error -> {
                                    lastCaptureResult = "❌ ${result.exception.message}"
                                }
                                null -> {}
                            }
                            currentRecording = null
                        } else {
                            // Start recording
                            try {
                                currentRecording = cameraController?.startRecording()
                                lastCaptureResult = "🔴 Recording started..."
                            } catch (e: Exception) {
                                lastCaptureResult = "❌ ${e.message}"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color.Red else Color.Red.copy(alpha = 0.6f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (isRecording) {
                    // Stop icon (square)
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.White)
                    )
                } else {
                    // Record icon (circle)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            // Photo capture button
            Button(
                onClick = {
                    scope.launch {
                        cameraController?.let { controller ->
                            when (val result = controller.takePicture()) {
                                is ImageCaptureResult.Success -> {
                                    lastCaptureResult = "📸 Captured ${result.width}x${result.height}"
                                }
                                is ImageCaptureResult.Error -> {
                                    lastCaptureResult = "❌ ${result.exception.message}"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                enabled = !isRecording
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            // Gallery button
            Button(
                onClick = onGalleryClick,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("🖼️", fontSize = 24.sp)
            }
        }
    }
}
