package com.hoon.microdustapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.hoon.microdustapp.data.api.RetrofitInstance.getMeasureInfo
import com.hoon.microdustapp.data.api.RetrofitInstance.getNearbyMeasuringStation
import com.hoon.microdustapp.data.model.measure.MeasureResult
import com.hoon.microdustapp.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val PERMISSION_LOCATION_REQUEST_CODE = 101
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient // 현재 위치를 가져옴
    private lateinit var cancellationTokenSource: CancellationTokenSource // 현재 위치 접근 동작을 취소할 수 있는 토큰
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initVariables()
        requestLocationPermission()
    }

    private fun initVariables() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            scope.launch {
                try {
                    val stationName =
                        getNearbyMeasuringStation(location.latitude, location.longitude)
                    val measureResult = getMeasureInfo(stationName!!)

                    updateUI(location.latitude, location.longitude, measureResult)

                } catch (e: NullPointerException) {
                    Toast.makeText(this@MainActivity, "근처 측정소 조회에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    return@launch
                }
            }
        }
    }

    private fun updateUI(latitude: Double, longitude: Double, measureResult: MeasureResult?) {
        updateAddressFromGps(latitude, longitude)
        updateTime()
        updateGrade()
    }

    private fun updateGrade() {
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