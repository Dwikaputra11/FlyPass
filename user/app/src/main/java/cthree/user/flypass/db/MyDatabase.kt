package cthree.user.flypass.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cthree.user.flypass.dao.AirportDao
import cthree.user.flypass.models.airport.Airport
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Airport::class], version = 1 )
abstract class MyDatabase : RoomDatabase() {

    abstract fun airportDao() : AirportDao

    companion object {

        private const val NUMBER_OF_THREADS = 4

        @Volatile
        private var INSTANCE: MyDatabase? = null
        val databaseWriteExecutor: ExecutorService =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS) // using for creating a database but not in main thread
//     that can interrupt our ui, so we have to create database in background

        //     that can interrupt our ui, so we have to create database in background
        fun getDatabase(context: Context): MyDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(MyDatabase::class.java) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "flypass.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}