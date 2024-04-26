package top.yogiczy.mytv

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import top.yogiczy.mytv.ui.App
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.HttpServer
import top.yogiczy.mytv.ui.utils.SP
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        UnsafeTrustManager.enableUnsafeTrustManager()
        installSplashScreen()
        SP.init(context = applicationContext)
        super.onCreate(savedInstanceState)

        setContent {
            // 隐藏状态栏、导航栏
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, window.decorView).let { insetsController ->
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            // 屏幕常亮
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            HttpServer.start(applicationContext)

            MyTVTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    App(
                        onBackPressed = {
                            finish()
                            exitProcess(0)
                        }
                    )
                }
            }
        }
    }
}
