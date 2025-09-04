package com.messenger.indiChat.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoCapture.withOutput
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import com.messenger.indiChat.R
import com.messenger.indiChat.network.RetrofitClient
import com.messenger.indiChat.repository.ReelRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddReelActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var btnCapture: ImageButton
    private lateinit var btnSwitch: ImageButton
    private lateinit var btnFlash: ImageButton
    private lateinit var btnGallery: ImageButton

    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private val GALLERY_REQUEST_CODE = 102

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var isRecording = false
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var isFlashOn = false

    private var lastRecordedFile: File? = null

    private lateinit var reelRepository: ReelRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reel)

        previewView = findViewById(R.id.previewView)
        btnCapture = findViewById(R.id.btnCapture)
        btnSwitch = findViewById(R.id.btnSwitchCamera)
        btnFlash = findViewById(R.id.btnFlash)
        btnGallery = findViewById(R.id.btnGallery)

        cameraExecutor = Executors.newSingleThreadExecutor()
        reelRepository = ReelRepository(RetrofitClient.reelApi(this))

        checkPermissions()

        btnCapture.setOnClickListener {
            if (isRecording) stopRecording() else startRecording()
        }

        btnSwitch.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        btnFlash.setOnClickListener {
            camera?.let {
                isFlashOn = !isFlashOn
                it.cameraControl.enableTorch(isFlashOn)
                Toast.makeText(this, if (isFlashOn) "Flash ON" else "Flash OFF", Toast.LENGTH_SHORT).show()
            }
        }

        btnGallery.setOnClickListener { openGallery() }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD, FallbackStrategy.lowerQualityThan(Quality.HD)))
                .build()

            videoCapture = withOutput(recorder)

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startRecording() {
        val videoCapture = this.videoCapture ?: return
        val outputFile = createVideoFile()
        lastRecordedFile = outputFile

        val outputOptions = androidx.camera.video.FileOutputOptions.Builder(outputFile).build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        recording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                Log.d("Reel", "Recording event: $recordEvent")
            }

        isRecording = true
        Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
        isRecording = false
        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show()

        // 🔹 Temporary file path
        lastRecordedFile?.let { file ->
            Log.d("Reel", "Video saved at: ${file.absolutePath}")
            // Pass this file path to the next activity
//            val intent = Intent(this, ReelDetailsActivity::class.java)
//            intent.putExtra("videoPath", file.absolutePath)
//            startActivity(intent)
        }
    }


    private fun createVideoFile(): File {
        val dir = externalMediaDirs.firstOrNull()?.let {
            File(it, "Reels").apply { mkdirs() }
        }
        return File(dir, "reel_${System.currentTimeMillis()}.mp4")
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "video/*" }
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedVideoUri: Uri? = data?.data
            if (selectedVideoUri != null) {
                val file = uriToFile(selectedVideoUri)
                file?.let { uploadVideo(it) }
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "gallery_${System.currentTimeMillis()}.mp4")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadVideo(file: File) {
        val requestFile = file.asRequestBody("video/mp4".toMediaType())
//        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//
//        val caption = "My first reel 🎥".toRequestBody("text/plain".toMediaType())
//        val userId = "USER123".toRequestBody("text/plain".toMediaType())
//
//        lifecycleScope.launch {
//            try {
//                val response = reelRepository.uploadReel(body, caption, userId)
//                if (response.isSuccessful) {
//                    Toast.makeText(this@AddReelActivity, "Upload successful!", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this@AddReelActivity, "Upload failed: ${response.code()}", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this@AddReelActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
