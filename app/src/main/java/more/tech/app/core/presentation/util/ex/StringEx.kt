package more.tech.app.core.presentation.util.ex

import com.google.gson.JsonParser
import java.text.SimpleDateFormat
import java.util.Locale

fun String.convertDate(): String {
    val date = SimpleDateFormat("dd.mm.yyyy", Locale.US).parse(this)
    return SimpleDateFormat("yyyy-mm-dd", Locale.US).format(date!!)
}

fun String.getErrorMessage(): String {
    val jsonObject = JsonParser.parseString(this).asJsonObject
    return jsonObject.get("message").asString
}