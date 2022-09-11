package com.hoon.microdustapp.extensions

import android.content.Context
import android.widget.Toast

internal fun Context.toast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}