package com.hoon.microdustapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoon.microdustapp.data.api.RetrofitInstance
import com.hoon.microdustapp.data.database.AddressDataBase
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.util.constants.Constants.INTENT_KEY_ADDRESS_MODEL
import com.hoon.microdustapp.data.util.constants.Constants.SEARCH_REGIONS_TIME_DELAY
import com.hoon.microdustapp.databinding.ActivitySearchRegionBinding
import com.hoon.microdustapp.ui.BaseActivity
import com.hoon.microdustapp.ui.adapter.SearchAddressAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchAddressActivity : BaseActivity(TransitionMode.VERTICAL) {

    companion object {
        const val TAG = "SearchRegionActivity"
    }

    private val binding: ActivitySearchRegionBinding by lazy {
        ActivitySearchRegionBinding.inflate(layoutInflater)
    }
    private val scope = MainScope()
    private val db by lazy { AddressDataBase.getInstance(applicationContext) }
    private lateinit var searchAddressAdapter: SearchAddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initCloseButton()
        initRecyclerView()
        searchRegion()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun initCloseButton() {
        binding.ivClose.setOnClickListener { finish() }
    }

    private fun initRecyclerView() = with(binding) {
        registerAdapterCallback()
        recyclerView.adapter = searchAddressAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this@SearchAddressActivity, LinearLayoutManager.VERTICAL, false)
    }

    private fun registerAdapterCallback() {
        searchAddressAdapter = SearchAddressAdapter { model: AddressModel ->
            scope.launch(Dispatchers.IO) {
                val cnt = db.addressDao().find(model.addressName).count()
                if (cnt == 0) {
                    // 저장되어있지 않다면
                    val intent =
                        Intent(this@SearchAddressActivity, MainActivity::class.java).apply {
                            putExtra(INTENT_KEY_ADDRESS_MODEL, model)
                        }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
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
                        scope.launch(Dispatchers.IO) {
                            val models = searchAddressFromApi(query)
                            searchAddressAdapter.submitList(models)
                        }
                    }
                }
            }
            startTime = endTime
        }
    }

    private suspend fun searchAddressFromApi(query: String): MutableList<AddressModel>? {
        val models = mutableListOf<AddressModel>()

        RetrofitInstance.getAddressFromKeyword(query)?.let { response ->
            response.forEach {
                Log.e(TAG, "${it.addressName}, ${it.x}, ${it.y}")
                val model = AddressModel(
                    it.addressName,
                    it.x.toDouble(),
                    it.y.toDouble()
                )
                models.add(model)
            }
        }

        return models
    }

}