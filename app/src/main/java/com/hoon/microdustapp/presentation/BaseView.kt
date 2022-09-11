package com.hoon.microdustapp.presentation

interface BaseView<PresenterT : BasePresenter> {

    val presenter: PresenterT
}