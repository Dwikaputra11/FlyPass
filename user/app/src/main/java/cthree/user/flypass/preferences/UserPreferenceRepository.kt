package cthree.user.flypass.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import cthree.user.flypass.UserProto
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.models.user.Profile
import cthree.user.flypass.preferences.UserPreferenceRepository.Companion.userPreferencesStore
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

    suspend fun saveLoginData(profile: Profile){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setEmail(profile.email).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setId(profile.id).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setRoleId(profile.roleId).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setName(profile.name).build()
        }
    }

    suspend fun saveBookingData(depFlight: String, arrFlight: String?, passList: String, contactDetail: String, baggageList: String){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepFlight(depFlight).build()
        }
        if(arrFlight != null){
            context.userPreferencesStore.updateData { preferences ->
                preferences.toBuilder().setArrFlight(arrFlight).build()
            }
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setPassengerList(passList).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setContactDetail(contactDetail).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setBaggageList(contactDetail).build()
        }
    }

    suspend fun savePaymentData(bookingCode: String,addOnsPrice: Int, depFlightPrice: Int,arrFlightPrice: Int?, totalPrice: Int){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setBookingCode(bookingCode).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAddOnsPrice(addOnsPrice).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setDepFlightPrice(depFlightPrice).build()
        }
        if(arrFlightPrice != null){
            context.userPreferencesStore.updateData { preferences ->
                preferences.toBuilder().setArrFlightPrice(arrFlightPrice).build()
            }
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setTotalPrice(totalPrice).build()
        }
    }

    suspend fun clearBookingData(){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearBookingCode().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearPassengerList().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearAddOnsPrice().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepFlight().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArrFlight().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearContactDetail().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearTotalPrice().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepFlightPrice().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArrFlightPrice().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearContactDetail().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearBaggageList().build()
        }
    }

    suspend fun saveToken(token: String){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setToken(token).build()
        }
    }
    suspend fun clearToken(){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearToken().build()
        }
    }
    suspend fun saveRefreshToken(token: String){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setRefreshToken(token).build()
        }
    }
    suspend fun clearRefreshToken(){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearRefreshToken().build()
        }
    }

    // save data to data store proto
    suspend fun saveDataUser(profile: Profile){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setEmail(profile.email).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setBirthDate(profile.birthDate).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setGender(profile.gender).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setId(profile.id).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setName(profile.name).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setPhone(profile.phone).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setRoleId(profile.roleId).build()
        }
        if(profile.image != null){
            context.userPreferencesStore.updateData { preferences ->
                preferences.toBuilder().setImage(profile.image).build()
            }
        }
    }

    suspend fun clearDataUser(){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearEmail().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearId().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearImage().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearName().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearPhone().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearRoleId().build()
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


    // delete datastore
    suspend fun clearData(){

    }

    suspend fun clearDataDepartArrive(){
        //Depart
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepartAirportCity().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepartAirportCountry().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepartAirportIata().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepartAirportId().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearDepartAirportName().build()
        }

        //Arrive
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArriveAirportCity().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArriveAirportCountry().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArriveAirportIata().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArriveAirportId().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearArriveAirportName().build()
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