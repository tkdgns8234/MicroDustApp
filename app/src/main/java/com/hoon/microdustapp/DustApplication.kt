package com.hoon.microdustapp

import android.app.Application
import com.hoon.microdustapp.di.appModule
import com.hoon.microdustapp.di.databaseModule
import com.hoon.microdustapp.di.retrofitModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class DustApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(
                if (BuildConfig.DEBUG) {
                    Level.DEBUG
                } else {
                    Level.NONE
                }
            )
            androidContext(this@DustApplication)
            modules(appModule + retrofitModule + databaseModule)
        }
    }
}