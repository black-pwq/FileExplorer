package com.example.explorerx

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.example.explorerx.repository.FileEntryRepo

class ExApplication: Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        FileEntryRepo.initialize(this)
    }
}