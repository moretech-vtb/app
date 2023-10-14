package more.tech.app.core.presentation.util

import more.tech.app.core.util.UiText
import okhttp3.ResponseBody

data class ViewState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val bodyError: ResponseBody? = null
)