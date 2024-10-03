package top.yogiczy.mytv.tv.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSourceList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSourceList
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsAppScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsCloudSyncScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsControlScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsDebugScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsEpgScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsIptvScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsLogScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsNetworkScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsThemeScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsUiScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsUpdateScreen
import top.yogiczy.mytv.tv.ui.screen.settings.categories.SettingsVideoPlayerScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsChannelGroupVisibilityScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsCloudSyncProviderScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsEpgRefreshTimeThresholdScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsEpgSourceScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsIptvHybridModeScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsIptvSourceCacheTimeScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsIptvSourceScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsUiDensityScaleRatioScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsUiFontScaleRatioScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsUiScreenAutoCloseScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsUiTimeShowModeScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsUpdateChannelScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsVideoPlayerDisplayModeScreen
import top.yogiczy.mytv.tv.ui.screen.settings.subcategories.SettingsVideoPlayerLoadTimeoutScreen
import top.yogiczy.mytv.tv.ui.utils.navigateSingleTop

object SettingsScreen {
    const val START_DESTINATION = "startDestination"
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    startDestinationProvider: () -> String? = { null },
    channelGroupListProvider: () -> ChannelGroupList = { ChannelGroupList() },
    settingsViewModel: SettingsViewModel = viewModel(),
    onReload: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        while (true) {
            settingsViewModel.refresh()
            delay(1000)
        }
    }

    val navController = rememberNavController()

    AppScreen(modifier = modifier, onBackPressed = onBackPressed) {
        NavHost(
            navController = navController,
            startDestination = startDestinationProvider() ?: "categories",
            builder = {
                composable(route = "categories") {
                    SettingsCategoriesScreen(
                        toSettingsCategoryScreen = { navController.navigateSingleTop(it.name) },
                        onBackPressed = onBackPressed,
                    )
                }

                composable(SettingsCategories.APP.name) {
                    SettingsAppScreen(
                        settingsViewModel = settingsViewModel,
                        onReload = onReload,
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.IPTV.name) {
                    SettingsIptvScreen(
                        channelGroupListProvider = channelGroupListProvider,
                        settingsViewModel = settingsViewModel,
                        toIptvSourceScreen = { navController.navigateSingleTop(SettingsSubCategories.IPTV_SOURCE.name) },
                        toIptvSourceCacheTimeScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.IPTV_SOURCE_CACHE_TIME.name)
                        },
                        toChannelGroupVisibilityScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.CHANNEL_GROUP_VISIBILITY.name)
                        },
                        toIptvHybridModeScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.IPTV_HYBRID_MODE.name)
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.EPG.name) {
                    SettingsEpgScreen(
                        settingsViewModel = settingsViewModel,
                        toEpgSourceScreen = { navController.navigateSingleTop(SettingsSubCategories.EPG_SOURCE.name) },
                        toEpgRefreshTimeThresholdScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.EPG_REFRESH_TIME_THRESHOLD.name)
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.UI.name) {
                    SettingsUiScreen(
                        settingsViewModel = settingsViewModel,
                        toUiTimeShowModeScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.UI_TIME_SHOW_MODE.name)
                        },
                        toUiScreenAutoCloseDelayScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.UI_SCREEN_AUTO_CLOSE_DELAY.name)
                        },
                        toUiDensityScaleRatioScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.UI_DENSITY_SCALE_RATIO.name)
                        },
                        toUiFontScaleRatioScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.UI_FONT_SCALE_RATIO.name)
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.CONTROL.name) {
                    SettingsControlScreen(
                        settingsViewModel = settingsViewModel,
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.VIDEO_PLAYER.name) {
                    SettingsVideoPlayerScreen(
                        settingsViewModel = settingsViewModel,
                        toVideoPlayerDisplayModeScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.VIDEO_PLAYER_DISPLAY_MODE.name)
                        },
                        toVideoPlayerLoadTimeoutScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.VIDEO_PLAYER_LOAD_TIMEOUT.name)
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.UPDATE.name) {
                    SettingsUpdateScreen(
                        settingsViewModel = settingsViewModel,
                        toUpdateChannelScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.UPDATE_CHANNEL.name)
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.NETWORK.name) {
                    SettingsNetworkScreen(
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.THEME.name) {
                    SettingsThemeScreen(
                        settingsViewModel = settingsViewModel,
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.CLOUD_SYNC.name) {
                    SettingsCloudSyncScreen(
                        settingsViewModel = settingsViewModel,
                        toCloudSyncProviderScreen = {
                            navController.navigateSingleTop(SettingsSubCategories.CLOUD_SYNC_PROVIDER.name)
                        },
                        onReload = onReload,
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.DEBUG.name) {
                    SettingsDebugScreen(
                        settingsViewModel = settingsViewModel,
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsCategories.LOG.name) {
                    SettingsLogScreen(
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.IPTV_SOURCE.name) {
                    SettingsIptvSourceScreen(
                        currentIptvSourceProvider = { settingsViewModel.iptvSourceCurrent },
                        iptvSourceListProvider = { settingsViewModel.iptvSourceList },
                        onIptvSourceSelected = {
                            if (settingsViewModel.iptvSourceCurrent != it) {
                                settingsViewModel.iptvSourceCurrent = it
                                settingsViewModel.iptvChannelGroupHiddenList = emptySet()
                            }
                            onReload()
                        },
                        onIptvSourceDelete = {
                            settingsViewModel.iptvSourceList =
                                IptvSourceList(settingsViewModel.iptvSourceList - it)
                        },
                        onBackPressed = {
                            if (!navController.navigateUp()) onBackPressed()
                        },
                    )
                }

                composable(SettingsSubCategories.IPTV_SOURCE_CACHE_TIME.name) {
                    SettingsIptvSourceCacheTimeScreen(
                        cacheTimeProvider = { settingsViewModel.iptvSourceCacheTime },
                        onCacheTimeChanged = {
                            settingsViewModel.iptvSourceCacheTime = it
                            navController.navigateUp()
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.CHANNEL_GROUP_VISIBILITY.name) {
                    SettingsChannelGroupVisibilityScreen(
                        channelGroupListProvider = channelGroupListProvider,
                        channelGroupNameHiddenListProvider = { settingsViewModel.iptvChannelGroupHiddenList.toList() },
                        onChannelGroupNameHiddenListChange = {
                            settingsViewModel.iptvChannelGroupHiddenList = it.toSet()
                        },
                        onBackPressed = { onReload() },
                    )
                }

                composable(SettingsSubCategories.IPTV_HYBRID_MODE.name) {
                    SettingsIptvHybridModeScreen(
                        hybridModeProvider = { settingsViewModel.iptvHybridMode },
                        onHybridModeChanged = {
                            settingsViewModel.iptvHybridMode = it
                            navController.navigateUp()
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.EPG_SOURCE.name) {
                    SettingsEpgSourceScreen(
                        currentEpgSourceProvider = { settingsViewModel.epgSourceCurrent },
                        epgSourceListProvider = { settingsViewModel.epgSourceList },
                        onEpgSourceSelected = {
                            settingsViewModel.epgSourceCurrent = it
                            onReload()
                        },
                        onEpgSourceDelete = {
                            settingsViewModel.epgSourceList =
                                EpgSourceList(settingsViewModel.epgSourceList - it)
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.EPG_REFRESH_TIME_THRESHOLD.name) {
                    SettingsEpgRefreshTimeThresholdScreen(
                        timeThresholdProvider = { settingsViewModel.epgRefreshTimeThreshold },
                        onTimeThresholdChanged = { settingsViewModel.epgRefreshTimeThreshold = it },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.UI_TIME_SHOW_MODE.name) {
                    SettingsUiTimeShowModeScreen(
                        timeShowModeProvider = { settingsViewModel.uiTimeShowMode },
                        onTimeShowModeChanged = {
                            settingsViewModel.uiTimeShowMode = it
                            navController.navigateUp()
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.UI_SCREEN_AUTO_CLOSE_DELAY.name) {
                    SettingsUiScreenAutoCloseScreen(
                        delayProvider = { settingsViewModel.uiScreenAutoCloseDelay },
                        onDelayChanged = { settingsViewModel.uiScreenAutoCloseDelay = it },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.UI_DENSITY_SCALE_RATIO.name) {
                    SettingsUiDensityScaleRatioScreen(
                        scaleRatioProvider = { settingsViewModel.uiDensityScaleRatio },
                        onScaleRatioChanged = { settingsViewModel.uiDensityScaleRatio = it },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.UI_FONT_SCALE_RATIO.name) {
                    SettingsUiFontScaleRatioScreen(
                        scaleRatioProvider = { settingsViewModel.uiFontScaleRatio },
                        onScaleRatioChanged = { settingsViewModel.uiFontScaleRatio = it },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.VIDEO_PLAYER_DISPLAY_MODE.name) {
                    SettingsVideoPlayerDisplayModeScreen(
                        displayModeProvider = { settingsViewModel.videoPlayerDisplayMode },
                        onDisplayModeChanged = {
                            settingsViewModel.videoPlayerDisplayMode = it
                            navController.navigateUp()
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.VIDEO_PLAYER_LOAD_TIMEOUT.name) {
                    SettingsVideoPlayerLoadTimeoutScreen(
                        timeoutProvider = { settingsViewModel.videoPlayerLoadTimeout },
                        onTimeoutChanged = {
                            settingsViewModel.videoPlayerLoadTimeout = it
                            navController.navigateUp()
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.UPDATE_CHANNEL.name) {
                    SettingsUpdateChannelScreen(
                        updateChannelProvider = { settingsViewModel.updateChannel },
                        onUpdateChannelChanged = {
                            settingsViewModel.updateChannel = it
                            navController.navigateUp()
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }

                composable(SettingsSubCategories.CLOUD_SYNC_PROVIDER.name) {
                    SettingsCloudSyncProviderScreen(
                        providerProvider = { settingsViewModel.cloudSyncProvider },
                        onProviderChanged = {
                            settingsViewModel.cloudSyncProvider = it
                            navController.navigateUp()
                        },
                        onBackPressed = { navController.navigateUp() },
                    )
                }
            }
        )
    }
}
