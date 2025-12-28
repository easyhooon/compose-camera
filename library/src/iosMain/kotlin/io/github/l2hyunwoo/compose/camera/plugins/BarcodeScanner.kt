@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.l2hyunwoo.compose.camera.plugins

import io.github.l2hyunwoo.compose.camera.CameraController
import io.github.l2hyunwoo.compose.camera.IOSCameraController
import io.github.l2hyunwoo.compose.camera.plugin.CameraPlugin
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.Vision.VNBarcodeObservation
import platform.Vision.VNBarcodeSymbologyAztec
import platform.Vision.VNBarcodeSymbologyCode128
import platform.Vision.VNBarcodeSymbologyCode39
import platform.Vision.VNBarcodeSymbologyCode93
import platform.Vision.VNBarcodeSymbologyDataMatrix
import platform.Vision.VNBarcodeSymbologyEAN13
import platform.Vision.VNBarcodeSymbologyEAN8
import platform.Vision.VNBarcodeSymbologyITF14
import platform.Vision.VNBarcodeSymbologyPDF417
import platform.Vision.VNBarcodeSymbologyQR
import platform.Vision.VNBarcodeSymbologyUPCE
import platform.Vision.VNDetectBarcodesRequest
import platform.Vision.VNImageRequestHandler

actual class BarcodeScanner actual constructor() : CameraPlugin {
    override val id: String = "BarcodeScanner"
    
    private val _barcodes = MutableStateFlow<List<Barcode>>(emptyList())
    actual val barcodes: StateFlow<List<Barcode>> = _barcodes.asStateFlow()

    private var iosController: IOSCameraController? = null
    
    private val request: VNDetectBarcodesRequest = VNDetectBarcodesRequest(completionHandler = { request, error ->
        if (error == null) {
            val observations = request?.results as? List<VNBarcodeObservation>
            val detected = observations?.map { observation ->
                Barcode(
                    rawValue = observation.payloadStringValue ?: "",
                    displayValue = observation.payloadStringValue,
                    format = mapSymbology(observation.symbology ?: "")
                )
            } ?: emptyList()
            _barcodes.value = detected
        }
    })

    private val frameListener: (CMSampleBufferRef?) -> Unit = { buffer ->
        if (buffer != null) {
            processFrame(buffer)
        }
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
            handler.performRequests(listOf(request), null)
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun mapSymbology(symbology: String): BarcodeFormat {
        return when (symbology) {
            VNBarcodeSymbologyQR -> BarcodeFormat.QR_CODE
            VNBarcodeSymbologyAztec -> BarcodeFormat.AZTEC
            VNBarcodeSymbologyDataMatrix -> BarcodeFormat.DATA_MATRIX
            VNBarcodeSymbologyPDF417 -> BarcodeFormat.PDF417
            VNBarcodeSymbologyEAN13 -> BarcodeFormat.EAN_13
            VNBarcodeSymbologyEAN8 -> BarcodeFormat.EAN_8
            VNBarcodeSymbologyUPCE -> BarcodeFormat.UPC_E
            VNBarcodeSymbologyCode39 -> BarcodeFormat.CODE_39
            VNBarcodeSymbologyCode93 -> BarcodeFormat.CODE_93
            VNBarcodeSymbologyCode128 -> BarcodeFormat.CODE_128
            VNBarcodeSymbologyITF14 -> BarcodeFormat.ITF
            else -> BarcodeFormat.UNKNOWN
        }
    }
}
