package com.github.partition.compose

import android.app.Application
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApp : Application() {

  private val module = module {
    single { Retrofit.Builder()
      .baseUrl("https://api.github.com")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    }
    single {
      val retrofit: Retrofit = get()
      retrofit.create(GithubApi::class.java)
    }
    viewModel { ComposeViewModel(get()) }
  }

  override fun onCreate() {
    super.onCreate()
    startKoin {
      modules(module)
    }
  }
}
