package com.hoon.microdustapp.di

import com.hoon.microdustapp.data.repository.AddressRepository
import com.hoon.microdustapp.data.repository.AddressRepositoryImpl
import com.hoon.microdustapp.data.repository.AirPollutionRepository
import com.hoon.microdustapp.data.repository.AirPollutionRepositoryImpl
import com.hoon.microdustapp.presentation.view.main.MainActivity
import com.hoon.microdustapp.presentation.view.main.MainContract
import com.hoon.microdustapp.presentation.view.main.MainPresenter
import com.hoon.microdustapp.presentation.view.search.SearchAddressActivity
import com.hoon.microdustapp.presentation.view.search.SearchAddressContract
import com.hoon.microdustapp.presentation.view.search.SearchAddressPresenter
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {
    single { Dispatchers.IO }

    scope<MainActivity> {
        scoped<MainContract.Presenter> {
            MainPresenter(get<MainActivity>(), get(), get())
        }
    }

    scope<SearchAddressActivity> {
        scoped<SearchAddressContract.Presenter> {
            SearchAddressPresenter(get<SearchAddressActivity>(), get())
        }
    }

    single<AirPollutionRepository> {
        AirPollutionRepositoryImpl(get(), get(), get())
    }

    single<AddressRepository> {
        AddressRepositoryImpl(get(), get(), get())
    }
}