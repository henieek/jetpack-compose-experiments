package com.github.partition.compose

import androidx.compose.Composable
import androidx.compose.State
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.layout.Column
import androidx.ui.material.Button
import androidx.ui.material.CircularProgressIndicator

@Composable
fun ListScreen(viewModel: ComposeViewModel, state: State<ListViewState>) {
  @Composable
  fun searchBar() {
    TextField(
      value = state.value.searchPhrase,
      onValueChange = viewModel::onSearchPhraseChange
    )
    Button("Search", onClick = viewModel::onSearchClicked)
  }

  @Composable
  fun listView() {
    when (val listState = state.value.listState) {
      ListState.Loading -> CircularProgressIndicator()
      ListState.Error -> Text("Error occurred")
      ListState.Empty -> Text("Start typing")
      is ListState.Repositories -> listState.repos.map { Text(it.name) }
    }
  }

  Column {
    searchBar()
    listView()
  }
}