package com.github.partition.compose

sealed class AppState {
  data class ListView(val listViewState: ListViewState) : AppState()
}

fun Router(appState: AppState, creator: (AppState) -> Unit) {
  when (appState) {
    is AppState.ListView -> creator(appState)
  }
}