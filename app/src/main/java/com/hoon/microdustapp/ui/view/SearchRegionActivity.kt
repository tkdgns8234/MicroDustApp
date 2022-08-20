package com.hoon.microdustapp.ui.view

import android.os.Bundle
import android.text.Editable
import androidx.core.widget.addTextChangedListener
import com.hoon.microdustapp.data.api.RetrofitInstance
import com.hoon.microdustapp.data.model.RegionModel
import com.hoon.microdustapp.data.util.constants.Constants.SEARCH_REGIONS_TIME_DELAY
import com.hoon.microdustapp.databinding.ActivitySearchRegionBinding
import com.hoon.microdustapp.ui.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchRegionActivity : BaseActivity(TransitionMode.VERTICAL) {
    private val binding: ActivitySearchRegionBinding by lazy {
        ActivitySearchRegionBinding.inflate(layoutInflater)
    }
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewBinding()
        initRecyclerView()
    }

    private fun initRecyclerView() = with(binding) {
//        recyclerView.adapter
//        recyclerView.layoutManager
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun viewBinding() = with(binding) {
        var startTime = System.currentTimeMillis()
        var endTime: Long

        editTextSearchRegion.addTextChangedListener { text: Editable? ->
            endTime = System.currentTimeMillis()

            if (endTime - startTime < SEARCH_REGIONS_TIME_DELAY) {
                text?.let {
                    val query = it.toString().trim()
                    if (query.isNotEmpty()) {
                        val models = searchRegion(query)

                    }
                }
            }
            startTime = endTime
        }
    }

    private fun searchRegion(query: String): MutableList<RegionModel> {
        val models = mutableListOf<RegionModel>()

        scope.launch(Dispatchers.IO) {
            RetrofitInstance.searchRegion(query)?.let { regions ->
                regions.forEach {

                    val model = RegionModel(
                        it.regionInfo.regionId,
                        it.regionInfo.description
                    )
                    models.add(model)
                }
            }
        }

        return models
    }


}