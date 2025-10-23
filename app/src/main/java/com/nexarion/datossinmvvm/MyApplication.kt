package com.nexarion.datossinmvvm

import android.app.Application
import androidx.room.Room

class MyApplication : Application() {
    val database: UserDatabase by lazy {
        Room.databaseBuilder(
            this,
            UserDatabase::class.java,
            "user_db"
        ).build()
    }
}