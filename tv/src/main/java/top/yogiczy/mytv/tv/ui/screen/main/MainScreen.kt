package top.yogiczy.mytv.tv.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelIdx
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelList
import top.yogiczy.mytv.core.data.entities.channel.ChannelList
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.screen.Screens
import top.yogiczy.mytv.tv.ui.screen.about.AboutScreen
import top.yogiczy.mytv.tv.ui.screen.channels.ChannelsScreen
import top.yogiczy.mytv.tv.ui.screen.dashboard.DashboardScreen
import top.yogiczy.mytv.tv.ui.screen.favorites.FavoritesScreen
import top.yogiczy.mytv.tv.ui.screen.loading.LoadingScreen
import top.yogiczy.mytv.tv.ui.screen.push.PushScreen
import top.yogiczy.mytv.tv.ui.screen.search.SearchScreen
import top.yogiczy.mytv.tv.ui.screen.settings.SettingsScreen
import top.yogiczy.mytv.tv.ui.screen.settings.SettingsSubCategories
import top.yogiczy.mytv.tv.ui.screen.update.UpdateScreen
import top.yogiczy.mytv.tv.ui.screen.update.UpdateViewModel
import top.yogiczy.mytv.tv.ui.screensold.settings.SettingsViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    updateViewModel: UpdateViewModel = viewModel(),
    onBackPressed: () -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by mainViewModel.uiState.collectAsState()

    val channelGroupListProvider = { (uiState as MainUiState.Ready).channelGroupList }
    val filteredChannelGroupListProvider = {
        ChannelGroupList(channelGroupListProvider().filter { it.name !in settingsViewModel.iptvChannelGroupHiddenList })
    }
    val favoriteChannelListProvider = {
        val favoriteChannelNameList = settingsViewModel.iptvChannelFavoriteList
        ChannelList(filteredChannelGroupListProvider().channelList
            .filter { favoriteChannelNameList.contains(it.name) })
    }
    val epgListProvider = { (uiState as MainUiState.Ready).epgList }

    val navController = rememberNavController()

    fun onChannelSelected(channel: Channel) {
        settingsViewModel.iptvLastChannelIdx =
            filteredChannelGroupListProvider().channelIdx(channel)
        navController.navigate(Screens.Live())
    }

    fun onChannelFavoriteToggle(channel: Channel) {
        if (!settingsViewModel.iptvChannelFavoriteEnable) return

        if (settingsViewModel.iptvChannelFavoriteList.contains(channel.name)) {
            settingsViewModel.iptvChannelFavoriteList -= channel.name
            Snackbar.show("取消收藏：${channel.name}")
        } else {
            settingsViewModel.iptvChannelFavoriteList += channel.name
            Snackbar.show("已收藏：${channel.name}")
        }
    }

    fun onChannelFavoriteClear() {
        if (!settingsViewModel.iptvChannelFavoriteEnable) return

        settingsViewModel.iptvChannelFavoriteList = emptySet()
        Snackbar.show("已清空所有收藏")
    }

    fun checkUpdate() {
        coroutineScope.launch {
            delay(3000)
            val currentVersion =
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            updateViewModel.checkUpdate(currentVersion, settingsViewModel.updateChannel)
            if (!updateViewModel.isUpdateAvailable) return@launch
            if (settingsViewModel.appLastLatestVersion == updateViewModel.latestRelease.version) return@launch

            settingsViewModel.appLastLatestVersion = updateViewModel.latestRelease.version
            if (settingsViewModel.updateForceRemind) {
                navController.navigate(Screens.Update())
            } else {
                Snackbar.show("发现新版本: v${updateViewModel.latestRelease.version}")
            }
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Loading(),
        builder = {
            composable(Screens.Loading()) {
                LoadingScreen(
                    mainUiState = uiState,
                    toDashboardScreen = {
                        navController.navigateUp()
                        navController.navigate(Screens.Dashboard())
                        checkUpdate()
                    },
                    toSettingsScreen = { navController.navigate(Screens.Settings()) },
                    onBackPressed = onBackPressed,
                )
            }

            composable(Screens.Dashboard()) {
                DashboardScreen(
                    currentIptvSourceProvider = { settingsViewModel.iptvSourceCurrent },
                    favoriteChannelListProvider = favoriteChannelListProvider,
                    onChannelSelected = { onChannelSelected(it) },
                    epgListProvider = epgListProvider,
                    toLiveScreen = { navController.navigate(Screens.Live()) },
                    toChannelsScreen = { navController.navigate(Screens.Channels()) },
                    toFavoritesScreen = { navController.navigate(Screens.Favorites()) },
                    toSearchScreen = { navController.navigate(Screens.Search()) },
                    toPushScreen = { navController.navigate(Screens.Push()) },
                    toSettingsScreen = { navController.navigate(Screens.Settings()) },
                    toAboutScreen = { navController.navigate(Screens.About()) },
                    toSettingsIptvSourceScreen = {
                        navController.navigate(
                            Screens.Settings.withArgs(SettingsSubCategories.IPTV_SOURCE)
                        )
                    },
                    onBackPressed = onBackPressed,
                )
            }

            composable(Screens.Live()) {
                top.yogiczy.mytv.tv.ui.screensold.main.MainScreen(
                    mainUiState = uiState as MainUiState.Ready,
                    onBackPressed = { navController.navigateUp() },
                )
            }

            composable(Screens.Channels()) {
                ChannelsScreen(
                    channelGroupListProvider = filteredChannelGroupListProvider,
                    onChannelSelected = { onChannelSelected(it) },
                    onChannelFavoriteToggle = { onChannelFavoriteToggle(it) },
                    epgListProvider = epgListProvider,
                    onBackPressed = { navController.navigateUp() },
                )
            }

            composable(Screens.Favorites()) {
                FavoritesScreen(
                    channelListProvider = favoriteChannelListProvider,
                    onChannelSelected = { onChannelSelected(it) },
                    onChannelFavoriteToggle = { onChannelFavoriteToggle(it) },
                    onChannelFavoriteClear = { onChannelFavoriteClear() },
                    epgListProvider = epgListProvider,
                    onBackPressed = { navController.navigateUp() },
                )
            }

            composable(Screens.Search()) {
                SearchScreen(
                    channelGroupListProvider = filteredChannelGroupListProvider,
                    onChannelSelected = { onChannelSelected(it) },
                    onChannelFavoriteToggle = { onChannelFavoriteToggle(it) },
                    epgListProvider = epgListProvider,
                    onBackPressed = { navController.navigateUp() },
                )
            }

            composable(Screens.Push()) {
                PushScreen(
                    onBackPressed = { navController.navigateUp() },
                )
            }

            composable(
                Screens.Settings(),
                arguments = listOf(
                    navArgument(SettingsScreen.START_DESTINATION) { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                SettingsScreen(
                    startDestinationProvider = {
                        val startDestination =
                            backStackEntry.arguments?.getString(SettingsScreen.START_DESTINATION)

                        if (startDestination == "{${SettingsScreen.START_DESTINATION}}") null
                        else startDestination
                    },
                    channelGroupListProvider = channelGroupListProvider,
                    settingsViewModel = settingsViewModel,
                    onReload = {
                        mainViewModel.init()
                        navController.navigateUp()
                        navController.navigate(Screens.Loading())
                    },
                    onBackPressed = { navController.navigateUp() },
                )
            }

            composable(Screens.About()) {
                AboutScreen(
                    latestVersionProvider = { updateViewModel.latestRelease.version },
                    toUpdateScreen = { navController.navigate(Screens.Update()) },
                    onBackPressed = { navController.navigateUp() },
                )
            }

            composable(Screens.Update()) {
                UpdateScreen(
                    updateViewModel = updateViewModel,
                    onBackPressed = { navController.navigateUp() },
                )
            }
        },
    )
}