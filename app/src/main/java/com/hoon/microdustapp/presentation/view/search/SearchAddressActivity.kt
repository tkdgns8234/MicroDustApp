package com.hoon.microdustapp.presentation.view.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.databinding.ActivitySearchRegionBinding
import com.hoon.microdustapp.presentation.BaseActivity
import com.hoon.microdustapp.presentation.view.main.MainActivity
import com.hoon.microdustapp.presentation.adapter.AddressAdapter
import com.hoon.microdustapp.presentation.adapter.holder.SearchViewHolder
import org.koin.android.ext.android.inject

class SearchAddressActivity : BaseActivity(TransitionMode.VERTICAL), SearchAddressContract.View {

    override val presenter: SearchAddressContract.Presenter by inject()

    private val binding: ActivitySearchRegionBinding by lazy {
        ActivitySearchRegionBinding.inflate(layoutInflater)
    }
    private lateinit var addressAdapter: AddressAdapter<SearchViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        searchRegion()
        presenter.onCreateView()
    }

    override fun onDestroy() {
        presenter.onDestroyView()
        super.onDestroy()
    }

    override fun showLoadingProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoadingProgress() {
        binding.progressBar.visibility = View.GONE
    }

    override fun updateAddressList(adressList: List<AddressModel>?) {
        addressAdapter.submitList(adressList)
    }

    override fun handleAddFavoriteAddressResult(isExist: Boolean, addressModel: AddressModel) {
        if (!isExist) { // 이미 추가된 favorite address 가 아니면 activity 종료 후 favorite address 에 추가
            val intent = MainActivity.newIntent(this, addressModel)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initViews() = with(binding) {
        ivClose.setOnClickListener { finish() }

        addressAdapter =
            AddressAdapter(AddressAdapter.Companion.HolderType.TYPE_SEARCH) { model: AddressModel ->
                presenter.addFavoriteAddress(model)
            }
        addressRecyclerView.adapter = addressAdapter
        addressRecyclerView.layoutManager =
            LinearLayoutManager(this@SearchAddressActivity, LinearLayoutManager.VERTICAL, false)
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
                        presenter.getAddressListFromKeyword(query)
                    }
                }
            }
            startTime = endTime
        }
    }

    companion object {
        const val TAG = "SearchAddressActivity"
        const val SEARCH_REGIONS_TIME_DELAY = 50L

        fun newIntent(context: Context) =
            Intent(context, SearchAddressActivity::class.java)
    }

}