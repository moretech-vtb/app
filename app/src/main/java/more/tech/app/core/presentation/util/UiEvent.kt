package more.tech.app.core.presentation.util

import android.os.Bundle
import androidx.annotation.IdRes
import okhttp3.ResponseBody
import more.tech.app.core.util.UiText

abstract class Event

sealed class UiEvent : Event() {
    object Initial : UiEvent()
    data class ShowToast(val uiText: UiText) : UiEvent()
    data class ComplexError(val bodyError: ResponseBody) : UiEvent()
    data class NavigationResource(@IdRes val id: Int, val bundle: Bundle? = null): UiEvent()
}
