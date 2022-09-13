package com.hoon.microdustapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hoon.microdustapp.R
import org.koin.androidx.scope.ScopeActivity

open class BaseActivity(private val transitionMode: TransitionMode = TransitionMode.NONE) :
    ScopeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (transitionMode) {
            TransitionMode.HORIZONTAL -> overridePendingTransition(
                R.anim.horizon_enter,
                R.anim.none
            )
            TransitionMode.VERTICAL -> overridePendingTransition(R.anim.vertical_enter, R.anim.none)
            TransitionMode.NONE -> Unit
        }
    }

    override fun finish() {
        super.finish()

        when (transitionMode) {
            TransitionMode.HORIZONTAL -> overridePendingTransition(R.anim.none, R.anim.horizon_exit)
            TransitionMode.VERTICAL -> overridePendingTransition(R.anim.none, R.anim.vertical_exit)
            TransitionMode.NONE -> Unit
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (isFinishing) {
            when (transitionMode) {
                TransitionMode.HORIZONTAL -> overridePendingTransition(R.anim.none, R.anim.horizon_exit)
                TransitionMode.VERTICAL -> overridePendingTransition(R.anim.none, R.anim.vertical_exit)
                TransitionMode.NONE -> Unit
            }
        }
    }

    enum class TransitionMode {
        NONE,
        HORIZONTAL,
        VERTICAL
    }
}