package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screens.settings.LocalSettings
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsCategories
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsCategoryList(
    modifier: Modifier = Modifier,
    currentCategoryProvider: () -> SettingsCategories = { SettingsCategories.entries.first() },
    onCategorySelected: (SettingsCategories) -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    LazyColumn(
        contentPadding = PaddingValues(top = childPadding.top, bottom = childPadding.bottom),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.ifElse(
            LocalSettings.current.uiFocusOptimize,
            Modifier.focusRestorer(),
        ),
    ) {
        itemsIndexed(SettingsCategories.entries) { index, category ->
            val isSelected by remember { derivedStateOf { currentCategoryProvider() == category } }

            SettingsCategoryItem(
                modifier = Modifier.ifElse(index == 0, Modifier.focusOnLaunchedSaveable()),
                icon = category.icon,
                title = category.title,
                isSelectedProvider = { isSelected },
                onCategorySelected = { onCategorySelected(category) },
            )
        }
    }
}

@Composable
private fun SettingsCategoryItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    isSelectedProvider: () -> Boolean = { false },
    onCategorySelected: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    ListItem(
        colors = ListItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(0.1f),
            selectedContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        selected = isSelectedProvider(),
        onClick = { },
        leadingContent = { Icon(icon, title) },
        headlineContent = { Text(text = title) },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
                if (isFocused) onCategorySelected()
            }
            .handleKeyEvents(
                isFocused = { isFocused },
                focusRequester = focusRequester,
                onSelect = { focusManager.moveFocus(FocusDirection.Right) },
            ),
    )
}

@Preview
@Composable
private fun SettingsCategoryItemPreview() {
    MyTVTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingsCategoryItem(
                icon = SettingsCategories.ABOUT.icon,
                title = SettingsCategories.ABOUT.title,
            )

            SettingsCategoryItem(
                icon = SettingsCategories.ABOUT.icon,
                title = SettingsCategories.ABOUT.title,
                isSelectedProvider = { true },
            )
        }
    }
}

@Preview
@Composable
private fun SettingsCategoryListPreview() {
    MyTVTheme {
        SettingsCategoryList()
    }
}