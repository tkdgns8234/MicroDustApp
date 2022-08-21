package com.hoon.microdustapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoon.microdustapp.data.api.RetrofitInstance
import com.hoon.microdustapp.data.database.RegionDataBase
import com.hoon.microdustapp.data.model.RegionModel
import com.hoon.microdustapp.data.util.constants.Constants.INTENT_KEY_REGION_MODEL
import com.hoon.microdustapp.data.util.constants.Constants.SEARCH_REGIONS_TIME_DELAY
import com.hoon.microdustapp.databinding.ActivitySearchRegionBinding
import com.hoon.microdustapp.ui.BaseActivity
import com.hoon.microdustapp.ui.adapter.SearchRegionAdapter
import kotlinx.coroutines.*

class SearchRegionActivity : BaseActivity(TransitionMode.VERTICAL) {

    companion object {
        const val TAG = "SearchRegionActivity"
    }

    private val binding: ActivitySearchRegionBinding by lazy {
        ActivitySearchRegionBinding.inflate(layoutInflater)
    }
    private val scope = MainScope()
    private val db by lazy { RegionDataBase.getInstance(applicationContext) }
    private lateinit var searchRegionAdapter: SearchRegionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        searchRegion()
        initRecyclerView()
        initSearchRegionAdapter()
    }

    private fun initRecyclerView() = with(binding) {
        recyclerView.adapter = searchRegionAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this@SearchRegionActivity, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun searchRegion() = with(binding) {
        var startTime = System.currentTimeMillis()
        var endTime: Long

        editTextSearchRegion.addTextChangedListener { text: Editable? ->
            endTime = System.currentTimeMillis()

            if (endTime - startTime > SEARCH_REGIONS_TIME_DELAY) {
                text?.let {
                    val query = it.toString().trim()
                    if (query.isNotEmpty()) {
                        var models: MutableList<RegionModel>? = null
                        scope.launch(Dispatchers.IO) {
                            models = searchRegionFromApi(query)
                            searchRegionAdapter.submitList(models)
                        }
                    }
                }
            }
            startTime = endTime
        }
    }

    private suspend fun searchRegionFromApi(query: String): MutableList<RegionModel>? {
        val models = mutableListOf<RegionModel>()

        RetrofitInstance.searchRegion(query)?.let { regions ->
            regions.forEach {
                Log.e(TAG, "${it.regionInfo.regionId}, ${it.regionInfo.description}")
                val model = RegionModel(
                    it.regionInfo.regionId,
                    it.regionInfo.description
                )
                models.add(model)
            }
        }

        return models
    }

    private fun initSearchRegionAdapter() {
        searchRegionAdapter = SearchRegionAdapter { model: RegionModel ->
            scope.launch(Dispatchers.IO) {
                val cnt = db.regionDao().findRegion(model.regionId).count()
                withContext(Dispatchers.Main) {
                    if (cnt == 0) {
                        // 저장되어있지 않다면
                        val intent =
                            Intent(this@SearchRegionActivity, MainActivity::class.java).apply {
                                putExtra(INTENT_KEY_REGION_MODEL, model)
                            }
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }

}