package com.github.partition.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.core.setContent
import androidx.ui.layout.Column
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

  private val viewModel: ComposeViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        view()
      }
    }
  }

  @Composable
  private fun view() {
    val state = flowState(
      initialState = viewModel::initialState,
      flow = viewModel.state(),
      scope = lifecycleScope
    )
    Column {
      TextField(
        value = state.value.searchPhrase,
        onValueChange = viewModel::onSearchPhraseChange
      )
      Button("Search", onClick = viewModel::onSearchClicked)
      when (val listState = state.value.listState) {
        ListState.Error -> errorView()
        ListState.Loading -> loadingView()
        ListState.Empty -> emptyView()
        is ListState.Repositories -> listView(listState.repos)
      }
    }
  }

  @Composable
  private fun listView(list: List<Repository>) {
    list.forEach {
      Text(it.name)
    }
  }

  @Composable
  private fun emptyView() {
    Text("Start typing")
  }

  @Composable
  private fun loadingView() {
    Text("Loading...")
  }

  @Composable
  private fun errorView() {
    Text("Error occurred")
  }
}