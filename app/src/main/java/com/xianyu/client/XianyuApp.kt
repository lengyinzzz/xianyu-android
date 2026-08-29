package com.xianyu.client

import android.app.Application
import com.xianyu.client.util.Prefs

class XianyuApp : Application() {
    lateinit var prefs: Prefs
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(this)
    }
}
