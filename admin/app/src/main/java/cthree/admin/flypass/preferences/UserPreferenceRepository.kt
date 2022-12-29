package cthree.admin.flypass.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import cthree.admin.flypass.UserProto
import cthree.admin.flypass.models.admin.UserAdmin
import cthree.admin.flypass.models.airline.Airline
import cthree.admin.flypass.models.airplane.Airplane
import cthree.admin.flypass.models.airport.Airport
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
    suspend fun saveDataAdmin(userAdmin: UserAdmin){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setId(userAdmin.id).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setEmail(userAdmin.email).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAccesstToken(userAdmin.accesstToken).build()
        }
    }

    suspend fun saveLoginStatus(paramIsLogin: Boolean){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setSaveLoginStatus(paramIsLogin).build()
        }
    }

    suspend fun saveDataAirportDepart(airport: Airport) {
        //Depart
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepartAirportCity(airport.city).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepartAirportCountry(airport.country).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepartAirportIata(airport.iata).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepartAirportId(airport.id).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepartAirportName(airport.name).build()
        }
    }

    suspend fun saveDataAirportArrive(airport: Airport){
        //Arrive
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setArriveAirportCity(airport.city).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setArriveAirportCountry(airport.country).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setArriveAirportIata(airport.iata).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setArriveAirportId(airport.id).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setArriveAirportName(airport.name).build()
        }
    }

    suspend fun saveDataAirline(airline: Airline){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirlineId(airline.id).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirlineIata(airline.iata).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirlineName(airline.name).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirlineImage(airline.image).build()
        }
    }

    suspend fun saveDataAirplane(airplane: Airplane){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirplaneIcao(airplane.icao).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirplaneId(airplane.id).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirplaneModel(airplane.model).build()
        }
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