package cthree.admin.flypass.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import cthree.admin.flypass.UserProto
import cthree.admin.flypass.models.admin.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

private const val USER_PREFERENCES_NAME = "user_preferences"
private const val DATA_STORE_FILE_NAME = "user_prefs.pb"
private const val SORT_ORDER_KEY = "sort_order"

class UserPreferenceRepository(private val context: Context) {
    companion object{
        // create data store proto
        private val Context.userPreferencesStore: DataStore<UserProto> by dataStore(
            fileName = DATA_STORE_FILE_NAME,
            serializer = UserPreferencesSerializer
        )
    }

    // save data to data store proto
    suspend fun saveDataAdmin(user: User){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setId(user.id).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setEmail(user.email).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAccesstToken(user.accesstToken).build()
        }
    }

//    suspend fun saveDataUserId(id: Int){
//        context.userPreferencesStore.updateData { preferences ->
//            preferences.toBuilder().setId(id).build()
//        }
//    }

    suspend fun saveLoginStatus(paramIsLogin: Boolean){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setSaveLoginStatus(paramIsLogin).build()
        }
    }


    // delete datastore
    suspend fun clearData(){

    }


    // read data store proto
    private val TAG: String = "UserPreferencesRepo"

    val readData: Flow<UserProto> = context.userPreferencesStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(UserProto.getDefaultInstance())
            } else {
                throw exception
            }
        }
}