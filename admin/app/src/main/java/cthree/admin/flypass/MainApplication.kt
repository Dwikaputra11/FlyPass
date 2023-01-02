package cthree.admin.flypass

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var workConfiguration: Configuration

    override fun getWorkManagerConfiguration(): Configuration {
        return workConfiguration
    }

}