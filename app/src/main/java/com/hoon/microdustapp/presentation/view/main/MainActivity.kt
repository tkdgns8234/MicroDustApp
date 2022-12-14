package com.hoon.microdustapp.presentation.view.main

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
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.hoon.microdustapp.R
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.model.AirPollutionModel
import com.hoon.microdustapp.data.model.ForecastModel
import com.hoon.microdustapp.data.response.measure.Grade
import com.hoon.microdustapp.data.response.measure.MeasureResult
import com.hoon.microdustapp.data.response.measuringstation.StationInfo
import com.hoon.microdustapp.data.model.AirPollution
import com.hoon.microdustapp.databinding.ActivityMainBinding
import com.hoon.microdustapp.databinding.LayoutDrawableViewBinding
import com.hoon.microdustapp.databinding.LayoutForecastViewBinding
import com.hoon.microdustapp.databinding.LayoutMainViewBinding
import com.hoon.microdustapp.extensions.toast
import com.hoon.microdustapp.presentation.BaseActivity
import com.hoon.microdustapp.presentation.adapter.AirPollutionListAdapter
import com.hoon.microdustapp.presentation.adapter.ForecastVideoAdapter
import com.hoon.microdustapp.presentation.adapter.FavoriteAddressAdapter
import com.hoon.microdustapp.presentation.adapter.callback.ItemTouchHelperCallback
import com.hoon.microdustapp.presentation.view.search.SearchAddressActivity
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 1. ?????? ???, ?????? ???????????? ?????? tm ????????? ?????????(kakao api)
 * 2. tm ???????????? ???????????? ?????? ???????????? ???????????? ??????
 * 3. ?????? ???????????? ????????? ????????? ?????? (???, ?????? ??????, ????????? ??? ???)
 * 4. ?????? ???????????? ??????, ?????? ?????? ????????? ?????????
 *
 * view -> ????????? UI ?????? activity ???????????? ?????? ????????? ??????????????? ??????
 * presenter -> ???????????? ??????(?????? ????????? ??????, Repository??? ??????????????? ??????.)
 *
 */

class MainActivity : BaseActivity(), MainContract.View {

    override val presenter: MainContract.Presenter by inject()

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewBinding: LayoutMainViewBinding by lazy { mainBinding.layoutMainView }
    private val forecastViewBinding: LayoutForecastViewBinding by lazy { mainBinding.layoutForecastView }
    private val drawableViewBinding: LayoutDrawableViewBinding by lazy { mainBinding.layoutDrawableView }

    // ?????? ?????? ?????? ????????? ????????? ??? ?????? ??????
    private val cancellationToken: CancellationTokenSource by lazy { CancellationTokenSource() }

    // ?????? ?????? ??????
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    private lateinit var airPollutionListAdapter: AirPollutionListAdapter
    private lateinit var forecastVideoAdapter: ForecastVideoAdapter
    private lateinit var addressAdapter: FavoriteAddressAdapter

