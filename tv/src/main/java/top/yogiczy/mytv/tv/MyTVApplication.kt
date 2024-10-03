package top.yogiczy.mytv.tv

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import top.yogiczy.mytv.core.data.AppData

class MyTVApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        AppData.init(applicationContext)
        UnsafeTrustManager.enableUnsafeTrustManager()
        initSentry()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader(this).newBuilder()
            .components {
                add(SvgDecoder.Factory())
            }
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this).build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .build()
            }
            .build()
    }

    private fun initSentry() {
        SentryAndroid.init(this) { options ->
            options.environment = BuildConfig.BUILD_TYPE
            options.dsn = BuildConfig.SENTRY_DSN
            options.tracesSampleRate = 1.0
            options.beforeSend =
                SentryOptions.BeforeSendCallback { event: SentryEvent, _: Hint ->
                    if (SentryLevel.ERROR == event.level || SentryLevel.FATAL == event.level) {
                        if (event.exceptions?.any { ex -> ex.type?.contains("HttpException") == true } == true) {
                            null
                        } else {
                            event
                        }
                    } else {
                        null
                    }
                }
        }
    }
}
