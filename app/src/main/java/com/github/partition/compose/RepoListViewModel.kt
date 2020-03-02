package com.github.partition.compose

import android.util.Log
import androidx.compose.State
import androidx.compose.state
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class RepoListViewModel(private val api: GithubApi) : ViewModel() {

  private val state = state { ListViewState.empty() }
  private var job: Job? = null
    set(value) {
      job?.cancel()
      field = value
    }

  fun state(): State<ListViewState> = state

  private fun sendNewValue(evalNewState: (ListViewState) -> ListViewState) {
    state.value = evalNewState(state.value)
  }

  fun onSearchClicked() {
    sendNewValue {
      it.copy(listState = ListState.Loading)
    }
    job = viewModelScope.launch {
      try {
        val searchPhrase = state.value.searchPhrase
        val response = withContext(Dispatchers.IO) {
          api.search(searchPhrase)
        }
        sendNewValue { oldState ->
          oldState.copy(listState = ListState.Repositories(response.items.map {
            Repository(it.fullName)
          }))
        }
      } catch (ignored: CancellationException) {
        // this is expected when user clicks the search button before the previous coroutine completes
      } catch (e: Throwable) {
        Log.d("ApiError", "Error occurred when retrieving repositories", e)
        sendNewValue { oldState ->
          oldState.copy(listState = ListState.Error)
        }
      }
    }
  }

  fun onSearchPhraseChange(phrase: String) {
    sendNewValue {
      it.copy(searchPhrase = phrase)
    }
  }
}

data class Repository(val name: String)

sealed class ListState {
  object Empty : ListState()
  object Loading : ListState()
  object Error : ListState()
  data class Repositories(val repos: List<Repository>) : ListState()
}

data class ListViewState(val searchPhrase: String, val listState: ListState) {
  companion object {
    fun empty() = ListViewState(
      searchPhrase = "",
      listState = ListState.Empty
    )
  }
}