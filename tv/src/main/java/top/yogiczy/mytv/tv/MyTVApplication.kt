package top.yogiczy.mytv.tv

import android.app.Application
import top.yogiczy.mytv.core.data.AppData

class MyTVApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppData.init(applicationContext)
        UnsafeTrustManager.enableUnsafeTrustManager()
    }
}
