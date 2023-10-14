package more.tech.app.feature_main.data.local.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import more.tech.app.feature_main.data.local.daos.ATMDao
import more.tech.app.feature_main.data.local.daos.OfficeDao
import more.tech.app.feature_main.data.local.entities.ATMEntity
import more.tech.app.feature_main.data.local.entities.OfficeEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


@Database(
    entities = [ATMEntity::class, OfficeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun atmDao(): ATMDao
    abstract fun officeDao(): OfficeDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            val factory =
                SupportFactory(SQLiteDatabase.getBytes("8c6cc4e2091a812a8274a4133fd5b8249".toCharArray()))
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "app_database"
                )
                    .openHelperFactory(factory)
                    .build()
                instance = db
                db
            }
        }
    }
}