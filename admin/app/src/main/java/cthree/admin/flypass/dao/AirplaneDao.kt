package cthree.admin.flypass.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cthree.admin.flypass.models.airplane.Airplane

@Dao
interface AirplaneDao {

    @Insert
    fun insertAirplane(airplane: List<Airplane>)

    @Query("SELECT * FROM airplanes")
    fun getAllAirplane() : LiveData<List<Airplane>>

    @Query("SELECT * FROM airplanes WHERE icao LIKE :query OR model LIKE :query LIMIT 4")
    fun existingSearchAirplane(query: String): LiveData<List<Airplane>>

    @Query("SELECT * FROM airplanes WHERE model LIKE '%' || :query || '%' OR icao LIKE '%' || :query || '%'")
    fun searchAirplane(query: String): LiveData<List<Airplane>>

    @Query("DELETE FROM airplanes")
    fun deleteAllAirplane()

    @Delete
    fun deleteSingleAirplane(airplane: Airplane)

    @Update
    fun updateAirplane(airplane: Airplane)

}