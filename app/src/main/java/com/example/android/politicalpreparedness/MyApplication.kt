package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.data.Repository
import com.example.android.politicalpreparedness.data.RepositoryImpl
import com.example.android.politicalpreparedness.data.local.LocalDataSource
import com.example.android.politicalpreparedness.data.local.LocalDataSourceImpl
import com.example.android.politicalpreparedness.data.remote.RemoteDataSource
import com.example.android.politicalpreparedness.data.remote.RemoteDataSourceImpl
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
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
            viewModel { params ->
                VoterInfoViewModel(currentElectionId = params.get(), division = params.get(), get(), get()) }
        }

        val networkModule = module {
            factory { CivicsHttpClient.getClient() }
            factory { CivicsApi.provideMoshi() }
            factory { CivicsApi.provideRetrofit(get(), get()) }
            single { CivicsApi.provideCivicsApi(get()) }
        }

        val dataModule = module {
            single { ElectionDatabase.getInstance(this@MyApplication) }
            single { LocalDataSourceImpl(get()) as LocalDataSource }
            single { RemoteDataSourceImpl(get()) as RemoteDataSource }
            single { RepositoryImpl(get(), get()) as Repository }
        }

        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(viewModelModule, dataModule, networkModule))
        }
    }
}

