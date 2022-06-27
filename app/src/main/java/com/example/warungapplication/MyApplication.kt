package com.example.warungapplication

import android.app.Application
import com.example.warungapplication.di.component.AppComponent
import com.example.warungapplication.di.component.DaggerAppComponent

open class MyApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}