package more.tech.app.feature_auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import more.tech.app.R
import more.tech.app.core.data.SharedPrefsManager
import more.tech.app.core.domain.states.StandardTextFieldState
import more.tech.app.core.presentation.util.UiEvent
import more.tech.app.core.presentation.util.ViewState
import more.tech.app.core.util.Resource
import more.tech.app.core.util.UiText
import more.tech.app.feature_auth.domain.use_case.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val registerUseCase: LoginUseCase,
    private val preferences: SharedPrefsManager
) : ViewModel() {

    val login = preferences.getLogin()

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = _viewState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _loginState = MutableStateFlow(StandardTextFieldState())
    val loginState: StateFlow<StandardTextFieldState> = _loginState

    private val _passwordState = MutableStateFlow(StandardTextFieldState())
    val passwordState: StateFlow<StandardTextFieldState> = _passwordState

    init {
        _loginState.value = _loginState.value.copy(text = login!!)
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EnteredLogin -> {
                _loginState.value = _loginState.value.copy(
                    text = event.value
                )
            }

            is LoginEvent.EnteredPassword -> {
                _passwordState.value = _passwordState.value.copy(
                    text = event.value
                )
            }

            is LoginEvent.Login -> {
                login()
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _viewState.value = viewState.value.copy(isLoading = true)
            val loginResult = registerUseCase(
                login = _loginState.value.text,
                password = _passwordState.value.text
            )

            if (loginResult.loginError != null) {
                _viewState.value = viewState.value.copy(isLoading = false)
                _loginState.value = loginState.value.copy(
                    error = loginResult.loginError
                )
            }

            if (loginResult.passwordError != null) {
                _viewState.value = viewState.value.copy(isLoading = false)
                _passwordState.value = passwordState.value.copy(
                    error = loginResult.passwordError
                )
            }

            if (loginResult.hasError) {
                _viewState.value = viewState.value.copy(
                    isLoading = false,
                    error = UiText.StringResource(R.string.fill_fields)
                )
            }

            when (loginResult.result) {
                is Resource.Error -> {
                    if (loginResult.result.data != null) {
//                        _eventFlow.emit(
//                            UiEvent.ComplexError(loginResult.result.m)
//                        )
                        _viewState.value = ViewState(isLoading = false)
                    } else {
                        _eventFlow.emit(
                            UiEvent.ShowToast(loginResult.result.message ?: UiText.unknownError())
                        )
                        _viewState.value = ViewState(isLoading = false)
                    }
                }

                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.NavigationResource(R.id.mainFragment))
                    _viewState.value = ViewState(isLoading = false)
                }

                else -> _viewState.value = ViewState(isLoading = false)
            }

        }
    }
}