package com.hoon.microdustapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.hoon.microdustapp.data.api.RetrofitInstance
import com.hoon.microdustapp.data.api.RetrofitInstance.getMeasureInfo
import com.hoon.microdustapp.data.api.RetrofitInstance.getNearbyMeasuringStation
import com.hoon.microdustapp.data.model.AirPollutionModel
import com.hoon.microdustapp.data.model.forecast.ForecastItem
import com.hoon.microdustapp.data.model.measure.MeasureResult
import com.hoon.microdustapp.data.util.constants.AirPollution
import com.hoon.microdustapp.databinding.ActivityMainBinding
import com.hoon.microdustapp.ui.adapter.AirPollutionListAdapter
import com.hoon.microdustapp.ui.adapter.ForecastVideoAdapter
import kotlinx.coroutines.*
import java.text.DateFormat
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
-> 실제 layout 사이즈 측정해서 guideline 위치 설정 (~dp)
https://bonoogi.postype.com/post/1467632
https://onedaycodeing.tistory.com/60

측정소 지도 구현(simple 8도 지도)

예보, 예보시간, 미세먼지 gif 이미지
http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth?serviceKey=cgQ0LFIb5z3rpBZC9JvmId2VpJGKSAY3OSgLG%2B6ueLWpYiJZbpYBQ%2FqWPCUbjgegEqaSU1yCO5JseVLBu21LnQ%3D%3D&returnType=json&numOfRows=700&searchDate=2022-08-16&ver=1.1

뷰페이저 + 인디케이터 만들기

8도 미세먼지 지도 만들기 -> ui 어떻게 위치시킬지 생각해보기

행동요령 -> 거의 null만 들어옴

측정정보
측정소
날짜
출처

로딩 구현

refresh layout으로 변경

하단 rv progress 수치 변경

애니메이션 구현

공유기능 구현

