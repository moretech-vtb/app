package more.tech.app.core.presentation.util

import android.content.Context
import more.tech.app.core.util.UiText

fun UiText.asString(context: Context): String {
    return when(this) {
        is UiText.DynamicString -> this.value
        is UiText.StringResource -> context.getString(this.id)
    }
}