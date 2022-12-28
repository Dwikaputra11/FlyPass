package cthree.admin.flypass.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cthree.admin.flypass.models.airline.Airline

@Dao
interface AirlineDao {

    @Insert
    fun insertAirline(airline: List<Airline>)

    @Query("SELECT * FROM airlines")
    fun getAllAirline() : LiveData<List<Airline>>

    @Query("SELECT * FROM airlines WHERE iata LIKE :query OR image LIKE :query OR name LIKE :query LIMIT 4")
    fun existingSearchAirline(query: String): LiveData<List<Airline>>

    @Query("SELECT * FROM airlines WHERE iata LIKE '%' || :query || '%' OR image LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchAirline(query: String): LiveData<List<Airline>>

    @Query("DELETE FROM airlines")
    fun deleteAllAirline()

    @Delete
    fun deleteSingleAirline(airlines: Airline)

    @Update
    fun updateAirline(airlines: Airline)

}