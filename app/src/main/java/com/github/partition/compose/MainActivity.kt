package com.github.partition.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.core.setContent
import androidx.ui.layout.Column
import androidx.ui.material.MaterialTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
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
        val state = state { viewModel.initialState() }
        lifecycleScope.launch {
            viewModel.state().collect { state.value = it }
        }
        Column {
            TextField(state.value.name, onValueChange = { viewModel.onNameChanged(it) })
            Text("Current: ${state.value.result}")
        }
    }
}

data class Model(val result: String, val name: String) {
    companion object {
        fun empty() = Model("", "")
    }
}

class ComposeViewModel : ViewModel() {

    private val channel = ConflatedBroadcastChannel(Model.empty())

    fun initialState(): Model = channel.value

    fun state(): Flow<Model> = channel.asFlow()

    fun onNameChanged(name: String) {
        channel.sendBlocking(Model(
                name = name,
                result = name.let {
                    if (name == CORRECT_PASSWORD) "Correct!" else "Nope :("
                }
        ))
    }

    private companion object {
        const val CORRECT_PASSWORD = "Password1"
    }
}