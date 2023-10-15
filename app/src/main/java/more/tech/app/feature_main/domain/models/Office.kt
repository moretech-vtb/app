package more.tech.app.feature_main.domain.models


data class Office(
    val id: String,
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
)

data class OpenHour(
    val id: String,
    val dayOfWeek: String,
    val hours: String,
)
