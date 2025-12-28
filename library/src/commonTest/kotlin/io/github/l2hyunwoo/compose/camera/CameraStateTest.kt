package io.github.l2hyunwoo.compose.camera

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CameraStateTest {
    @Test
    fun testInitializingState() {
        val state = CameraState.Initializing
        assertTrue(state is CameraState.Initializing)
    }

    @Test
    fun testReadyState() {
        val state = CameraState.Ready(
            currentLens = CameraLens.BACK,
            flashMode = FlashMode.OFF,
            isRecording = false,
            zoomRatio = 1.0f
        )

        assertEquals(CameraLens.BACK, state.currentLens)
        assertEquals(FlashMode.OFF, state.flashMode)
        assertEquals(false, state.isRecording)
        assertEquals(1.0f, state.zoomRatio)
    }

    @Test
    fun testErrorState() {
        val exception = CameraException.PermissionDenied()
        val state = CameraState.Error(exception)

        assertTrue(state.exception is CameraException.PermissionDenied)
    }
}
