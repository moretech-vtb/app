package more.tech.app.feature_auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import more.tech.app.R
import more.tech.app.core.data.SharedPrefsManager
import more.tech.app.core.presentation.util.UiEvent
import more.tech.app.core.presentation.util.ViewState
import more.tech.app.core.util.NetworkUtils
import more.tech.app.core.util.Resource
import more.tech.app.feature_auth.domain.use_case.AuthenticateUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authenticateUseCase: AuthenticateUseCase,
    private val preferences: SharedPrefsManager,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = _viewState

    private val _eventFlow = MutableStateFlow<UiEvent>(UiEvent.Initial)
    val eventFlow: StateFlow<UiEvent> = _eventFlow

    init {
        viewModelScope.launch {
            val token = preferences.getToken()
            if (!token.isNullOrBlank()) {
                if (networkUtils.isNetworkAvailable()) {
                    _viewState.value = viewState.value.copy(isLoading = true)
                    when (authenticateUseCase()) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.NavigationResource(R.id.mainFragment))
                            _viewState.value = ViewState(isLoading = false)
                        }

                        else -> {
                            _eventFlow.emit(UiEvent.NavigationResource(R.id.loginFragment))
                            _viewState.value = ViewState(isLoading = false)
                        }
                    }
                } else {
                    _eventFlow.emit(UiEvent.NavigationResource(R.id.mainFragment))
                    _viewState.value = viewState.value.copy(isLoading = false)
                }

            } else {
                _eventFlow.emit(UiEvent.NavigationResource(R.id.loginFragment))
            }
        }
    }
}