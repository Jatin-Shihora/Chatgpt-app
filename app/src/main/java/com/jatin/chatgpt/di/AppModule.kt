package com.jatin.chatgpt.di

import com.jatin.chatgpt.net.MyHttpClient
import com.jatin.chatgpt.repository.GptRepository
import com.jatin.chatgpt.repository.local.AppDatabase
import com.jatin.chatgpt.repository.remote.GptApi
import com.jatin.chatgpt.viewmodel.MainPageViewModel
import com.jatin.template.common.Constants
import com.jatin.template.common.net.NetworkHandler
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Injection
 *
 * @author Jatin
 * @time 30/04/2023
 */
val appModule = module {
    single {
        MyHttpClient()
    }
    single {
        Retrofit.Builder().baseUrl(Constants.BASE_API_URL)
            .client((get() as MyHttpClient).getClient())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    single { NetworkHandler(get()) }

}
val dataModule = module {
    single { (get() as Retrofit).create(GptApi::class.java) }
    single { AppDatabase.getDBInstance(get()).getMessageDao() }
    single { AppDatabase.getDBInstance(get()).getSessionDao() }
    single { AppDatabase.getDBInstance(get()).getTemplateDao() }
    single { GptRepository(get(), get(), get(), get(), get()) }
}
val viewModelModule = module {
    single { MainPageViewModel(get()) }
}

val allModules = appModule + dataModule + viewModelModule