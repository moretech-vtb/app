package more.tech.app.feature_main.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import more.tech.app.feature_main.data.local.entities.ATMEntity

@Dao
interface ATMDao {

    @Query("SELECT * FROM atms")
    suspend fun getAllATMs(): List<ATMEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertATMs(atms: List<ATMEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertATM(atm: ATMEntity)
}