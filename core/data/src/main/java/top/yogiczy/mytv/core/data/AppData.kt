package top.yogiczy.mytv.core.data

import android.content.Context
import android.provider.Settings
import top.yogiczy.mytv.core.data.utils.Globals
import top.yogiczy.mytv.core.data.utils.SP

object AppData {
    fun init(context: Context) {
        Globals.cacheDir = context.cacheDir
        Globals.resources = context.resources
        Globals.deviceName = Settings.Secure.getString(context.contentResolver, "bluetooth_name")

        SP.init(context)
    }
}