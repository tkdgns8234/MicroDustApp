package com.hoon.microdustapp.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Point
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.hoon.microdustapp.R
import com.hoon.microdustapp.data.api.RetrofitInstance
import com.hoon.microdustapp.data.api.RetrofitInstance.getMeasureInfo
import com.hoon.microdustapp.data.api.RetrofitInstance.getNearbyMeasuringStation
import com.hoon.microdustapp.data.database.AddressDataBase
import com.hoon.microdustapp.data.database.AddressEntity
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.model.AirPollutionModel
import com.hoon.microdustapp.data.model.forecast.ForecastItem
import com.hoon.microdustapp.data.model.measure.Grade
import com.hoon.microdustapp.data.model.measure.MeasureResult
import com.hoon.microdustapp.data.model.measuringstation.StationInfo
import com.hoon.microdustapp.data.util.AirPollution
import com.hoon.microdustapp.data.util.constants.Constants.INTENT_KEY_ADDRESS_MODEL
import com.hoon.microdustapp.databinding.ActivityMainBinding
import com.hoon.microdustapp.databinding.LayoutDrawableViewBinding
import com.hoon.microdustapp.databinding.LayoutForecastViewBinding
import com.hoon.microdustapp.databinding.LayoutMainViewBinding
import com.hoon.microdustapp.ui.BaseActivity
import com.hoon.microdustapp.ui.adapter.AirPollutionListAdapter
import com.hoon.microdustapp.ui.adapter.ForecastVideoAdapter
import com.hoon.microdustapp.ui.adapter.SearchAddressAdapter
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

/*
코루틴 에러 핸들링 문제
완료

코루틴 수명주기 맞추기 아래 글 참조
https://origogi.github.io/coroutine/%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%8A%A4%EC%BD%94%ED%94%84/

progress
// max값을 먼저 지정해야함 (sdk 코드 보면 progress 적용 시 값을 max 이상 min 이하로 지정할 수 없음)


여러화면 대응하기
guideline + constraintlayout + dimens
-> dimens 만으로 세부하게 모두 커버하기 힘듦
-> 실제 layout 사이즈 측정해서 guideline 위치 설정 (~dp) -> https://ryan94.tistory.com/32 참조
https://bonoogi.postype.com/post/1467632
https://onedaycodeing.tistory.com/60


페이지 로딩 구현하기, 각 layout alpha 0 에서 1로 변경하기 -> 5번

지역 추가 기능 구현 ->  Tmap api, drawable layout + activity 화면전환 사용하기 !!
https://greedy0110.tistory.com/52 화면전환 애니메이션 참고 글


측정 정보(자료 출처 추가)  -> 완료
viewpager 인디케이터 추가   -> (미세 - 초미세 전환), 영상 출처 -> 완료
로딩 구현 ->완료
refresh layout으로 변경 -> 완료
애니메이션 구현 -> 완료
지역추가 기능 구현 -> 완료

TODO::
 아키텍처 패턴 적용
 공유기능 구현
 premission 거부했을 때 로직 추가
 앱의 string 관리
 전체적인 리팩토링
 xml 변수 명 변경
 rest api 사용 시 exception 관리 철저
 */


