package eu.kanade.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.zIndex
import eu.kanade.presentation.category.visualName
import tachiyomi.domain.category.model.Category
import tachiyomi.presentation.core.components.material.TabText

@Composable
internal fun LibraryTabs(
    categories: List<Category>,
    pagerState: PagerState,
    getItemCountForCategory: (Category) -> Int?,
    onTabItemClick: (Int) -> Unit,
) {
    val currentPageIndex = pagerState.currentPage.coerceAtMost(categories.lastIndex)
    Column(modifier = Modifier.zIndex(2f)) {
        PrimaryScrollableTabRow(
            selectedTabIndex = currentPageIndex,
            edgePadding = 8.dp,
            indicator = {},
            // TODO: use default when width is fixed upstream
            // https://issuetracker.google.com/issues/242879624
            divider = {},
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = currentPageIndex == index,
                    onClick = { onTabItemClick(index) },
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (currentPageIndex == index) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surface,
                        ),
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

        HorizontalDivider()
    }
}
