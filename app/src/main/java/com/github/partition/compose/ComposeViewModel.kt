package com.github.partition.compose

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

data class Repository(val name: String)

sealed class ListState {
  object Empty : ListState()
  object Loading : ListState()
  object Error : ListState()
  data class Repositories(val repos: List<Repository>) : ListState()
}

data class ViewState(val searchPhrase: String, val listState: ListState) {
  companion object {
    fun empty() = ViewState(
      searchPhrase = "",
      listState = ListState.Empty
    )
  }
}

class ComposeViewModel(private val api: GithubApi) : ViewModel() {

  private val channel = ConflatedBroadcastChannel(ViewState.empty())
  private var job: Job? = null

  fun initialState(): ViewState = channel.value

  fun state(): Flow<ViewState> = channel.asFlow()

  private fun sendNewValue(evalNewState: (ViewState) -> ViewState) {
    val oldState = channel.value
    channel.sendBlocking(evalNewState(oldState))
  }

  fun onSearchClicked() {
    sendNewValue {
      it.copy(listState = ListState.Loading)
    }
    job?.cancel()
    job = viewModelScope.launch {
      try {
        val response = withContext(Dispatchers.IO) {
          api.search(channel.value.searchPhrase)
        }
        sendNewValue { oldState ->
          oldState.copy(listState = ListState.Repositories(response.items.map {
            Repository(it.fullName)
          }))
        }
      } catch (e: Exception) {
        Log.e("ApiError", "Error occurred when retrieving repositories", e)
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