지역추가 기능 구현 dsts

 */

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val PERMISSION_LOCATION_REQUEST_CODE = 101
    }

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val scope = MainScope()
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 현재 위치를 가져옴
    private lateinit var cancellationTokenSource: CancellationTokenSource // 현재 위치 접근 동작을 취소할 수 있는 토큰
    private lateinit var airPollutionListAdapter: AirPollutionListAdapter
    private lateinit var forecastVideoAdapter: ForecastVideoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initVariables()
        initPollutionListAdapter()
        initForecastVideoAdapter()
        requestLocationPermission()
        getForecastInfo()
    }

    private fun initVariables() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initForecastVideoAdapter() = with(binding) {
        forecastVideoAdapter = ForecastVideoAdapter()
        forecastVideoViewPager.adapter = forecastVideoAdapter
    }

    private fun initPollutionListAdapter() = with(binding) {
        airPollutionListAdapter = AirPollutionListAdapter { }
        rvAirPollution.adapter = airPollutionListAdapter
        rvAirPollution.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource.cancel()
        scope.cancel()
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
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY, // 한번만 요청할것이기에 priority high 로 요청
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->

            val handler = CoroutineExceptionHandler { _, exception ->
                Toast.makeText(this, "미세먼지 측정 정보를 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "${exception.printStackTrace()}")
            }

            scope.launch(handler) {
                withContext(Dispatchers.IO) {
                    val stationName =
                        getNearbyMeasuringStation(location.latitude, location.longitude)
                    val measureResult = getMeasureInfo(stationName!!)

                    withContext(Dispatchers.Main) {
                        updateMainUI(location.latitude, location.longitude, measureResult!!)
                    }
                }
            }
        }
    }

    private fun getForecastInfo() = scope.launch(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        if (hour < 5) {
            // 새벽 5시 이전인경우 하루 이전 관측 정보를 보여준다.
            calendar.add(Calendar.DATE, -1)
        }
        val searchDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)

        RetrofitInstance.getForecastInfo(searchDate)?.let { forecastItems ->
            scope.launch(Dispatchers.Main) {
                updateForecastUI(forecastItems)
            }
        }
    }


    private fun updateForecastUI(forecastItems: List<ForecastItem>) = with(binding) {

        var imageUrlMicroDust: String? = null
        var imageUrlUltraMicroDust: String? = null

        forecastItems.forEach { item ->
            if (item.imageUrl7.endsWith("fileName=").not()) { // url 쿼리 스트링이 잘 설정되어있는 경우
                imageUrlMicroDust = item.imageUrl7
            }
            if (item.imageUrl8.endsWith("fileName=").not()) {
                imageUrlUltraMicroDust = item.imageUrl8
            }
            item.informData
        }
        forecastVideoAdapter.submitList(listOf(imageUrlMicroDust, imageUrlUltraMicroDust))

        forecastItems.get(0)?.let {
            tvTodayDesc.text = it.informCause.split("[미세먼지] ")[1] // 예보 내용
            forecastTime.text = it.dataTime // 예보 업데이트 시간
        }

        forecastItems.get(1)?.let {
            tvTomorrowDesc.text = it.informCause.split("[미세먼지] ")[1] // 예보 내용
        }
    }

    private fun updateRecyclerView(measureResult: MeasureResult) {
        val models = mutableListOf<AirPollutionModel>()

        models.add(
            AirPollutionModel(
                AirPollution.PM_10.pollutionName,
                measureResult.pm10Grade,
                measureResult.pm10Value,
                AirPollution.PM_10.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.PM_2_5.pollutionName,
                measureResult.pm25Grade,
                measureResult.pm25Value,
                AirPollution.PM_2_5.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.CAI.pollutionName,
                measureResult.khaiGrade,
                measureResult.khaiValue,
                AirPollution.CAI.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.O3.pollutionName,
                measureResult.o3Grade,
                measureResult.o3Value,
                AirPollution.O3.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.NO2.pollutionName,
                measureResult.no2Grade,
                measureResult.no2Value,
                AirPollution.NO2.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.CO.pollutionName,
                measureResult.coGrade,
                measureResult.coValue,
                AirPollution.CO.maximumValue
            )
        )
        models.add(
            AirPollutionModel(
                AirPollution.SO2.pollutionName,
                measureResult.so2Grade,
                measureResult.so2Value,
                AirPollution.SO2.maximumValue
            )
        )

        airPollutionListAdapter.submitList(models)
    }

    private fun updateMainUI(latitude: Double, longitude: Double, measureResult: MeasureResult) {
        updateAddressFromGps(latitude, longitude)
        updateTime()
        updateGradeInfo(measureResult)
        updateRecyclerView(measureResult!!)
    }

    private fun updateGradeInfo(measureResult: MeasureResult) = with(binding) {
        val currentGrade = measureResult.pm10Grade
        val currentVal = measureResult.pm10Value

        ivEmoji.setImageResource(currentGrade.emojiDrawableID)
        tvGrade.text = currentGrade.gradeText
        tvDescription.text = resources.getString(currentGrade.descStringID)
        layoutMainBackground.setBackgroundResource(currentGrade.colorID)

        microDustProgressbarText.text = "${currentVal}/150~"
        microDustProgressbar.progress = currentVal.toIntOrNull() ?: 0
        microDustProgressbar.progressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, currentGrade.colorID))
    }

    private fun updateAddressFromGps(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.KOREAN)
        val address = geocoder.getFromLocation(latitude, longitude, 1).firstOrNull()

        if (address != null) {
            binding.tvLocation.text = "${address.locality ?: ""} " +
                    "${address.subLocality ?: ""} " +
                    "${address.thoroughfare ?: ""}"
        } else {
            Toast.makeText(this, "주소 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTime() {
        val now = System.currentTimeMillis()
        binding.tvTime.text = SimpleDateFormat("HH:mma").format(Date(now))
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