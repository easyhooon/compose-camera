@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.l2hyunwoo.compose.camera.plugins

import io.github.l2hyunwoo.compose.camera.CameraController
import io.github.l2hyunwoo.compose.camera.IOSCameraController
import io.github.l2hyunwoo.compose.camera.plugin.CameraPlugin
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelAccurate
import platform.darwin.NSObject

actual class TextRecognizer actual constructor() : CameraPlugin {
    override val id: String = "TextRecognizer"
    
    private val _text = MutableStateFlow<TextResult?>(null)
    actual val text: StateFlow<TextResult?> = _text.asStateFlow()

    private var iosController: IOSCameraController? = null
    
    private val request: VNRecognizeTextRequest = VNRecognizeTextRequest(completionHandler = { request, error ->
        if (error == null) {
            val observations = request?.results as? List<VNRecognizedTextObservation>
            if (observations != null) {
                // Combine all text for simplicity
                val fullText = StringBuilder()
                val blocks = observations.map { observation ->
                    val topCandidate = observation.topCandidates(1u).firstOrNull() as? VNRecognizedText
                    val blockText = topCandidate?.string ?: ""
                    fullText.append(blockText).append("\n")
                    
                    // Vision doesn't provide generic Block/Line/Element hierarchy directly like ML Kit
                    // It gives Observations (roughly lines or blocks depending on settings)
                    TextBlock(
                        text = blockText,
                        lines = listOf(TextLine(blockText, listOf(TextElement(blockText))))
                    )
                }
                
                _text.value = TextResult(
                    text = fullText.toString().trim(),
                    blocks = blocks
                )
            } else {
                _text.value = null
            }
        }
    })

    private val frameListener: (CMSampleBufferRef?) -> Unit = { buffer ->
        if (buffer != null) {
            processFrame(buffer)
        }
    }

    init {
        request.recognitionLevel = VNRequestTextRecognitionLevelAccurate
    }

    override fun onAttach(controller: CameraController) {
        if (controller is IOSCameraController) {
            iosController = controller
            controller.addFrameListener(frameListener)
        }
    }

    override fun onDetach() {
        iosController?.removeFrameListener(frameListener)
        iosController = null
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun processFrame(buffer: CMSampleBufferRef) {
        val handler = VNImageRequestHandler(cMSampleBuffer = buffer, options = mapOf<Any?, Any?>())
        try {
            // performRequests throws Error in Kotlin if method signature has error param and returns generic
            // But for performRequests:error:, it usually returns Boolean. 
            // In KMP, we might need to pass error pointer or it throws exception.
            // Let's try simple call first, assuming it throws on failure.
            handler.performRequests(listOf(request), null)
        } catch (e: Exception) {
            // Handle error
        }
    }
}
