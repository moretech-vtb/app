package more.tech.app.feature_main.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import more.tech.app.feature_main.domain.models.ATM

@Entity(tableName = "atms")
data class ATMEntity(
    @PrimaryKey val id: String,
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
}


