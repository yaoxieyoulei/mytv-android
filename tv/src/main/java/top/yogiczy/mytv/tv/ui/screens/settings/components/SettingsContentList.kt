package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yogiczy.mytv.tv.ui.rememberChildPadding

@Composable
fun SettingsContentList(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    val childPadding = rememberChildPadding()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = childPadding.bottom),
    ) {
        content()
    }
}