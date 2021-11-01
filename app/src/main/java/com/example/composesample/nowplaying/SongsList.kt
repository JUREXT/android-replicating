package com.example.composesample.nowplaying

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composesample.ModelSongInfo
import com.example.composesample.PlaybackData
import com.example.composesample.lerp
import com.example.composesample.toPx
import com.example.composesample.ui.theme.PlayerTheme

/*
 * Created by Exyte on 14.10.2021.
 */
@Composable
fun SongListItem(
    modifier: Modifier = Modifier,
    number: Int,
    author: String,
    title: String,
    duration: String,
    isLiked: Boolean,
    onLikeClick: (index: Int) -> Unit = {},
    onContentClick: (index: Int) -> Unit = {},
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth(),
        color = Color.Transparent,
    ) {
        val likedIcon = rememberVectorPainter(image = Icons.Filled.Favorite)
        val notLikedIcon = rememberVectorPainter(image = Icons.Outlined.Favorite)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .clip(shape = RoundedCornerShape(percent = 50))
                .clickable { onContentClick(number) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "$number.",
                style = MaterialTheme.typography.body2,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.alpha(0.75f),
                    text = author,
                    style = MaterialTheme.typography.body2,
                    fontSize = 13.sp,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.body2,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = duration,
                style = MaterialTheme.typography.body2,
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { onLikeClick(number - 1) }) {
                Icon(
                    painter = if (isLiked) likedIcon else notLikedIcon,
                    contentDescription = "",
                    tint = if (isLiked) {
                        MaterialTheme.colors.secondary
                    } else {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                    }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun SongList(
    modifier: Modifier = Modifier,
    items: List<ModelSongInfo>,
    @FloatRange(from = 0.0, to = 1.0) offsetPercent: Float = 1f,
    likedIndices: LikedIndices,
    onLikeClick: (index: Int) -> Unit = {},
) {
    val density = LocalDensity.current
    val scrollState = rememberLazyListState()
    Box(modifier = modifier) {
        LazyColumn(state = scrollState) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            itemsIndexed(items) { index, info ->
                SongListItem(
                    modifier = Modifier
                        .offset {
                            val yOffset = lerp(200.dp * index, 0.dp, offsetPercent)
                            IntOffset(0, yOffset.toPx(density))
                        },
                    number = index + 1,
                    author = info.author,
                    title = info.title,
                    duration = info.duration,
                    isLiked = likedIndices.isLiked(index),
                    onLikeClick = onLikeClick,
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSongList() {
    PlayerTheme(false) {
        SongList(
            modifier = Modifier.background(MaterialTheme.colors.surface),
            items = PlaybackData().albums.first().songs,
            likedIndices = LikedIndices()
        )
    }
}