package cthree.user.flypass.repositories

import androidx.lifecycle.LiveData
import cthree.user.flypass.dao.AirportDao
import cthree.user.flypass.db.MyDatabase
import cthree.user.flypass.models.airport.Airport
import javax.inject.Inject

class AirportRepository @Inject constructor(private val airportDao: AirportDao) {

    fun getAllAirport(): LiveData<List<Airport>> = airportDao.getAllAirport()

    fun existingSearchAirport(query: String): LiveData<List<Airport>> = airportDao.existingSearchAirport(query)

    fun searchAirport(query: String): LiveData<List<Airport>> = airportDao.searchAirport(query)

    fun insertAirport(airport: List<Airport>){
        MyDatabase.databaseWriteExecutor.execute {
            airportDao.insertAirport(airport)
        }
    }

    fun deleteAllAirport(){
        MyDatabase.databaseWriteExecutor.execute {
            airportDao.deleteAllAirport()
        }
    }

    fun deleteSingleAirport(airport: Airport){
        MyDatabase.databaseWriteExecutor.execute {
            airportDao.deleteSingleAirport(airport)
        }
    }

    fun updateAirport(airport: Airport){
        MyDatabase.databaseWriteExecutor.execute {
            airportDao.updateAirport(airport)
        }
    }
}