package top.yogiczy.mytv

import android.app.Application

class MyTVApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGlobal.cacheDir = applicationContext.cacheDir
    }
}
