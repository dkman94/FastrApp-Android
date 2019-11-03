package app.android.fastrapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import java.io.File
import kotlin.math.abs

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS =
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE)

private val MIN_DROOPY_MOUTH_THRESHOLD_DIFF = 0.16

class SmileTestFragment : Fragment() {

    lateinit var testResult: TextView
    lateinit var viewFinder: TextureView
    lateinit var takePicture: Button
    var viewFinderWidth = 0
    var viewFinderHeight = 0
    lateinit var helper: UserPrefsHelper;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewFinderWidth = (context!!.resources.displayMetrics.density * 300).toInt()
        viewFinderHeight = (context!!.resources.displayMetrics.density * 500).toInt()
        val view = inflater.inflate(R.layout.smile_tests_page_fragment, container, false)

        viewFinder = view.findViewById<TextureView>(R.id.view_finder)
        takePicture = view.findViewById<Button>(R.id.take_picture_button)
        testResult = view.findViewById<TextView>(R.id.droop_face_result)
        viewFinder.post {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }

        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform(viewFinder)
        }

        return view
    }

    fun initialize(helper: UserPrefsHelper) {
        this.helper = helper
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startCamera() {

        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(viewFinderWidth, viewFinderHeight))
            setLensFacing(CameraX.LensFacing.FRONT)
        }.build()
        val imageCapture = setupImageCapture()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform(viewFinder)
        }

        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun updateTransform(viewFinder: TextureView) {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    private fun setupImageCapture(): ImageCapture {

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setTargetAspectRatio(Rational(1, 1))
                // Sets the capture mode to prioritise over high quality images
                // or lower latency capturing
                setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                setLensFacing(CameraX.LensFacing.FRONT)
            }.build()

        val imageCapture = ImageCapture(imageCaptureConfig)

        // Set a click listener on the capture Button to capture the image
        takePicture.setOnClickListener {
            // Create the image file
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "${System.currentTimeMillis()}_CameraXPlayground.jpg"
            )
            testResult.setText(R.string.analyzing_face)
            testResult.setBackgroundColor(context!!.resources.getColor(R.color.defaultAlertColor))
            imageCapture.takePicture(file,
                                     object : ImageCapture.OnImageSavedListener {
                                         override fun onError(
                                             imageCaptureError: ImageCapture.ImageCaptureError,
                                             message: String,
                                             cause: Throwable?
                                         ) {
                                             val msg = "Photo capture failed: $message"
                                             Log.e("CameraXApp", msg)
                                         }

                                         // If the image capture is successful
                                         override fun onImageSaved(file: File) {
                                             val msg =
                                                 "Photo capture succeeded: ${file.absolutePath}"
                                             Log.d("CameraXApp", msg)
                                             getUserPictureAndAnalyze(file)
                                         }
                                     })
        }

        return imageCapture
    }

    private fun getUserPictureAndAnalyze(file: File) {
        val options = FirebaseVisionFaceDetectorOptions.Builder().apply {
            setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            setMinFaceSize(.15f)
        }.build()
        val image = FirebaseVisionImage.fromFilePath(context!!, file.toUri())
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        val result = detector.detectInImage(image)
            .addOnSuccessListener {
                println("analyzed image")
                testResult.visibility = View.VISIBLE
                if (it.isEmpty()) {
                    testResult.setText(R.string.no_faces_found)
                    testResult.setBackgroundColor(context!!.resources.getColor(R.color.failedTestAlert))
                } else {
                    val detectedFaceDroop = hasDetectedFaceDroop(it.first())
                    if (detectedFaceDroop) {
                        testResult.setText(R.string.droop_test_fail)
                        testResult.setBackgroundColor(context!!.resources.getColor(R.color.failedTestAlert))
                    } else {
                        testResult.setText(R.string.droop_test_pass)
                        testResult.setBackgroundColor(context!!.resources.getColor(R.color.healthyTestAlert))
                    }
                }
            }
            .addOnFailureListener {
                println("failed to analyzed image")
            }
    }

    private fun hasDetectedFaceDroop(face: FirebaseVisionFace): Boolean {
        val leftLip = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)!!.position
        val rightLip = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)!!.position
        val mouthMiddleBottom = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)!!.position

        val leftLipToMiddle =
            abs((leftLip.y - mouthMiddleBottom.y) / (leftLip.x - mouthMiddleBottom.x))
        val rightLipToMiddle =
            abs((rightLip.y - mouthMiddleBottom.y) / (rightLip.x - mouthMiddleBottom.x))
        val diff = abs(leftLipToMiddle - rightLipToMiddle)
        Log.d("threshold ", diff.toString())
        return diff > MIN_DROOPY_MOUTH_THRESHOLD_DIFF
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context!!, it
        ) == PackageManager.PERMISSION_GRANTED
    }

}
