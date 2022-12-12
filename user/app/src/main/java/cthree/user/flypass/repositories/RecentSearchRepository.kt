package cthree.user.flypass.repositories

import androidx.lifecycle.LiveData
import cthree.user.flypass.dao.RecentSearchDao
import cthree.user.flypass.data.RecentSearch
import cthree.user.flypass.db.MyDatabase
import javax.inject.Inject

class RecentSearchRepository @Inject constructor(private val recentSearchDao: RecentSearchDao) {

    fun getAllSearch(): LiveData<List<RecentSearch>> = recentSearchDao.getAllSearch()

    fun getSearch(id: Int): LiveData<RecentSearch> = recentSearchDao.getSearch(id)

    fun insertSearch(search: RecentSearch){
        MyDatabase.databaseWriteExecutor.execute {
            recentSearchDao.insertSearch(search)
        }
    }

    fun deleteSearch(search: RecentSearch){
        MyDatabase.databaseWriteExecutor.execute {
            recentSearchDao.deleteSearch(search)
        }
    }

    fun deleteAllSearch(){
        MyDatabase.databaseWriteExecutor.execute {
            recentSearchDao.deleteAllSearch()
        }
    }
}