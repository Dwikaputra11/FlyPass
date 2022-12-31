package cthree.user.flypass

import android.content.Context
import android.os.Build.VERSION_CODES.Q
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.airport.AirportList
import cthree.user.flypass.worker.AirportWorker
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import retrofit2.Call

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Q])
class Testing {
    private lateinit var apiService: ApiService
    private lateinit var context: Context

    @Before
    fun setUp(){
        apiService = mockk()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun getAirportList(): Unit= runBlocking {
        val resp = mockk<Call<AirportList>>()

        every {
            runBlocking {
                apiService.apiServiceAirport()
            }
        } returns resp

        val result = apiService.apiServiceAirport()

        verify {
            runBlocking {
                apiService.apiServiceAirport()
            }
        }
        assertEquals(result, resp)
    }

//    // Test Worker
//    @Test
//    fun testAirportWorker(){
//        // get the Listenable Worker
//        val worker = TestListenableWorkerBuilder<AirportWorker>(context).build()
//
//        // run the worker synchronously
//        val result = worker.startWork().get()
//
//        MatcherAssert.assertThat(result, `is` (ListenableWorker.Result.success()))
//    }
}