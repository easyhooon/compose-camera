package io.github.l2hyunwoo.compose.camera.plugins

import io.github.l2hyunwoo.compose.camera.plugin.CameraPlugin
import kotlinx.coroutines.flow.StateFlow

/**
 * Generic Barcode data class
 */
data class Barcode(
    val rawValue: String,
    val format: BarcodeFormat,
    val displayValue: String? = null
)

/**
 * Supported barcode formats
 */
enum class BarcodeFormat {
    QR_CODE,
    AZTEC,
    DATA_MATRIX,
    PDF417,
    EAN_13,
    EAN_8,
    UPC_A,
    UPC_E,
    CODE_39,
    CODE_93,
    CODE_128,
    CODABAR,
    ITF,
    UNKNOWN
}

/**
 * Plugin for scanning barcodes and QR codes.
 */
expect class BarcodeScanner() : CameraPlugin {
    /**
     * Stream of detected barcodes.
     * Emits a list of barcodes detected in the current frame.
     */
    val barcodes: StateFlow<List<Barcode>>
}
