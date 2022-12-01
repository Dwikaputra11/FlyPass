package cthree.user.flypass.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cthree.user.flypass.models.airport.Airport

@Dao
interface AirportDao {

    @Insert
    fun insertAirport(airport: List<Airport>)

    @Query("SELECT * FROM Airport")
    fun getAllAirport() : LiveData<List<Airport>>

    @Query("DELETE FROM Airport")
    fun deleteAllAirport()

    @Delete
    fun deleteSingleAirport(airport: Airport)

    @Update
    fun updateAirport(airport: Airport)

}