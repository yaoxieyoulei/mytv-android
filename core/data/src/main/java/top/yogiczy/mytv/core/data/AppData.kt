package top.yogiczy.mytv.core.data

import android.content.Context
import top.yogiczy.mytv.core.data.utils.Globals
import top.yogiczy.mytv.core.data.utils.SP

object AppData {
    fun init(context: Context) {
        Globals.cacheDir = context.cacheDir
        SP.init(context)
    }
}