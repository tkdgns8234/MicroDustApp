package com.hoon.microdustapp.presentation

import kotlinx.coroutines.CoroutineScope

interface BasePresenter {
    val scope: CoroutineScope

    fun onCreateView()

    fun onDestroyView()
}