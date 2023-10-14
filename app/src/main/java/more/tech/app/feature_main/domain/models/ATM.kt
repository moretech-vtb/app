package more.tech.app.feature_main.domain.models


data class ATM(
    val id: String,
    val address: String,
    val allDay: Boolean,
    val latitude: Double,
    val longitude: Double,
    val wheelchairService: Boolean,
    val blindService: Boolean,
    val nfcService: Boolean,
    val qrService: Boolean,
    val supportsUsd: Boolean,
    val supportsChargeRub: Boolean,
    val supportsEur: Boolean,
    val supportsRub: Boolean,
)