class MainActivity : BaseActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val PERMISSION_LOCATION_REQUEST_CODE = 101
    }

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewBinding: LayoutMainViewBinding by lazy { mainBinding.layoutMainView }
    private val forecastViewBinding: LayoutForecastViewBinding by lazy { mainBinding.layoutForecastView }
    private val drawableViewBinding: LayoutDrawableViewBinding by lazy { mainBinding.layoutDrawableView }

    private lateinit var fusedLocationClient: FusedLocationProviderClient // 현재 위치를 가져옴
    private lateinit var cancellationTokenSource: CancellationTokenSource // 현재 위치 접근 동작을 취소할 수 있는 토큰

    private lateinit var airPollutionListAdapter: AirPollutionListAdapter
    private lateinit var forecastVideoAdapter: ForecastVideoAdapter
    private lateinit var searchAddressAdapter: SearchAddressAdapter

    private val scope = MainScope()
    private val db by lazy { AddressDataBase.getInstance(applicationContext) }

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        bindViews()
        initVariables()
        initPollutionListAdapter()
        initForecastVideoAdapter()
        initSearchRegionAdapter()

        requestLocationPermission()
        initGuideLinePosition()
        initDrawbleView()

        // db에 저장되어있는 regionModel load
        updateAddressList()
    }

    private fun updateUI(latitude: Double, longitude: Double) {
        mainBinding.progressBar.visibility = View.VISIBLE
        updateMainUIWithCoordinate(latitude, longitude)
        updateForecast()
        mainBinding.progressBar.visibility = View.GONE
        mainBinding.scrollView.animate()
            .alpha(1F)
            .start()
    }

    private fun bindViews() {
        mainBinding.swipeLayout.setOnRefreshListener {
            updateUI(currentLatitude!!, currentLongitude!!)
            mainBinding.swipeLayout.isRefreshing = false
        }

        // ScrollView와 SwipeRefreshLayout 함께 사용하기 위함
        mainBinding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            mainBinding.swipeLayout.isEnabled = (mainBinding.scrollView.scrollY == 0)
        }
    }

    private fun updateAddressList() = scope.launch(Dispatchers.IO) {
        val models = mutableListOf<AddressModel>()

        val entities = db.addressDao().getAll()
        entities.forEach { entity ->
            val model = AddressModel(entity.addressName, entity.x, entity.y)
            models.add(model)
        }
        withContext(Dispatchers.Main) {
            searchAddressAdapter.submitList(models)
        }
    }

    private fun initSearchRegionAdapter() {
        searchAddressAdapter = SearchAddressAdapter {
            updateUI(it.y, it.x)
            mainBinding.root.closeDrawer(Gravity.LEFT)
        }
        drawableViewBinding.recyclerView.adapter = searchAddressAdapter
        drawableViewBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun initDrawbleView() {
        drawableViewBinding.btnSearchRegion.setOnClickListener {
            val intent = Intent(this, SearchAddressActivity::class.java)
            startActivityForResult.launch(intent)
        }
    }

    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val addressModel =
                result.data?.getParcelableExtra<AddressModel>(INTENT_KEY_ADDRESS_MODEL)
            addressModel?.let {
                scope.launch(Dispatchers.IO) {
                    // db 저장
                    val entity = AddressEntity(it.addressName, it.x, it.y)
                    db.addressDao().insert(entity)

                    // adapter update
                    val models = searchAddressAdapter.currentList.toMutableList() //call by ref 방지
                    models.add(it)
                    withContext(Dispatchers.Main) {
                        searchAddressAdapter.submitList(models)
                    }
                }
            }
        }
    }

    private fun initGuideLinePosition() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val height = if (Build.VERSION.SDK_INT >= 30) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.y
        }

        with(mainViewBinding) {

            guideLine1.setGuidelineBegin((height * 0.63).toInt())
            guideLine2.setGuidelineBegin((height * 0.93).toInt())
        }
    }

    private fun initVariables() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initForecastVideoAdapter() = with(forecastViewBinding) {
        forecastVideoAdapter = ForecastVideoAdapter()
        forecastVideoViewPager.adapter = forecastVideoAdapter
        forecastVideoViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                ivIndicator1.setImageDrawable(getDrawable(R.drawable.shape_oval_gray))
                ivIndicator2.setImageDrawable(getDrawable(R.drawable.shape_oval_gray))

                when(position) {
                    0 -> {
                        ivIndicator1.setImageDrawable(getDrawable(R.drawable.shape_oval_white))
                        forecastVideoSubTitle.text = "미세먼지"
                        forecastVideoSubTitle.text = "미세먼지 (출처: 한국환경공단)"
                    }
                    1 -> {
                        ivIndicator2.setImageDrawable(getDrawable(R.drawable.shape_oval_white))
                        forecastVideoSubTitle.text = "초미세먼지"
                        forecastVideoSubTitle.text = "초미세먼지 (출처: 한국환경공단)"
                    }
                }
            }
        })
    }

    private fun initPollutionListAdapter() = with(mainViewBinding) {
        airPollutionListAdapter = AirPollutionListAdapter { }
        rvAirPollution.adapter = airPollutionListAdapter
        rvAirPollution.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroy() {
        scope.cancel()
        cancellationTokenSource.cancel()

        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            val result = grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED

            if (result) {
                getAirCondition()
            } else {
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getAirCondition() {
        // 위치정보를 가져옴
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY, // 한번만 요청할것이기에 priority high 로 요청
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            updateUI(location.latitude, location.longitude)
        }
    }

    private fun updateMainUIWithCoordinate( // latitude: 위도,y longitude: 경도,x
        latitude: Double,
        longitude: Double
    ) {
        currentLatitude = latitude
        currentLongitude = longitude

        //scope 별 catch 구문 2개 사용하는것보다 scope에 exception handler 설정
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            Toast.makeText(this, "미세먼지 측정 정보를 불러오는데 실패하였습니다.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "${exception.printStackTrace()}")
        }

        scope.launch(errorHandler) {
            withContext(Dispatchers.IO) {
                val stationInfo = getNearbyMeasuringStation(latitude, longitude)
                val measureResult = getMeasureInfo(stationInfo?.stationName!!)

                withContext(Dispatchers.Main) {
                    updateMainUI(
                        latitude,
                        longitude,
                        measureResult!!,
                        stationInfo
                    )
                }
            }
        }
    }

    private fun updateForecast() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        if (hour < 5) {
            // 새벽 5시 이전인경우 하루 이전 관측 정보를 보여준다. (서버 관측 정보가 업데이트 되지 않았기 때문)
            calendar.add(Calendar.DATE, -1)
        }
        val searchDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)

        scope.launch(Dispatchers.IO) {
            RetrofitInstance.getForecastInfo(searchDate)?.let { forecastItems ->
                withContext(Dispatchers.Main) {
                    updateForecastUI(forecastItems)
                }
            }
        }
    }


    private fun updateForecastUI(forecastItems: List<ForecastItem>) = with(forecastViewBinding) {

        var imageUrlMicroDust: String? = null
        var imageUrlUltraMicroDust: String? = null

        forecastItems.forEach { item ->
            if (item.imageUrl7.endsWith("fileName=").not()) { // url 쿼리 스트링이 잘 설정되어있는 경우
                imageUrlMicroDust = item.imageUrl7
            }
            if (item.imageUrl8.endsWith("fileName=").not()) {
                imageUrlUltraMicroDust = item.imageUrl8
            }
        }
        forecastVideoAdapter.submitList(listOf(imageUrlMicroDust, imageUrlUltraMicroDust))

        // 오늘 예보
        forecastItems.get(0)?.let {
            tvTodayDesc.text = it.informCause.split("[미세먼지] ")[1] // 예보 내용
            forecastTime.text = it.dataTime // 예보 업데이트 시간
        }

        // 내일 예보
        forecastItems.get(1)?.let {
            tvTomorrowDesc.text = it.informCause.split("[미세먼지] ")[1] // 예보 내용
        }
    }

    private fun updateAirPollutionList(measureResult: MeasureResult) {
        val models = mutableListOf<AirPollutionModel>()

        models.add(
            AirPollutionModel(
                AirPollution.PM_10.pollutionName,
                measureResult.pm10Grade ?: Grade.UNKNOWN,
                measureResult.pm10Value,
                AirPollution.PM_10.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.PM_2_5.pollutionName,
                measureResult.pm25Grade ?: Grade.UNKNOWN,
                measureResult.pm25Value,
                AirPollution.PM_2_5.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.CAI.pollutionName,
                measureResult.khaiGrade ?: Grade.UNKNOWN,
                measureResult.khaiValue,
                AirPollution.CAI.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.O3.pollutionName,
                measureResult.o3Grade ?: Grade.UNKNOWN,
                measureResult.o3Value,
                AirPollution.O3.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.NO2.pollutionName,
                measureResult.no2Grade ?: Grade.UNKNOWN,
                measureResult.no2Value,
                AirPollution.NO2.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.CO.pollutionName,
                measureResult.coGrade ?: Grade.UNKNOWN,
                measureResult.coValue,
                AirPollution.CO.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.SO2.pollutionName,
                measureResult.so2Grade ?: Grade.UNKNOWN,
                measureResult.so2Value,
                AirPollution.SO2.maximumValue
            )
        )

        airPollutionListAdapter.submitList(models)
    }

    private fun updateMainUI(latitude: Double, longitude: Double, measureResult: MeasureResult, stationInfo: StationInfo) {
        updateAddressFromGps(latitude, longitude)
        updateTimeText()
        updateGradeInfo(measureResult)
        updateAirPollutionList(measureResult)
        mainBinding.tvMeasureStationDesc.text =
            "${stationInfo.stationName} 측정소\n${stationInfo.addr}"
        mainBinding.layoutMainView.ivRegionList.setOnClickListener {
            mainBinding.root.openDrawer(Gravity.LEFT)
        }
    }

    private fun updateGradeInfo(measureResult: MeasureResult) = with(mainViewBinding) {
        val currentGrade = measureResult.pm10Grade ?: Grade.UNKNOWN
        val currentVal = measureResult.pm10Value

        ivEmoji.setImageResource(currentGrade.emojiDrawableID)
        tvGrade.text = currentGrade.gradeText
        tvDescription.text = resources.getString(currentGrade.descStringID)
        mainBinding.activityMain.setBackgroundResource(currentGrade.colorID)

        microDustProgressbarText.text = "${currentVal}/150~"
        microDustProgressbar.progress = currentVal.toIntOrNull() ?: 0
        microDustProgressbar.progressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, currentGrade.colorID))
    }

    private fun updateAddressFromGps(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.KOREAN)
        val address = geocoder.getFromLocation(latitude, longitude, 1).firstOrNull()

        if (address != null) {
            mainViewBinding.tvLocation.text = "${address.locality ?: ""} " +
                    "${address.subLocality ?: ""} " +
                    "${address.thoroughfare ?: ""}"
        } else {
            Toast.makeText(this, "주소 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimeText() {
        val now = System.currentTimeMillis()
        mainViewBinding.tvTime.text = SimpleDateFormat("HH:mm a").format(Date(now))
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_LOCATION_REQUEST_CODE
        )
    }
}