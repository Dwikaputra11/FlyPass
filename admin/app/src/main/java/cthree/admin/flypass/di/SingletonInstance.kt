package cthree.admin.flypass.di

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.dao.AirlineDao
import cthree.admin.flypass.dao.AirplaneDao
import cthree.admin.flypass.dao.AirportDao
import cthree.admin.flypass.db.MyDatabase
import cthree.admin.flypass.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonInstance {

    private val interceptor: HttpLoggingInterceptor
        get(){
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level  = HttpLoggingInterceptor.Level.BODY
            }
        }

    private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Provides
    @Singleton
    fun providesService(): APIService =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(context: Application): MyDatabase =
        MyDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun providesAirportDao(myDatabase: MyDatabase): AirportDao =
        myDatabase.airportDao()

    @Provides
    @Singleton
    fun providesAirlineDao(myDatabase: MyDatabase): AirlineDao =
        myDatabase.airlineDao()

    @Provides
    @Singleton
    fun providesAirplaneDao(myDatabase: MyDatabase): AirplaneDao =
        myDatabase.airplaneDao()

    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(workerFactory: HiltWorkerFactory): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

}