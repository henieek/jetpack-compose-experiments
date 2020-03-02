package com.github.partition.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

  private val viewModel: ComposeViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        val state = flowState(
          initialState = viewModel::initialState,
          flow = viewModel.state(),
          scope = lifecycleScope
        )
        ListScreen(viewModel, state)
      }
    }
  }
}