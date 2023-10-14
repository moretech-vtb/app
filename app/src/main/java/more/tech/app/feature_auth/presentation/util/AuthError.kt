package more.tech.app.feature_auth.presentation.util

import more.tech.app.core.util.CustomError

sealed class AuthError : CustomError() {
    object FieldEmpty : AuthError()
}
