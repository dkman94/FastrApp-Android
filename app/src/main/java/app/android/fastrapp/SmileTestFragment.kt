package app.android.fastrapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.*
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class SmileTestFragment : Fragment() {

    lateinit var viewFinder: TextureView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.smile_tests_page_fragment, container, false)

        viewFinder = view.findViewById<TextureView>(R.id.view_finder)
        viewFinder.post {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                println("here")
            }
        }
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform(viewFinder)
        }

        val options = FirebaseVisionFaceDetectorOptions.Builder().apply {
            setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            setMinFaceSize(.15f)
        }


        return view
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startCamera() {

        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
        }.build()


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

        CameraX.bindToLifecycle(this, preview)

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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context!!, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}
