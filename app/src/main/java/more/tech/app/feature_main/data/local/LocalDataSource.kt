package more.tech.app.feature_main.data.local

import more.tech.app.feature_main.data.local.daos.ATMDao
import more.tech.app.feature_main.data.local.daos.OfficeDao
import more.tech.app.feature_main.data.local.entities.ATMEntity
import more.tech.app.feature_main.data.local.entities.OfficeEntity

class LocalDataSource(
    private val atmDao: ATMDao,
    private val officeDao: OfficeDao
) {

    suspend fun fetchATMs(): List<ATMEntity> {
        return atmDao.getAllATMs()
    }

    suspend fun insertATMs(atms: List<ATMEntity>) {
        atmDao.insertATMs(atms)
    }

    suspend fun insertATM(atm: ATMEntity) {
        atmDao.insertATM(atm)
    }

    suspend fun fetchOffices(): List<OfficeEntity> {
        return officeDao.getAllOffices()
    }

    suspend fun insertOffices(offices: List<OfficeEntity>) {
        officeDao.insertOffices(offices)
    }

    suspend fun insertOffice(office: OfficeEntity) {
        officeDao.insertOffice(office)
    }

}