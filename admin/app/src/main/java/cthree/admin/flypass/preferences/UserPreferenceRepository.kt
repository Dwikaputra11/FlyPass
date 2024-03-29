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
import cthree.admin.flypass.models.update.ForUpdate
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

    suspend fun saveDataForUpdateTicket(forUpdate: ForUpdate){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setIdTicket(forUpdate.idTicket).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setFlightCode(forUpdate.flightNumber).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirlineName(forUpdate.airlineName).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirlineId(forUpdate.airlineId).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirplaneModel(forUpdate.airplaneType).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAirplaneId(forUpdate.airplaneId).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepartAirportCity(forUpdate.departAirportCity).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepartAirportId(forUpdate.departAirportId).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setArriveAirportCity(forUpdate.arriveAirportCity).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setArriveAirportId(forUpdate.arriveAirportId).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setCalendarDepart(forUpdate.calendarDepart).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setCalendarArrival(forUpdate.calendarArrival).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setTimeDepart(forUpdate.timeDepart).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setTimeArrival(forUpdate.timeArrival).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setPrice(forUpdate.price).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setSpSeatClass(forUpdate.spSeatClass).build()
        }
    }

    suspend fun clearDataTicket(){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepartAirportCity().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepartAirportCountry().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepartAirportId().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArriveAirportCity().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArriveAirportCountry().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArriveAirportId().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearAirlineName().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearAirplaneModel().build()
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