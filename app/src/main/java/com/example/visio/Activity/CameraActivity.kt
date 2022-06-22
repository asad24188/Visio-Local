package com.example.visio.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visio.Adapter.AdapterBTsheetCollection
import com.example.visio.Adapter.AdapterBTsheetImages
import com.example.visio.DataModel.CollectionBTsheetDataModel
import com.example.visio.DataModel.DataModelBottomSheetImages
import com.example.visio.R
import com.example.visio.databinding.ActivityCameraBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraBinding
    private var btbehavior: BottomSheetBehavior<*>?=null

    private lateinit var adapter: AdapterBTsheetImages
    private lateinit var list: ArrayList<DataModelBottomSheetImages>
    private lateinit var adapterGrid: AdapterBTsheetCollection
    private lateinit var listGrid: ArrayList<CollectionBTsheetDataModel>

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory : File
    private lateinit var cameraExecutor : ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CameraX
        if(allPermissionsGranted()){
            startCamera()
        }else{
            ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //Setup the listener for take photo button

        binding.ButtonCameraClick.setOnClickListener {
            takePhoto()
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()


        list = ArrayList()
        list.add(DataModelBottomSheetImages(R.drawable.ic_tree_image))
        list.add(DataModelBottomSheetImages(R.drawable.ic_tree_image))
        list.add(DataModelBottomSheetImages(R.drawable.ic_tree_image))
        list.add(DataModelBottomSheetImages(R.drawable.ic_tree_image))
        list.add(DataModelBottomSheetImages(R.drawable.ic_tree_image))


        binding.recycler.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        adapter = AdapterBTsheetImages(this, list)
        binding.recycler.adapter = adapter

        listGrid = ArrayList()
        listGrid.add(
            CollectionBTsheetDataModel(
                "Collection 1",
                "123",
                "345",
                "789"
            )
        )
        listGrid.add(
            CollectionBTsheetDataModel(
                "Collection 1",
                "123",
                "345",
                "789"
            )
        )
        listGrid.add(
            CollectionBTsheetDataModel(
                "Collection 1",
                "123",
                "345",
                "789"
            )
        )
        listGrid.add(
            CollectionBTsheetDataModel(
                "Collection 1",
                "123",
                "345",
                "789"
            )
        )

//, GridLayoutManager.HORIZONTAL, false
        binding.recyclergrid.layoutManager = GridLayoutManager(this, 2)
        adapterGrid = AdapterBTsheetCollection(this, listGrid)
        binding.recyclergrid.adapter = adapterGrid

        btbehavior = BottomSheetBehavior.from(binding.btsheet)

        binding.headerLayout.setOnClickListener {
            btbehavior?.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        btbehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    binding.dimenLayout.visibility = View.VISIBLE
                    binding.icArrowUp.visibility = View.GONE
                    binding.icArrowDown.visibility = View.VISIBLE
                    binding.headerLayout.setBackgroundColor(ContextCompat.getColor(this@CameraActivity, R.color.dark_clr))
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    binding.dimenLayout.visibility = View.GONE
                    binding.icArrowUp.visibility = View.VISIBLE
                    binding.icArrowDown.visibility = View.GONE
                    binding.headerLayout.setBackgroundColor(ContextCompat.getColor(this@CameraActivity, R.color.black))
                }

                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
//                    binding.dimenLayout.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

    }


    fun takePhoto(){

        //get a stable reference of the modifiable image capture use case

        val imageCapture = imageCapture?:return

        //create time-stamped output file to hold the image

        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())+ ".jpg")

        //create output options object which contains file + metadata

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //setup image captue listener , which is triggered after photo has been taken

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}" , exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo captured succeeded: $savedUri"
                    Toast.makeText(baseContext,msg, Toast.LENGTH_SHORT).show()

                    Log.d(TAG, msg)
                }
            })

    }
    fun startCamera(){
        val cameraProviderFuture =  ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(ViewPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }
            , ContextCompat.getMainExecutor(this))
    }

    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED

    }

    fun getOutputDirectory():File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it,resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return  if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if (allPermissionsGranted()){
                startCamera()
            }else{
                Toast.makeText(
                    this,"Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object{
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val  REQUEST_CODE_PERMISSIONS = 10
        private  val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}