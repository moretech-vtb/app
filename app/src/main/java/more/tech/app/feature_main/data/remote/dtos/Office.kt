package more.tech.app.feature_main.data.remote.dtos

import com.google.gson.annotations.SerializedName
import more.tech.app.feature_main.data.local.entities.ATMEntity
import more.tech.app.feature_main.data.local.entities.OfficeEntity
import more.tech.app.feature_main.domain.models.ATM
import more.tech.app.feature_main.domain.models.Office
import more.tech.app.feature_main.domain.models.OpenHour

data class OfficesResponse(
    @SerializedName("offices")
    val offices: List<OfficeDTO>
)

data class OfficeResponse(
    @SerializedName("office")
    val office: OfficeDTO
)


data class OfficeDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("rko")
    val rko: Boolean,
    @SerializedName("suo_availability")
    val suoAvailability: Boolean,
    @SerializedName("my_branch")
    val myBranch: Boolean,
    @SerializedName("kep")
    val kep: Boolean,
    @SerializedName("has_ramp")
    val hasRamp: Boolean,
    @SerializedName("office_type")
    val officeType: String,
    @SerializedName("sale_point_format")
    val salePointFormat: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("metro_station")
    val metroStation: String? = null,
    @SerializedName("openHoursIndividual")
    val openHoursIndividual: List<OpenHourDTO>,
    @SerializedName("open_hours")
    val openHours: List<OpenHourDTO>,
    @SerializedName("loads_individual")
    val loadsIndividual: List<String>? = null,
    @SerializedName("loads")
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
            openHoursIndividual = if (openHoursIndividual.isEmpty()) {
                emptyList()
            } else {
                openHoursIndividual.map { it.toOpenHour() }
            },
            openHours = if (openHours.isEmpty()) {
                emptyList()
            } else {
                openHours.map { it.toOpenHour() }
            },
            loadsIndividual = loadsIndividual ?: listOf(),
            loads = loads ?: listOf(),
        )
    }

    fun toOfficeEntity(): OfficeEntity {
        return OfficeEntity(
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
            openHoursIndividual = if (openHoursIndividual.isEmpty()) {
                emptyList()
            } else {
                openHoursIndividual.map { it.toOpenHour() }
            },
            openHours = if (openHours.isEmpty()) {
                emptyList()
            } else {
                openHours.map { it.toOpenHour() }
            },
            loadsIndividual = loadsIndividual ?: listOf(),
            loads = loads ?: listOf(),
        )
    }
}

data class OpenHourDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("day_of_week")
    val dayOfWeek: String,
    @SerializedName("hours")
    val hours: String,
) {
    fun toOpenHour(): OpenHour {
        return OpenHour(
            id = id, dayOfWeek = dayOfWeek, hours = hours,
        )
    }
}