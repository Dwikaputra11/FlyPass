package cthree.user.flypass.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cthree.user.flypass.data.RecentSearch

@Dao
interface RecentSearchDao {

    @Query("SELECT * FROM recent_search ORDER BY id DESC")
    fun getAllSearch(): LiveData<List<RecentSearch>>

    @Query("SELECT * FROM recent_search WHERE id == :id")
    fun getSearch(id: Int): LiveData<RecentSearch>

    @Query("DELETE FROM recent_search")
    fun deleteAllSearch()

    @Insert
    fun insertSearch(search: RecentSearch)

    @Delete
    fun deleteSearch(search: RecentSearch)
}