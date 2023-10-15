package more.tech.app.feature_main.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import more.tech.app.feature_main.data.local.converters.ListTypeConverter
import more.tech.app.feature_main.data.local.converters.OpenHourTypeConverter
import more.tech.app.feature_main.domain.models.Office
import more.tech.app.feature_main.domain.models.OpenHour

@Entity(tableName = "offices")
@TypeConverters(OpenHourTypeConverter::class, ListTypeConverter::class)
data class OfficeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rko: Boolean,
    val suoAvailability: Boolean,
    val myBranch: Boolean,
    val kep: Boolean,
    val hasRamp: Boolean,
    val officeType: String,
    val salePointFormat: String,
    val status: String,
    val metroStation: String? = null,
    val openHoursIndividual: List<OpenHour>,
    val openHours: List<OpenHour>,
    val loadsIndividual: List<String>? = null,
    val loads: List<String>? = null,
) {
    fun toOffice(): Office {
        return Office(
            id = id,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            rko = rko,
            suoAvailability = suoAvailability,
            myBranch = myBranch,
            kep = kep,
            hasRamp = hasRamp,
            officeType = officeType,
            salePointFormat = salePointFormat,
            status = status,
            metroStation = metroStation,
            openHoursIndividual = openHoursIndividual,
            openHours = openHours,
            loadsIndividual = loadsIndividual ?: listOf(),
            loads = loads ?: listOf(),
        )
    }
}