    override fun showLoadingProgress() {
        mainBinding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoadingProgress() {
        mainBinding.progressBar.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        requestLocationPermission()
        initViews()
        bindViews()

        presenter.onCreateView()
    }

    private fun initViews() {
        setGuideLinePosition()
        initPollutionListAdapter()
        initForecastVideoAdapter()
        initDrawableAddressAdapter()
    }

    private fun initPollutionListAdapter() = with(mainViewBinding) {
        airPollutionListAdapter = AirPollutionListAdapter()
        rvAirPollution.adapter = airPollutionListAdapter
        rvAirPollution.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initForecastVideoAdapter() = with(forecastViewBinding) {
        forecastVideoAdapter = ForecastVideoAdapter()
        forecastVideoViewPager.adapter = forecastVideoAdapter
        forecastVideoViewPager.registerOnPageChangeCallback(pageChangeCallback())
    }

    private fun initDrawableAddressAdapter() = with(drawableViewBinding) {
        addressAdapter = FavoriteAddressAdapter() {
            setPosition(it.y, it.x)
            updateUI()
            mainBinding.root.closeDrawer(Gravity.LEFT)
        }
        recyclerView.adapter = addressAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

        val itemTouchHelperCallback = ItemTouchHelper(ItemTouchHelperCallback { idx ->
            val addressModel = addressAdapter.currentList[idx]
            presenter.deleteAddressDB(addressModel)
        })
        itemTouchHelperCallback.attachToRecyclerView(recyclerView)
    }

    private fun bindViews() {
        mainBinding.swipeLayout.setOnRefreshListener {
            updateUI()
            mainBinding.swipeLayout.isRefreshing = false
        }

        // ScrollView??? SwipeRefreshLayout ?????? ???????????? ??????
        mainBinding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            mainBinding.swipeLayout.isEnabled = (mainBinding.scrollView.scrollY == 0)
        }

        drawableViewBinding.btnSearchRegion.setOnClickListener {
            val intent = SearchAddressActivity.newIntent(this@MainActivity)
            startActivityForResult.launch(intent)
        }
    }

    private fun updateUI() {
        presenter.updateAirPollutionInfo(currentLatitude!!, currentLongitude!!)

        mainBinding.scrollView.animate()
            .alpha(1F)
            .start()
    }

    override fun updateFavoriteAddress(addressList: List<AddressModel>) {
        addressAdapter.submitList(addressList)
    }

    override fun onDestroy() {
        cancellationToken.cancel()
        presenter.onDestroyView()
        super.onDestroy()
    }

    override fun updateForecastUI(forecastItems: List<ForecastModel>) = with(forecastViewBinding) {
        val forecastVideoUrls = mutableListOf<String>()

        forecastItems.forEach { item ->
            if (item.imageUrlMicroDust.endsWith("fileName=").not()) { // url ?????? ???????????? ??? ?????????????????? ??????
                forecastVideoUrls.add(item.imageUrlMicroDust)
            }
            if (item.imageUrlUltraMicroDust.endsWith("fileName=").not()) {
                forecastVideoUrls.add(item.imageUrlUltraMicroDust)
            }
        }
        forecastVideoAdapter.submitList(forecastVideoUrls)

        // ?????? ??????
        forecastItems.get(0)?.let {
            tvTodayDesc.text = it.contents.split("[????????????] ")[1] // ?????? ??????
            forecastTime.text = it.updateTime // ?????? ???????????? ??????
        }

        // ?????? ??????
        forecastItems.get(1)?.let {
            tvTomorrowDesc.text = it.contents.split("[????????????] ")[1] // ?????? ??????
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

    override fun updateMainUI(
        measureResult: MeasureResult,
        stationInfo: StationInfo
    ) {
        updateAddressFromGps()
        updateTimeText()
        updateGradeUI(measureResult)
        updateAirPollutionList(measureResult)
        mainBinding.tvMeasureStationDesc.text =
            "${stationInfo.stationName} ?????????\n${stationInfo.addr}"
        mainBinding.layoutMainView.btnDrawable.setOnClickListener {
            mainBinding.root.openDrawer(Gravity.LEFT)
        }

        // ?????? ????????? ?????? ????????? ?????? background, progress bar ??????
        mainViewBinding.backgroundView.visibility = View.VISIBLE
        mainViewBinding.microDustProgressbar.visibility = View.VISIBLE
    }

    private fun updateGradeUI(measureResult: MeasureResult) = with(mainViewBinding) {
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

    private fun updateAddressFromGps() {
        val geocoder = Geocoder(this, Locale.KOREAN) // ???, ?????? <-> ?????? ?????? api
        val address =
            geocoder.getFromLocation(currentLatitude!!, currentLongitude!!, 1).firstOrNull()

        if (address != null) {
            // locality, subLocality: ???, ???, ???
            // thoroughfare: ???
            mainViewBinding.tvLocation.text = "${address.locality ?: ""} " +
                    "${address.subLocality ?: ""} " +
                    "${address.thoroughfare ?: ""}"
        } else {
            Toast.makeText(this, "?????? ????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimeText() {
        val now = System.currentTimeMillis()
        mainViewBinding.tvTime.text = SimpleDateFormat("HH:mm a").format(Date(now))
    }

    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    )
    { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val addressModel =
                result.data?.getParcelableExtra<AddressModel>(INTENT_KEY_ADDRESS_MODEL)
            addressModel?.let {
                presenter.insertAddressDB(it)
                //TODO flow ??? ?????? ?????? update ????????? ??????
            }
        }
    }

    private fun setGuideLinePosition() {
        val height = getDeviceHeight()

        with(mainViewBinding) {
            guideLine1.setGuidelineBegin((height * 0.63).toInt())
            guideLine2.setGuidelineBegin((height * 0.93).toInt())
        }
    }

    private fun getDeviceHeight(): Int {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return if (Build.VERSION.SDK_INT >= 30) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.y
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            val isGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED

            if (isGranted) {
                updateCurrentPosition()
            } else {
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateCurrentPosition() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.getCurrentLocation( // ??????????????? ?????????
            LocationRequest.PRIORITY_HIGH_ACCURACY, // ????????? ????????????????????? priority high ??? ??????
            cancellationToken.token
        ).addOnSuccessListener { location ->
            // ?????? ?????? update
            setPosition(location.latitude, location.longitude)
            updateUI()
        }
    }

    private fun setPosition(latitude: Double, longitude: Double) {
        currentLatitude = latitude
        currentLongitude = longitude
    }

    private fun pageChangeCallback(): ViewPager2.OnPageChangeCallback {
        with(forecastViewBinding) {
            return object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    ivIndicator1.setImageDrawable(getDrawable(R.drawable.shape_oval_gray))
                    ivIndicator2.setImageDrawable(getDrawable(R.drawable.shape_oval_gray))

                    when (position) {
                        0 -> {
                            ivIndicator1.setImageDrawable(getDrawable(R.drawable.shape_oval_white))
                            forecastVideoSubTitle.text = "???????????? (??????: ??????????????????)"
                        }
                        1 -> {
                            ivIndicator2.setImageDrawable(getDrawable(R.drawable.shape_oval_white))
                            forecastVideoSubTitle.text = "??????????????? (??????: ??????????????????)"
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
        const val PERMISSION_LOCATION_REQUEST_CODE = 101
        const val INTENT_KEY_ADDRESS_MODEL = "AddressModel"
        fun newIntent(context: Context, addressModel: AddressModel) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(INTENT_KEY_ADDRESS_MODEL, addressModel)
            }
    }
}