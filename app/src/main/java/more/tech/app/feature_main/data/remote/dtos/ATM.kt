package more.tech.app.feature_main.data.remote.dtos

import com.google.gson.annotations.SerializedName
import more.tech.app.feature_main.data.local.entities.ATMEntity
import more.tech.app.feature_main.domain.models.ATM

data class ATMsResponse(
    @SerializedName("atms")
    val atms: List<ATMDto>
)

data class ATMResponse(
    @SerializedName("atm")
    val atm: ATMDto
)


data class ATMDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("all_day")
    val allDay: Boolean,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("wheelchair_service")
    val wheelchairService: Boolean,
    @SerializedName("blind_service")
    val blindService: Boolean,
    @SerializedName("nfc_for_bank_cards_service")
    val nfcService: Boolean,
    @SerializedName("qr_read_service")
    val qrService: Boolean,
    @SerializedName("supports_usd_service")
    val supportsUsd: Boolean,
    @SerializedName("supports_charge_rub_service")
    val supportsChargeRub: Boolean,
    @SerializedName("supports_eur_service")
    val supportsEur: Boolean,
    @SerializedName("supports_rub_service")
    val supportsRub: Boolean,
) {
    fun toATM(): ATM {
        return ATM(
            id = id,
            address = address,
            allDay = allDay,
            latitude = latitude,
            longitude = longitude,
            wheelchairService = wheelchairService,
            blindService = blindService,
            nfcService = nfcService,
            qrService = qrService,
            supportsUsd = supportsUsd,
            supportsChargeRub = supportsChargeRub,
            supportsEur = supportsEur,
            supportsRub = supportsRub,
        )
    }

    fun toATMEntity(): ATMEntity {
        return ATMEntity(
            id = id,
            address = address,
            allDay = allDay,
            latitude = latitude,
            longitude = longitude,
            wheelchairService = wheelchairService,
            blindService = blindService,
            nfcService = nfcService,
            qrService = qrService,
            supportsUsd = supportsUsd,
            supportsChargeRub = supportsChargeRub,
            supportsEur = supportsEur,
            supportsRub = supportsRub,
        )
    }
}