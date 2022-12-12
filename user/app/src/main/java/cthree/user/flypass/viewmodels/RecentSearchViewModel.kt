package cthree.user.flypass.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cthree.user.flypass.data.RecentSearch
import cthree.user.flypass.repositories.RecentSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentSearchViewModel @Inject constructor(private val recentSearchRepository: RecentSearchRepository): ViewModel() {

    fun getAllSearch(): LiveData<List<RecentSearch>> = recentSearchRepository.getAllSearch()

    fun getSearch(id: Int): LiveData<RecentSearch> = recentSearchRepository.getSearch(id)

    fun insertSearch(search: RecentSearch){
        viewModelScope.launch {
            recentSearchRepository.insertSearch(search)
        }
    }

    fun deleteSearch(search: RecentSearch){
        viewModelScope.launch {
            recentSearchRepository.deleteSearch(search)
        }
    }

    fun deleteAllSearch(){
        viewModelScope.launch {
            recentSearchRepository.deleteAllSearch()
        }
    }

}