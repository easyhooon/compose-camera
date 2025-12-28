package io.github.l2hyunwoo.compose.camera

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CameraConfigurationTest {
    @Test
    fun testDefaultConfiguration() {
        val config = CameraConfiguration()

        assertEquals(CameraLens.BACK, config.lens)
        assertEquals(FlashMode.OFF, config.flashMode)
        assertEquals(ImageFormat.JPEG, config.imageFormat)
        assertEquals(VideoQuality.FHD, config.videoQuality)
        assertEquals(30, config.targetFps)
        assertEquals(false, config.enableHdr)
        assertTrue(config.plugins.isEmpty())
    }

    @Test
    fun testCopyConfiguration() {
        val original = CameraConfiguration()
        val modified = original.copy(
            lens = CameraLens.FRONT,
            flashMode = FlashMode.ON
        )

        // Original should be unchanged
        assertEquals(CameraLens.BACK, original.lens)
        assertEquals(FlashMode.OFF, original.flashMode)

        // Modified should have new values
        assertEquals(CameraLens.FRONT, modified.lens)
        assertEquals(FlashMode.ON, modified.flashMode)

        // Other values should remain the same
        assertEquals(original.imageFormat, modified.imageFormat)
        assertEquals(original.videoQuality, modified.videoQuality)
    }

    @Test
    fun testWithPlugin() {
        val config = CameraConfiguration()
        val plugin = object : io.github.l2hyunwoo.compose.camera.plugin.CameraPlugin {
            override val id = "test-plugin"
            override fun onAttach(controller: CameraController) {}
            override fun onDetach() {}
        }

        val withPlugin = config.withPlugin(plugin)

        assertTrue(config.plugins.isEmpty())
        assertEquals(1, withPlugin.plugins.size)
        assertEquals("test-plugin", withPlugin.plugins.first().id)
    }

    @Test
    fun testWithoutPlugin() {
        val plugin1 = object : io.github.l2hyunwoo.compose.camera.plugin.CameraPlugin {
            override val id = "plugin-1"
            override fun onAttach(controller: CameraController) {}
            override fun onDetach() {}
        }
        val plugin2 = object : io.github.l2hyunwoo.compose.camera.plugin.CameraPlugin {
            override val id = "plugin-2"
            override fun onAttach(controller: CameraController) {}
            override fun onDetach() {}
        }

        val config = CameraConfiguration()
            .withPlugin(plugin1)
            .withPlugin(plugin2)

        assertEquals(2, config.plugins.size)

        val withoutOne = config.withoutPlugin("plugin-1")
        assertEquals(1, withoutOne.plugins.size)
        assertEquals("plugin-2", withoutOne.plugins.first().id)
    }
}
