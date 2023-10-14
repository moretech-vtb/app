package more.tech.app.core.presentation.util.ex

import java.text.DecimalFormat
import kotlin.math.roundToInt

fun Double.toDistance(): String {
    return if (this < 1000) {
        "${(this).roundToInt()} м от Вас"
    } else {
        "${DecimalFormat("#.##").format(this / 1000.0)} км от Вас"
    }
}

fun Double.toTime(): String {
    return "${(this / 60.0).roundToInt()} мин"
}