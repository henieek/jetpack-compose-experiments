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
import androidx.ui.material.CircularProgressIndicator
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
      searchBar(state.value)
      listView(state.value)
    }
  }

  @Composable
  private fun searchBar(state: ViewState) {
    TextField(
      value = state.searchPhrase,
      onValueChange = viewModel::onSearchPhraseChange
    )
    Button("Search", onClick = viewModel::onSearchClicked)
  }

  @Composable
  private fun listView(state: ViewState) {
    when (val listState = state.listState) {
      ListState.Loading -> CircularProgressIndicator()
      ListState.Error -> Text("Error occurred")
      ListState.Empty -> Text("Start typing")
      is ListState.Repositories -> listState.repos.map { Text(it.name) }
    }
  }
}