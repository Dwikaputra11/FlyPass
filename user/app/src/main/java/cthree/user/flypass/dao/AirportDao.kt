package cthree.user.flypass.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cthree.user.flypass.models.airport.Airport

@Dao
interface AirportDao {

    @Insert
    fun insertAirport(airport: List<Airport>)

    @Query("SELECT * FROM airports")
    fun getAllAirport() : LiveData<List<Airport>>

    @Query("SELECT * FROM airports WHERE city LIKE :query OR name LIKE :query OR country LIKE :query LIMIT 4")
    fun searchAirport(query: String): LiveData<List<Airport>>

    @Query("DELETE FROM airports")
    fun deleteAllAirport()

    @Delete
    fun deleteSingleAirport(airport: Airport)

    @Update
    fun updateAirport(airport: Airport)

}