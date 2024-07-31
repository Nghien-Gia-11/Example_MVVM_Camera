package com.example.example_mvvm_camera

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalGetImage::class)
fun scanQR(images: ImageProxy, qrViewModel: QrViewModel){

    val mediaImage = images.image
    if (mediaImage != null) {
        val scanner = BarcodeScanning.getClient()
        val inputImage =
            InputImage.fromMediaImage(mediaImage, images.imageInfo.rotationDegrees)
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    Log.d("MlKitAnalyzer", "QR Code detected: $rawValue")
                    barcode.rawValue?.let { qrCode ->
                        qrViewModel.setResult(qrCode)
                    }

                }
            }
            .addOnFailureListener {
                Log.e("MlKitAnalyzer", "Barcode scanning failed", it)
            }
            .addOnCompleteListener {
                images.close()
            }
    } else {
        images.close()
    }


}

