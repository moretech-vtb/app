package more.tech.app.feature_main.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import more.tech.app.feature_main.data.local.entities.OfficeEntity

@Dao
interface OfficeDao {

    @Query("SELECT * FROM offices")
    suspend fun getAllOffices(): List<OfficeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffices(offices: List<OfficeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffice(office: OfficeEntity)
}