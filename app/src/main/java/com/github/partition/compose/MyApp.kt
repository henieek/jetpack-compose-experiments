package com.github.partition.compose

import android.app.Application
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {

  private val module = module {
    viewModel { ComposeViewModel() }
  }

  override fun onCreate() {
    super.onCreate()
    startKoin {
      modules(module)
    }
  }
}
