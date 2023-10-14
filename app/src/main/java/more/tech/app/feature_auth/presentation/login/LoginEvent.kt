package more.tech.app.feature_auth.presentation.login


sealed class LoginEvent {
    data class EnteredLogin(val value: String) : LoginEvent()
    data class EnteredPassword(val value: String) : LoginEvent()
    object Login : LoginEvent()
}