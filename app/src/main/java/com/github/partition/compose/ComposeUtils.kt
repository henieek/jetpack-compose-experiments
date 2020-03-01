package com.github.partition.compose

import androidx.compose.State
import androidx.compose.state
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> flowState(
  initialState: () -> T,
  flow: Flow<T>,
  scope: CoroutineScope
): State<T> = state { initialState() }.also { state ->
  scope.launch {
    flow.collect { state.value = it }
  }
}
