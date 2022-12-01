package cthree.user.flypass.repositories

import androidx.lifecycle.LiveData
import cthree.user.flypass.dao.AirportDao
import cthree.user.flypass.db.MyDatabase
import cthree.user.flypass.models.airport.Airport

class AirportRepository (private val airportDao: AirportDao) {

    fun getAllAirport(): LiveData<List<Airport>> = airportDao.getAllAirport()

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