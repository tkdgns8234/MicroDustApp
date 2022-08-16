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
import com.google.android.material.snackbar.Snackbar
import com.hoon.microdustapp.data.api.RetrofitInstance.getMeasureInfo
import com.hoon.microdustapp.data.api.RetrofitInstance.getNearbyMeasuringStation
import com.hoon.microdustapp.data.model.AirPollutionModel
import com.hoon.microdustapp.data.model.measure.MeasureResult
import com.hoon.microdustapp.databinding.ActivityMainBinding
import com.hoon.microdustapp.ui.adapter.AirPollutionListAdapter
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


/*
코루틴 에러 핸들링 문제
완료

코루틴 수명주기 맞추기 아래 글 참조
https://origogi.github.io/coroutine/%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%8A%A4%EC%BD%94%ED%94%84/

측정소 지도 구현(simple 8도 지도)

예보, 예보시간, 미세먼지 gif 이미지
http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth?serviceKey=cgQ0LFIb5z3rpBZC9JvmId2VpJGKSAY3OSgLG%2B6ueLWpYiJZbpYBQ%2FqWPCUbjgegEqaSU1yCO5JseVLBu21LnQ%3D%3D&returnType=json&numOfRows=700&searchDate=2022-08-16&ver=1.1


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
    private lateinit var recyclerViewAdapter: AirPollutionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initVariables()
        initRecyclerView()
        requestLocationPermission()
    }

    private fun initVariables() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initRecyclerView() = with(binding) {
        recyclerViewAdapter = AirPollutionListAdapter { }
        rvAirPollution.adapter = recyclerViewAdapter
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
                        updateUI(location.latitude, location.longitude, measureResult!!)
                    }
                }
            }
        }
    }

    private fun updateRecyclerView(measureResult: MeasureResult) {
        val models = mutableListOf<AirPollutionModel>()
        models.add(AirPollutionModel("초미세먼지", measureResult.pm25Grade, measureResult.pm25Value))
        models.add(AirPollutionModel("미세먼지", measureResult.pm10Grade, measureResult.pm10Value))
        models.add(AirPollutionModel("통합환경지수", measureResult.khaiGrade, measureResult.khaiValue))
        models.add(AirPollutionModel("오존", measureResult.o3Grade, measureResult.o3Value))
        models.add(AirPollutionModel("이산화질소", measureResult.no2Grade, measureResult.no2Value))
        models.add(AirPollutionModel("일산화탄소", measureResult.coGrade, measureResult.coValue))
        models.add(AirPollutionModel("아황산가스", measureResult.so2Grade, measureResult.so2Value))

        recyclerViewAdapter.submitList(models)
    }

    private fun updateUI(latitude: Double, longitude: Double, measureResult: MeasureResult) {
        updateAddressFromGps(latitude, longitude)
        updateTime()
        updateGradeInfo(measureResult)
        updateRecyclerView(measureResult!!)
    }

    private fun updateGradeInfo(measureResult: MeasureResult) = with(binding) {
        val totalGrade = measureResult.khaiGrade

        ivEmoji.setImageResource(totalGrade.emojiDrawableID)
        tvGrade.text = totalGrade.gradeText
        tvDescription.text = resources.getString(totalGrade.descStringID)
        layoutMainBackground.setBackgroundResource(totalGrade.colorID)

        microDustProgressbarText.text = "${measureResult.pm10Value}/150~"
        microDustProgressbar.progress = measureResult.pm10Value.toIntOrNull() ?: 0
        microDustProgressbar.progressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, totalGrade.colorID))

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