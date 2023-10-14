package more.tech.app.core.domain.states

import more.tech.app.core.util.CustomError

data class StandardTextFieldState(
    val text: String = "",
    var error: CustomError? = null
)
