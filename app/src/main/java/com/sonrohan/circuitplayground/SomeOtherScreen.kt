package com.sonrohan.circuitplayground

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data object SomeOtherPage : Screen {
    data class State(
        val numberOfPoints: Int,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object Back : Event()
    }
}

@Composable
fun SomeOther(
    state: SomeOtherPage.State,
    modifier: Modifier = Modifier,
) {
    Column (
        modifier = modifier,
    ) {
        Text(
            text = "Points: ${state.numberOfPoints}"
        )

        Button(onClick = {
            state.eventSink(SomeOtherPage.Event.Back)
        }) {
            Text("Some other screen")
        }
    }
}

class SomeOtherPresenter(
    private val navigator: Navigator,
) : Presenter<SomeOtherPage.State> {
    @Composable
    override fun present(): SomeOtherPage.State {
        val points by remember {
            mutableIntStateOf(12334)
        }
        return SomeOtherPage.State(
            numberOfPoints = points,
            eventSink = { event ->
                when (event) {
                    SomeOtherPage.Event.Back -> {
                        navigator.pop()
                    }
                }
            },
        )
    }
}