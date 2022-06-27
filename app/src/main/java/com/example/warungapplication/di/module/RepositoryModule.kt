package com.example.warungapplication.di.module

import com.example.warungapplication.data.repository.IWarungRepository
import com.example.warungapplication.data.repository.WarungRepository
import dagger.Binds
import dagger.Module

@Module(includes = [DatabaseModule::class])
abstract class RepositoryModule {

    @Binds
    abstract fun provideMovieRepository(movieRepository: WarungRepository) : IWarungRepository

}