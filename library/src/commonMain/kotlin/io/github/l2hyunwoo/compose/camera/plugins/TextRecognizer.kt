package io.github.l2hyunwoo.compose.camera.plugins

import io.github.l2hyunwoo.compose.camera.plugin.CameraPlugin
import kotlinx.coroutines.flow.StateFlow

data class TextResult(
    val text: String,
    val blocks: List<TextBlock>
)

data class TextBlock(
    val text: String,
    val lines: List<TextLine>
)

data class TextLine(
    val text: String,
    val elements: List<TextElement>
)

data class TextElement(
    val text: String
)

/**
 * Plugin for Recognizing Text (OCR).
 */
expect class TextRecognizer() : CameraPlugin {
    /**
     * Stream of recognized text.
     */
    val text: StateFlow<TextResult?>
}
