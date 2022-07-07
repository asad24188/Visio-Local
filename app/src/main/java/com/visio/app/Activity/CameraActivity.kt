package com.visio.app.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.visio.Adapter.AdapterBTsheetCollection
import com.example.visio.Adapter.AdapterBTsheetImages
import com.example.visio.Adapter.PreviousCollectionsAdapter
import com.example.visio.DataModel.DataModelBottomSheetImages
import com.fed.fedsense.RoomDB.LocalCollection
import com.fed.fedsense.RoomDB.MyAppDataBase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.kircherelectronics.fsensor.filter.averaging.MeanFilter
import com.kircherelectronics.fsensor.observer.SensorSubject
import com.kircherelectronics.fsensor.sensor.FSensor
import com.kircherelectronics.fsensor.sensor.gyroscope.GyroscopeSensor
import com.mtechsoft.compassapp.networking.Constants
import com.visio.app.DataModel.BaseResponse
import com.visio.app.DataModel.projectDetail.Collection
import com.visio.app.DataModel.projectDetail.ProjectDetailResponse
import com.visio.app.R
import com.visio.app.Sensor.datalogger.DataLoggerManager
import com.visio.app.Services.ApiClient
import com.visio.app.databinding.ActivityCameraBinding
import com.visio.app.utils.Compass
import com.visio.app.utils.GPSTracker
import com.visio.app.utils.SOWTFormatter
import com.wayprotect.app.utils.Utilities
import kotlinx.android.synthetic.main.activity_camera.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(),AdapterBTsheetImages.ItemClickListener {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var context: Context
    private lateinit var utilities: Utilities
    private lateinit var adapter: AdapterBTsheetImages
    private lateinit var list: ArrayList<DataModelBottomSheetImages>
    private lateinit var adapterGrid: AdapterBTsheetCollection
    private lateinit var previousAdapter: PreviousCollectionsAdapter
    private lateinit var outputDirectory : File
    private lateinit var cameraExecutor : ExecutorService
    private lateinit var previousCollections: ArrayList<Collection>
    private lateinit var previousLocalCollections: List<LocalCollection>

    private var btbehavior: BottomSheetBehavior<*>?=null
    private var imageCapture: ImageCapture? = null
    private var imageFiles = ArrayList<File>()
    private var multiImages: ArrayList<MultipartBody.Part> = ArrayList()

    //compass work
    private val TAG: String? = "MainActivity"
    private var compass: Compass? = null
    private val dial: ImageView? = null
    private val hand: ImageView? = null
    private val label: TextView? = null
    private val currentAzimuth = 0f
    private val longitude: TextView? = null
    private  var latitude:TextView? = null
    private var locationManager: LocationManager? = null
    private var sowtFormatter: SOWTFormatter? = null


    //sensor work
    // Indicate if the output should be logged to a .csv file
    private val logData = false
    private var meanFilterEnabled = false
    private var fusedOrientation = FloatArray(3)

    // Handler for the UI plots so everything plots smoothly
    protected var uiHandler: Handler? = null
    protected var uiRunnable: Runnable? = null
    private var tvXAxis: TextView? = null
    private var tvYAxis: TextView? = null
    private var tvZAxis: TextView? = null
    private var sensorValue: TextView? = null
    private var fSensor: FSensor? = null
    private var meanFilter: MeanFilter? = null
    private var dataLogger: DataLoggerManager? = null

    private val sensorObserver =
        SensorSubject.SensorObserver { values -> updateValues(values) }

    var lat = 0.0
    var lng = 0.0
    var imagePath = ""

    private lateinit var myAppDatabase: MyAppDataBase
    lateinit var byteImage: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initt()
        clicks()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        onLocationChanged(location!!)
        setupCompass()


        dataLogger = DataLoggerManager(this)
        meanFilter = MeanFilter()

        uiHandler = Handler()
        uiRunnable = object : Runnable {
            override fun run() {
                uiHandler!!.postDelayed(this, 100)
                updateText()
            }
        }



        //CameraX
        if(allPermissionsGranted()){ startCamera()
        }else{
            ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //Setup the listener for take photo button

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        list = ArrayList()
        previousCollections = ArrayList()
        previousLocalCollections = ArrayList()

        //local work
         getpreviousCollections()
        //api work
//        setPrevoiusCollections()

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


    private fun updateText() {
        binding.valueXAxisCalibrated!!.text = java.lang.String.format(
            Locale.getDefault(), "%.0f", (Math.toDegrees(
                fusedOrientation[1].toDouble()
            ) + 360) % 360
        )
        binding.valueYAxisCalibrated!!.text = java.lang.String.format(
            Locale.getDefault(), "%.0f", (Math.toDegrees(
                fusedOrientation[2].toDouble()
            ) + 360) % 360
        )
        binding.valueZAxisCalibrated!!.text = java.lang.String.format(
            Locale.getDefault(), "%.0f", (Math.toDegrees(
                fusedOrientation[0].toDouble()
            ) + 360) % 360
        )
    }

    fun onLocationChanged(location: Location) {
        lat = location.latitude
        lng = location.longitude
//        binding.btnGps.text = String.format("%s", lattitude)+"/"+String.format("%s", longitude)

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "start compass")
        setupCompass()
        compass!!.start()
    }

    override fun onPause() {
        super.onPause()
        compass!!.stop()

        fSensor!!.unregister(sensorObserver)
        fSensor!!.stop()
        uiHandler!!.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        setupCompass()
        compass!!.start()

        //

        val mode: Mode = readPrefs()
        when (mode) {
            Mode.GYROSCOPE_ONLY -> fSensor =
                GyroscopeSensor(this)
        }
        fSensor!!.register(sensorObserver)
        fSensor!!.start()
        uiHandler!!.post(uiRunnable!!)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "stop compass")
        compass!!.stop()
    }

    private fun setupCompass() {
        compass = Compass(this)
        val cl: Compass.CompassListener = getCompassListener()
        compass!!.setListener(cl)
    }

    private fun getCompassListener(): Compass.CompassListener {
        return object : Compass.CompassListener {
            override fun onNewAzimuth(azimuth: Float) {
                runOnUiThread {
                    adjustSotwLabel(azimuth)
                }
            }
        }
    }

    private fun adjustSotwLabel(azimuth: Float) {
        binding.label!!.text = sowtFormatter!!.format(azimuth)
    }

    private fun getpreviousCollections() {

        previousLocalCollections = java.util.ArrayList()
        var projectId = Constants.PROJECT_ID.toInt()
        previousLocalCollections = myAppDatabase.collectionDao().loadAllCollections(projectId)

        binding.recyclergrid.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        previousAdapter = PreviousCollectionsAdapter(this, previousLocalCollections)
        binding.recyclergrid.adapter = previousAdapter


    }

    private fun setPrevoiusCollections() {

        val gsonn = Gson()
        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        val jsonn: String =  sharedPref.getString("pDetail", "").toString()
        val obj: ProjectDetailResponse = gsonn.fromJson(jsonn, ProjectDetailResponse::class.java)
        previousCollections = obj.data

        binding.recyclergrid.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterGrid = AdapterBTsheetCollection(this, previousCollections)
        binding.recyclergrid.adapter = adapterGrid

    }

    private fun clicks() {



        binding.ButtonCameraClick.setOnClickListener {
            takePhoto()
        }

        binding.locationLin.setOnClickListener {

            val gpsTracker = GPSTracker(context)
            val lat = gpsTracker.latitude.toString()
            val lng = gpsTracker.longitude.toString()

            utilities.makeToast(context,lat+"/"+lng)

        }

        binding.btnDone.setOnClickListener {


            var action = "done"
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


                                //api work

                                val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
                                ) {

                                    callApi(Constants.PROJECT_ID_FOR_API,collectionName,id1,id2,id3,action)
                                }

                                //local work
                                addLocalCollection(projectId, collectionName, id1, id2, id3, action)



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

        binding.btnNext.setOnClickListener {


            var action = "next"
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

                                //api work

                                val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
                                ) {

                                    callApi(Constants.PROJECT_ID_FOR_API,collectionName,id1,id2,id3,action)
                                }

                                //local work
                                addLocalCollection(projectId, collectionName, id1, id2, id3, action)

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

    private fun addLocalCollection(
        projectId: String,
        collectionName: String,
        id1: String,
        id2: String,
        id3: String,
        action: String
    ) {

        val gpsTracker = GPSTracker(context)
        if (gpsTracker.canGetLocation()){

            lat = gpsTracker.latitude
            lng = gpsTracker.longitude

        }else{

            lat = 0.0
            lng = 0.0
        }


        val r = Random()
        val id = r.nextInt(100000 - 1) + 1
        Log.d("iddd",id.toString())



        val collection = LocalCollection(id,projectId,collectionName,id1,id2,id3,lat.toString(),lng.toString(),false,imagePath)
        myAppDatabase.collectionDao().addCollection(collection)
        utilities.makeToast(context,"added")
        if (action.equals("done")){
            finish()
        }else{

            startActivity(Intent(context,CameraActivity::class.java))
            finish()
        }

    }

    private fun callApi(
        projectId: String,
        collectionName: String,
        id1: String,
        id2: String,
        id3: String,
        action: String
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

//        utilities.showProgressDialog(context,"Processing ...")
        val apiClient = ApiClient()
        apiClient.getApiService().createPost(pId,cName,Id1,Id2,Id3,latt,lngg,multiImages)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

                    utilities.hideProgressDialog()
                    if (response.isSuccessful) {

//                        utilities.makeToast(context,response.body()!!.message)


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
        sowtFormatter = SOWTFormatter(context)
        myAppDatabase = Room.databaseBuilder(this, MyAppDataBase::class.java, "VISIODB").allowMainThreadQueries().build()

    }


    fun takePhoto(){

        binding.ButtonCameraClick.isEnabled = false
        //get a stable reference of the modifiable image capture use case

        val imageCapture = imageCapture?:return

        //create time-stamped output file to hold the image

        val photoFile = File(outputDirectory,SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())+ ".jpg")



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

//                    btbehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    addImage(savedUri)
                    binding.ButtonCameraClick.isEnabled = true
                    imageFiles.add(photoFile)
                    Log.d("path",photoFile.path.toString())
                    imagePath = photoFile.path.toString()


//                    val iStream: InputStream? = contentResolver.openInputStream(savedUri)
//                    byteImage = getBytes(iStream!!)!!

                    Log.d(TAG, msg)
                }
            })

    }


    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    private fun addImage(savedUri: Uri?) {

        list.add(DataModelBottomSheetImages(savedUri!!))
        binding.recycler.setLayoutManager(LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false))
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

    fun getProjectDirectory():File{
        val mediaDir = resources.getString(R.string.app_name).let {
            File(it,"Project").apply {
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


    private fun updateValues(values: FloatArray) {
        fusedOrientation = values
        if (meanFilterEnabled) {
            fusedOrientation = meanFilter!!.filter(fusedOrientation)
        }
        if (logData) {
            dataLogger!!.setRotation(fusedOrientation)
        }
    }

    private enum class Mode {
        GYROSCOPE_ONLY, COMPLIMENTARY_FILTER, KALMAN_FILTER
    }

    private fun readPrefs(): Mode {

        val mode: Mode = Mode.GYROSCOPE_ONLY
        return mode
    }

}