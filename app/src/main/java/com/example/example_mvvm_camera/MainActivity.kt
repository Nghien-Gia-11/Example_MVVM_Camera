package com.example.example_mvvm_camera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.example_mvvm_camera.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: QrViewModel by viewModels()

    // xin cấp quyền bằng registerForActivityResult
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setUpCameraX()
            } else {
                Toast.makeText(this, "Cần phải cấp quyền camera", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)


        viewModel.result.observe(this) {
            if (isValidURL(it)) {
                openWeb(it)
            } else {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpCameraX() {
        // tạo ra 1 luồng nền để sử lý việc phân tích ảnh
        // khi destroy cần shutdown luồng nền này đi
        cameraExecutor = Executors.newSingleThreadExecutor()
        // tạo ra đối tượng providerFutue để quản lý vòng đời của preview hay imageAnalyzis
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        // cung cấp surface để hiển thị hình ảnh bên cameraPreview trên màn hình
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = binding.cameraPreview.surfaceProvider
        }
        // chỉ định cam nào sẽ được sử dụng. ở đây là cam sau (back) nếu là trước (font)
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // sử lý tốc độ đọc ảnh
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        // đọc mã qr
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { image ->
            scanQR(image, viewModel)
        }
        try {
            cameraProvider.unbindAll()
            // liên kết providerFuture với vòng đời của activity hay fragment
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        } catch (e: Exception) {
            Log.e("BarcodeScanner", e.toString())
        }
    }

    private fun isValidURL(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    private fun openWeb(url: String) {
        val intentWeb = Intent(Intent.ACTION_VIEW)
        // đặt dữ liệu cho intent
        intentWeb.data = Uri.parse(url)

        // kiểm tra xem có intent nào khởi động đc url đó không
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intentWeb)
        } else {
            Toast.makeText(this, "Không thể mở liên kết", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}