package cthree.admin.flypass.repositories

import androidx.lifecycle.LiveData
import cthree.admin.flypass.dao.AirplaneDao
import cthree.admin.flypass.db.MyDatabase
import cthree.admin.flypass.models.airplane.Airplane
import javax.inject.Inject

class AirplaneRepository @Inject constructor(private val airplaneDao : AirplaneDao) {

    fun getAllAirplane(): LiveData<List<Airplane>> = airplaneDao.getAllAirplane()

    fun existingSearchAirplane(query: String): LiveData<List<Airplane>> = airplaneDao.existingSearchAirplane(query)

    fun searchAirplane(query: String): LiveData<List<Airplane>> = airplaneDao.searchAirplane(query)

    fun insertAirplane(airplane: List<Airplane>){
        MyDatabase.databaseWriteExecutor.execute {
            airplaneDao.insertAirplane(airplane)
        }
    }

    fun deleteAllAirplane(){
        MyDatabase.databaseWriteExecutor.execute {
            airplaneDao.deleteAllAirplane()
        }
    }

    fun deleteSingleAirplane(airplane: Airplane){
        MyDatabase.databaseWriteExecutor.execute {
            airplaneDao.deleteSingleAirplane(airplane)
        }
    }

    fun updateAirplane(airplane: Airplane){
        MyDatabase.databaseWriteExecutor.execute {
            airplaneDao.updateAirplane(airplane)
        }
    }
}