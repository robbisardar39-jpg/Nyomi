package eu.kanade.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import eu.kanade.presentation.category.visualName
import tachiyomi.domain.category.model.Category
import tachiyomi.presentation.core.components.material.TabText

@Composable
internal fun LibraryTabs(
    categories: List<Category>,
    pagerState: PagerState,
    getItemCountForCategory: (Category) -> Int?,
    onTabItemClick: (Int) -> Unit,
    hazeState: HazeState? = null,
) {
    val currentPageIndex = pagerState.currentPage.coerceAtMost(categories.lastIndex)
    val isDark = isSystemInDarkTheme()
    val unselectedFill = if (isDark) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.42f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.56f)
    }
    val selectedFill = if (isDark) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
    } else {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.82f)
    }
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }

    Column(modifier = Modifier.zIndex(2f)) {
        PrimaryScrollableTabRow(
            selectedTabIndex = currentPageIndex,
            containerColor = Color.Transparent,
            edgePadding = 8.dp,
            indicator = {},
            divider = {},
        ) {
            categories.forEachIndexed { index, category ->
                val isSelected = currentPageIndex == index
                val pillShape = RoundedCornerShape(50)
                Tab(
                    selected = isSelected,
                    onClick = { onTabItemClick(index) },
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                        .clip(pillShape)
                        .then(
                            if (hazeState != null) {
                                Modifier.hazeEffect(
                                    state = hazeState,
                                    style = HazeStyle(
                                        backgroundColor = Color.Transparent,
                                        tint = HazeTint(if (isSelected) selectedFill else unselectedFill),
                                        blurRadius = 22.dp,
                                    ),
                                )
                            } else {
                                Modifier.background(if (isSelected) selectedFill else unselectedFill)
                            },
                        )
                        .border(1.dp, borderColor, pillShape),
                    text = {
                        TabText(
                            text = category.visualName,
                            badgeCount = getItemCountForCategory(category),
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
