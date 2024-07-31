package com.example.example_mvvm_camera

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalGetImage::class)
fun scanQR(images: ImageProxy, qrViewModel: QrViewModel) {
    // lấy ảnh từ ImageProxy
    val mediaImage = images.image
    if (mediaImage != null) {
        // lấy ra đối tượng để phân tích mã vạch
        val scanner = BarcodeScanning.getClient()
        // lấy ra ảnh để Ml Kit phân tích
        val inputImage =
            InputImage.fromMediaImage(mediaImage, images.imageInfo.rotationDegrees)
        // phân tích mã vạch
        scanner.process(inputImage)
            // phân tích thành công
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrCode ->
                        qrViewModel.setResult(qrCode)
                    }
                }
            }
            // phân tích thất bại
            .addOnFailureListener {
                Log.e("MlKitAnalyzer", "Barcode scanning failed", it)
            }
            // phân tích hoàn thành
            .addOnCompleteListener {
                images.close()
            }
    } else {
        images.close()
    }
}

