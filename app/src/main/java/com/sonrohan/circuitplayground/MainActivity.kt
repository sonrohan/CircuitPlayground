package com.sonrohan.circuitplayground

import android.accounts.Account
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.sonrohan.circuitplayground.ui.theme.CircuitPlaygroundTheme
import kotlinx.parcelize.Parcelize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val backStack = rememberSaveableBackStack(root = AccountPage)
            val navigator = rememberCircuitNavigator(backStack)

//            NavigableCircuitContent(navigator, backStack)

            val circuit: Circuit =
                Circuit.Builder()
                    .addPresenter<AccountPage, AccountPage.State>(AccountPresenter(navigator = navigator))
                    .addUi<AccountPage, AccountPage.State> { state, modifier -> Account(state, modifier) }
                    .addPresenter<SomeOtherPage, SomeOtherPage.State>(SomeOtherPresenter(navigator = navigator))
                    .addUi<SomeOtherPage, SomeOtherPage.State> { state, modifier -> SomeOther(state, modifier) }
                    .build()

            CircuitPlaygroundTheme {
                CircuitCompositionLocals(circuit) {
                    NavigableCircuitContent(navigator, backStack)
                }
            }
        }
    }
}

@Parcelize
private data object AccountPage : Screen {
    data class State(
        val firstName: String,
        val lastName: String,
        val email: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object GetData : Event()
        data object SomeOtherScreen : Event()
    }
}


@Composable
private fun Account(
    state: AccountPage.State,
    modifier: Modifier = Modifier,
) {
   Column(
       modifier = modifier,
   ) {
      Text(
          text = "Welcome ${state.firstName} ${state.lastName}",
      )
       Text(
           text = state.email,
       )

       Button(onClick = {
           state.eventSink(AccountPage.Event.GetData)
       }) {
           Text("Get Data!")
       }

       Button(onClick = {
           state.eventSink(AccountPage.Event.SomeOtherScreen)
       }) {
           Text("Some other screen")
       }

   }
}


private class AccountPresenter(
    private val navigator: Navigator,
) : Presenter<AccountPage.State> {
    @Composable
    override fun present(): AccountPage.State {
        var email by remember {
            mutableStateOf("")
        }

        var firstName by remember {
            mutableStateOf("")
        }

        var lastName by remember {
            mutableStateOf("")
        }

        return AccountPage.State(
            email = email,
            firstName = firstName,
            lastName = lastName,
            eventSink = { event ->
                when (event) {
                    AccountPage.Event.GetData -> {
                        email = "rohan@google.com"
                        firstName = "Rohan"
                        lastName = "Harrison"
                    }

                    AccountPage.Event.SomeOtherScreen -> {
                        navigator.goTo(
                            SomeOtherPage
                        )
                    }
                }
            },
        )
    }
}