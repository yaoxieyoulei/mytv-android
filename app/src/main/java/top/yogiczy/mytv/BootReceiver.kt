package top.yogiczy.mytv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import top.yogiczy.mytv.activities.LeanbackActivity
import top.yogiczy.mytv.ui.utils.SP

/**
 * 开机自启动监听
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val sp: SharedPreferences = SP.getInstance(context)
            val bootLaunch = sp.getBoolean(SP.KEY.APP_BOOT_LAUNCH.name, false)

            if (bootLaunch) {
                context.startActivity(Intent(context, LeanbackActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }
}
