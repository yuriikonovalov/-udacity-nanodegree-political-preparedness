package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.data.ElectionsRepository
import com.example.android.politicalpreparedness.data.ElectionsRepositoryImpl
import com.example.android.politicalpreparedness.data.local.ElectionsLocalDataSource
import com.example.android.politicalpreparedness.data.local.ElectionsLocalDataSourceImpl
import com.example.android.politicalpreparedness.data.remote.ElectionsRemoteDataSource
import com.example.android.politicalpreparedness.data.remote.ElectionsRemoteDataSourceImpl
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.network.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val viewModelModule = module {
            viewModel { ElectionsViewModel(get(), get()) }
        }

        val networkModule = module {
            factory { CivicsHttpClient.getClient() }
            factory { CivicsApi.provideMoshi() }
            factory { CivicsApi.provideRetrofit(get(), get()) }
            single { CivicsApi.provideCivicsApi(get()) }
        }

        val dataModule = module {
            single { ElectionDatabase.getInstance(this@MyApplication) }
            single { ElectionsLocalDataSourceImpl(get()) as ElectionsLocalDataSource }
            single { ElectionsRemoteDataSourceImpl(get()) as ElectionsRemoteDataSource }
            single { ElectionsRepositoryImpl(get(), get()) as ElectionsRepository }
        }

        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(viewModelModule, dataModule, networkModule))
        }
    }
}

