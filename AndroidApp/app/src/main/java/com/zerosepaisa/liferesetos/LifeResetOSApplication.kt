package com.zerosepaisa.liferesetos

import android.app.Application
import com.zerosepaisa.liferesetos.di.AppContainer

class LifeResetOSApplication : Application() {

    val appContainer: AppContainer by lazy {
        AppContainer(this)
    }
}