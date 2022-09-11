package com.hoon.microdustapp.di

import androidx.room.Room
import com.hoon.microdustapp.data.database.AddressDataBase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    // AddressDataBase
    single { AddressDataBase.build(androidApplication()) }
    single { get<AddressDataBase>().addressDao() }

}