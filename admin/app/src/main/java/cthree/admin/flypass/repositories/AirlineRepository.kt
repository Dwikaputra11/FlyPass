package cthree.admin.flypass.repositories

import androidx.lifecycle.LiveData
import cthree.admin.flypass.dao.AirlineDao
import cthree.admin.flypass.db.MyDatabase
import cthree.admin.flypass.models.airline.Airline
import javax.inject.Inject

class AirlineRepository @Inject constructor(private val airlineDao: AirlineDao) {

    fun getAllAirline(): LiveData<List<Airline>> = airlineDao.getAllAirline()

    fun existingSearchAirline(query: String): LiveData<List<Airline>> = airlineDao.existingSearchAirline(query)

    fun searchAirline(query: String): LiveData<List<Airline>> = airlineDao.searchAirline(query)

    fun insertAirline(airline: List<Airline>){
        MyDatabase.databaseWriteExecutor.execute {
            airlineDao.insertAirline(airline)
        }
    }

    fun deleteAllAirline(){
        MyDatabase.databaseWriteExecutor.execute {
            airlineDao.deleteAllAirline()
        }
    }

    fun deleteSingleAirline(airline: Airline){
        MyDatabase.databaseWriteExecutor.execute {
            airlineDao.deleteSingleAirline(airline)
        }
    }

    fun updateAirline(airline: Airline){
        MyDatabase.databaseWriteExecutor.execute {
            airlineDao.updateAirline(airline)
        }
    }

}