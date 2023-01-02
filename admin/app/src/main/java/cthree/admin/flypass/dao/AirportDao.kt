package cthree.admin.flypass.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cthree.admin.flypass.models.airport.Airport

@Dao
interface AirportDao {

    @Insert
    fun insertAirport(airport: List<Airport>)

    @Query("SELECT * FROM airports")
    fun getAllAirport() : LiveData<List<Airport>>

    @Query("SELECT * FROM airports WHERE iata LIKE :query OR city LIKE :query OR name LIKE :query OR country LIKE :query LIMIT 4")
    fun existingSearchAirport(query: String): LiveData<List<Airport>>

    @Query("SELECT * FROM airports WHERE iata LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' OR country LIKE '%' || :query || '%'")
    fun searchAirport(query: String): LiveData<List<Airport>>

    @Query("DELETE FROM airports")
    fun deleteAllAirport()

    @Delete
    fun deleteSingleAirport(airport: Airport)

    @Update
    fun updateAirport(airport: Airport)

}