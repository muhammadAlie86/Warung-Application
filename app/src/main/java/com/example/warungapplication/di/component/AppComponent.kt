package com.example.warungapplication.di.component

import android.content.Context
import com.example.warungapplication.ui.add.AddActivity
import com.example.warungapplication.ui.login.LoginActivity
import com.example.warungapplication.ui.main.MainActivity
import com.example.warungapplication.di.module.DatabaseModule
import com.example.warungapplication.di.module.RepositoryModule
import com.example.warungapplication.di.module.ViewModelModule
import com.example.warungapplication.ui.edit.EditActivity
import com.example.warungapplication.ui.register.RegisterActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(

    modules = [ DatabaseModule::class,ViewModelModule::class, RepositoryModule::class]

)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: RegisterActivity)
    fun inject(activity: AddActivity)
    fun inject(activity: EditActivity)

}