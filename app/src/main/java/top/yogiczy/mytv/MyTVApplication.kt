package top.yogiczy.mytv

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import top.yogiczy.mytv.data.repositories.EpgRepository
import top.yogiczy.mytv.data.repositories.EpgRepositoryImpl
import top.yogiczy.mytv.data.repositories.IptvRepository
import top.yogiczy.mytv.data.repositories.IptvRepositoryImpl

@HiltAndroidApp
class MyTVApplication : Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideIptvRepository(context: Context): IptvRepository {
        return IptvRepositoryImpl(context)
    }

    @Provides
    fun provideEpgRepository(context: Context): EpgRepository {
        return EpgRepositoryImpl(context)
    }
}