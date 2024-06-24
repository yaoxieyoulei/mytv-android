package top.yogiczy.mytv.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import top.yogiczy.mytv.ui.utils.SP

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityClass = when (SP.appDeviceDisplayType) {
            SP.AppDeviceDisplayType.LEANBACK -> LeanbackActivity::class.java
            SP.AppDeviceDisplayType.MOBILE -> MobileActivity::class.java
            SP.AppDeviceDisplayType.PAD -> PadActivity::class.java
        }

        // TODO 切换时变化生硬
        startActivity(Intent(this, activityClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })

        finish()
    }
}
