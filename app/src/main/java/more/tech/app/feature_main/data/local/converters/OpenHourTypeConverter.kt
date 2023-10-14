package more.tech.app.feature_main.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import more.tech.app.feature_main.domain.models.OpenHour

class OpenHourTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromOpenHoursIndividualList(value: List<OpenHour>): String {
        val type = object : TypeToken<List<OpenHour>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toOpenHoursIndividualList(value: String): List<OpenHour> {
        val type = object : TypeToken<List<OpenHour>>() {}.type
        return gson.fromJson(value, type)
    }
}