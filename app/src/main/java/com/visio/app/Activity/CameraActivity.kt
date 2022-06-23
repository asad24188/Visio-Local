package com.visio.app.Activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mtechsoft.compassapp.networking.Constants
import com.visio.app.DataModel.BaseResponse
import com.visio.app.R
import com.visio.app.Services.ApiClient
import com.visio.app.databinding.ActivityCameraBinding
import com.visio.app.utils.GPSTracker
import com.wayprotect.app.utils.Utilities
import kotlinx.android.synthetic.main.activity_camera.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(),AdapterBTsheetImages.ItemClickListener {

    lateinit var binding: ActivityCameraBinding
    private lateinit var utilities: Utilities
    lateinit var context: Context

    private var btbehavior: BottomSheetBehavior<*>?=null

    private lateinit var adapter: AdapterBTsheetImages
    private lateinit var list: ArrayList<DataModelBottomSheetImages>
    private lateinit var adapterGrid: AdapterBTsheetCollection
    private lateinit var listGrid: ArrayList<CollectionBTsheetDataModel>

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory : File
    private lateinit var cameraExecutor : ExecutorService
    var imageFiles = ArrayList<File>()
    var multiImages: ArrayList<MultipartBody.Part> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initt()
        clicks()

        //CameraX
        if(allPermissionsGranted()){
            startCamera()
        }else{
            ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //Setup the listener for take photo button

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()


        list = ArrayList()



        binding.recycler.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

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
            btbehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        btbehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
//                    binding.dimenLayout.visibility = View.VISIBLE
                    binding.icArrowUp.visibility = View.GONE
                    binding.icArrowDown.visibility = View.VISIBLE
                    binding.headerLayout.setBackgroundColor(ContextCompat.getColor(this@CameraActivity, R.color.dark_clr))
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
//                    binding.dimenLayout.visibility = View.GONE
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

    private fun clicks() {

        binding.ButtonCameraClick.setOnClickListener {
            takePhoto()
        }

        binding.btnDone.setOnClickListener {


            var projectId = Constants.PROJECT_ID
            Log.d("proId",projectId.toString())

            var collectionName = binding.edtName.text.toString()
            var id1 = binding.edtId1.text.toString()
            var id2 = binding.edtId2.text.toString()
            var id3 = binding.edtId3.text.toString()
            var imageCount = imageFiles.size

            if (!collectionName.isEmpty()){
                if (!id1.isEmpty()){
                    if (!id2.isEmpty()){
                        if (!id3.isEmpty()){
                            if (imageCount > 0){

                                callApi(projectId,collectionName,id1,id2,id3)

                            }else{
                                utilities.makeToast(context,"Please upload images")
                            }
                        }else{
                            utilities.makeToast(context,"ID3 empty")
                        }
                    }else{
                        utilities.makeToast(context,"ID2 empty")
                    }
                }else{
                    utilities.makeToast(context,"ID1 empty")
                }
            }else{
                utilities.makeToast(context,"Enter collection name")
            }


        }

    }

    private fun callApi(
        projectId: String,
        collectionName: String,
        id1: String,
        id2: String,
        id3: String
    ) {

        val gpsTracker = GPSTracker(context)
        val lat = gpsTracker.latitude.toString()
        val lng = gpsTracker.longitude.toString()

        for (file in imageFiles) {
            val surveyBody = RequestBody.create(MediaType.parse("*/*"), file)
            multiImages.add(MultipartBody.Part.createFormData("images[]", file!!.path, surveyBody))
        }

        val pId: RequestBody = RequestBody.create(MediaType.parse("text/plain"), projectId)
        val cName: RequestBody = RequestBody.create(MediaType.parse("text/plain"), collectionName)
        val Id1: RequestBody = RequestBody.create(MediaType.parse("text/plain"), id1)
        val Id2: RequestBody = RequestBody.create(MediaType.parse("text/plain"), id2)
        val Id3: RequestBody = RequestBody.create(MediaType.parse("text/plain"), id3)
        val latt: RequestBody = RequestBody.create(MediaType.parse("text/plain"), lat)
        val lngg: RequestBody = RequestBody.create(MediaType.parse("text/plain"), lng)

        utilities.showProgressDialog(context,"Processing ...")
        val apiClient = ApiClient()
        apiClient.getApiService().createPost(pId,cName,Id1,Id2,Id3,latt,lngg,multiImages)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

                    utilities.hideProgressDialog()
                    if (response.isSuccessful) {

                        utilities.makeToast(context,response.body()!!.message)
                        finish()

                    } else {

                        utilities.hideProgressDialog()
                        utilities.makeToast(context,response.body()!!.message)

                    }
                }
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {

                    utilities.hideProgressDialog()
                    utilities.makeToast(context,t.message.toString())

                }
            })

    }

    private fun initt() {

        context = this
        if (!::utilities.isInitialized) utilities = Utilities(this)
        imageFiles = ArrayList()

    }


    fun takePhoto(){

        binding.ButtonCameraClick.isEnabled = false
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

                    btbehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    addImage(savedUri)
                    binding.ButtonCameraClick.isEnabled = true
                    imageFiles.add(photoFile)



                    Toast.makeText(baseContext,msg, Toast.LENGTH_SHORT).show()

                    Log.d(TAG, msg)
                }
            })

    }

    private fun addImage(savedUri: Uri?) {

        list.add(DataModelBottomSheetImages(savedUri!!))
        adapter = AdapterBTsheetImages(this@CameraActivity, list,this@CameraActivity)
        binding.recycler.adapter = adapter
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


    override fun onItemDeleted(position: Int) {

        imageFiles.removeAt(position)
        Log.d("files",imageFiles.size.toString())
        Log.d("files",list.size.toString())
    }

}