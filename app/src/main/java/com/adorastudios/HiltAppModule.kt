package com.adorastudios

import android.app.Application
import com.adorastudios.androidbluetoothmessenger.data.BluetoothControllerImpl
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltAppModule {
    @Provides
    @Singleton
    fun provideBluetoothController(app: Application): BluetoothController {
        return BluetoothControllerImpl(app)
    }
}
