package eu.kanade.presentation.library.components

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FlipToBack
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.unit.dp
import tachiyomi.domain.category.model.Category
import eu.kanade.presentation.category.visualName
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.components.SearchToolbar
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.Pill
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.theme.active

@Composable
fun LibraryToolbar(
    hasActiveFilters: Boolean,
    selectedCount: Int,
    title: LibraryToolbarTitle,
    onClickUnselectAll: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickInvertSelection: () -> Unit,
    onClickFilter: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickGlobalUpdate: () -> Unit,
    onClickOpenRandomManga: () -> Unit,
    onClickSyncNow: () -> Unit,
    // SY -->
    onClickSyncExh: (() -> Unit)?,
    isSyncEnabled: Boolean,
    // SY <--
    searchQuery: String?,
    onSearchQueryChange: (String?) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
    onInvalidateDownloadCache: (Context) -> Unit,
) {
    if (selectedCount > 0) {
        LibrarySelectionToolbar(
            selectedCount = selectedCount,
            onClickUnselectAll = onClickUnselectAll,
            onClickSelectAll = onClickSelectAll,
            onClickInvertSelection = onClickInvertSelection,
        )
    }
}

@Composable
fun LibrarySearchPill(
    searchQuery: String?,
    onSearchQueryChange: (String?) -> Unit,
    hasFilters: Boolean = false,
    onClickFilter: (() -> Unit)? = null,
    onClickRefresh: (() -> Unit)? = null,
    onClickGlobalUpdate: (() -> Unit)? = null,
    onClickOpenRandomManga: (() -> Unit)? = null,
    onClickSyncNow: (() -> Unit)? = null,
    // SY -->
    onClickSyncExh: (() -> Unit)? = null,
    isSyncEnabled: Boolean = false,
    // SY <--
    onInvalidateDownloadCache: ((Context) -> Unit)? = null,
    hazeState: HazeState? = null,
    categories: List<Category> = emptyList(),
    selectedCategoryIndex: Int = 0,
    showCategoryTabs: Boolean = false,
    isExpanded: Boolean = true,
    getItemCountForCategory: ((Category) -> Int?)? = null,
    onTabItemClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activeQuery = searchQuery.orEmpty()
    val isDark = isSystemInDarkTheme()
    val pillTint = if (isDark) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
    }
    val pillBorder = if (isDark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }
    val hasCategories = showCategoryTabs &&
        categories.isNotEmpty() &&
        (categories.size > 1 || !categories.first().isSystemCategory)
    val showExpandedCategories = hasCategories && isExpanded
    val searchRowHeight by animateDpAsState(
        targetValue = if (isExpanded) 50.dp else 44.dp,
        label = "searchRowHeight",
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (showExpandedCategories) 26.dp else 28.dp,
        label = "pillCornerRadius",
    )
    val containerShape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(containerShape)
            .then(
                if (hazeState != null) {
                    Modifier.hazeEffect(
                        state = hazeState,
                        style = HazeStyle(
                            backgroundColor = Color.Transparent,
                            tint = HazeTint(pillTint),
                            blurRadius = 24.dp,
                        ),
                    )
                } else {
                    Modifier.background(pillTint, containerShape)
                },
            )
            .border(1.dp, pillBorder, containerShape)
            .clickable {
                if (searchQuery == null) {
                    onSearchQueryChange("")
                }
            },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(searchRowHeight)
                    .padding(start = 16.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (activeQuery.isEmpty()) {
                        Text(
                            text = "Search library",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    BasicTextField(
                        value = activeQuery,
                        onValueChange = { onSearchQueryChange(it) },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (activeQuery.isNotEmpty() || searchQuery != null) {
                    IconButton(
                        onClick = { onSearchQueryChange(null) },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (onClickFilter != null) {
                    val filterTint = if (hasFilters) MaterialTheme.colorScheme.active else MaterialTheme.colorScheme.onSurfaceVariant
                    IconButton(
                        onClick = onClickFilter,
                        modifier = Modifier.size(38.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = stringResource(MR.strings.action_filter),
                            modifier = Modifier.size(21.dp),
                            tint = filterTint,
                        )
                    }
                }
                val overflowItems = buildList {
                    if (onClickGlobalUpdate != null) {
                        add(
                            AppBar.OverflowAction(
                                title = stringResource(MR.strings.action_update_library),
                                onClick = onClickGlobalUpdate,
                            ),
                        )
                    }
                    if (onClickRefresh != null) {
                        add(
                            AppBar.OverflowAction(
                                title = stringResource(MR.strings.action_update_category),
                                onClick = onClickRefresh,
                            ),
                        )
                    }
                    if (onClickOpenRandomManga != null) {
                        add(
                            AppBar.OverflowAction(
                                title = stringResource(MR.strings.action_open_random_manga),
                                onClick = onClickOpenRandomManga,
                            ),
                        )
                    }
                    if (onInvalidateDownloadCache != null) {
                        add(
                            AppBar.OverflowAction(
                                title = stringResource(MR.strings.pref_invalidate_download_cache),
                                onClick = { onInvalidateDownloadCache(context) },
                            ),
                        )
                    }
                    // SY -->
                    if (onClickSyncExh != null) {
                        add(
                            AppBar.OverflowAction(
                                title = stringResource(SYMR.strings.sync_favorites),
                                onClick = onClickSyncExh,
                            ),
                        )
                    }
                    if (isSyncEnabled && onClickSyncNow != null) {
                        add(
                            AppBar.OverflowAction(
                                title = stringResource(SYMR.strings.sync_library),
                                onClick = onClickSyncNow,
                            ),
                        )
                    }
                    // SY <--
                }
                if (overflowItems.isNotEmpty()) {
                    AppBarActions(
                        actions = persistentListOf(*overflowItems.toTypedArray()),
                    )
                }
            }

            AnimatedVisibility(
                visible = showExpandedCategories,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = pillBorder,
                    )
                    val scrollState = rememberScrollState()
                    val clampedIndex = selectedCategoryIndex.coerceIn(0, (categories.size - 1).coerceAtLeast(0))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        categories.forEachIndexed { index, category ->
                            val isSelected = clampedIndex == index
                            val chipBg = if (isSelected) {
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = if (isDark) 0.78f else 0.88f)
                            } else {
                                if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)
                            }
                            val chipTextColor = if (isSelected) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            val count = getItemCountForCategory?.invoke(category)
                            val label = if (count != null) "${category.visualName} ($count)" else category.visualName
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(chipBg)
                                    .clickable { onTabItemClick?.invoke(index) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = chipTextColor,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibrarySelectionToolbar(
    selectedCount: Int,
    onClickUnselectAll: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickInvertSelection: () -> Unit,
) {
    AppBar(
        titleContent = { Text(text = "$selectedCount") },
        actions = {
            AppBarActions(
                persistentListOf(
                    AppBar.Action(
                        title = stringResource(MR.strings.action_select_all),
                        icon = Icons.Outlined.SelectAll,
                        onClick = onClickSelectAll,
                    ),
                    AppBar.Action(
                        title = stringResource(MR.strings.action_select_inverse),
                        icon = Icons.Outlined.FlipToBack,
                        onClick = onClickInvertSelection,
                    ),
                ),
            )
        },
        isActionMode = true,
        onCancelActionMode = onClickUnselectAll,
    )
}

@Immutable
data class LibraryToolbarTitle(
    val text: String,
    val numberOfManga: Int? = null,
)
