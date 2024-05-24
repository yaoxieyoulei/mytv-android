package top.yogiczy.mytv

import android.app.Application
import top.yogiczy.mytv.ui.utils.SP

class MyTVApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        UnsafeTrustManager.enableUnsafeTrustManager()
        AppGlobal.cacheDir = applicationContext.cacheDir
        SP.init(applicationContext)
    }
}
