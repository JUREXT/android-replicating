package com.example.composesample.mainplayer

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.composesample.NowPlayingSong
import com.example.composesample.mainplayer.movingupsongpanel.MovingUpSongPanel
import com.example.composesample.mainplayer.movingupsongpanel.PlayerControlContainer
import com.example.composesample.mainplayer.movingupsongpanel.SongInfoContainer
import com.example.composesample.navigationBarsPaddingWithOffset
import com.example.composesample.states.PlayerScreenState
import com.example.composesample.states.Screen
import com.example.composesample.toDp
import com.example.composesample.toPx
import com.example.composesample.ui.DraggableButton
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.math.abs

/*
 * Created by Exyte on 18.07.2022.
 */

@Composable
fun MainPlayerScreen(
    screenState: PlayerScreenState,
) {
    val density = LocalDensity.current
    val animScope = rememberCoroutineScope()
    val draggableButtonSize = DpSize(width = 64.dp, height = 48.dp)

    fun animateOffset(initialValue: Float, targetValue: Float, onEnd: () -> Unit) {
        val distance = abs(targetValue - initialValue)
        val distancePercent = distance / screenState.maxContentWidth
        val duration = (250 * distancePercent).toInt()

        animScope.launch {
            animate(
                initialValue = initialValue,
                targetValue = targetValue,
                animationSpec = tween(duration),
            ) { value, _ -> screenState.currentDragOffset = value }
            onEnd()
        }
    }

    fun calculateNewOffset(dragAmount: Float) {
        val newOffset = minOf(
            screenState.currentDragOffset + dragAmount,
            (screenState.maxContentWidth - draggableButtonSize.width.toPx(density)).toFloat()
        )
        if (newOffset >= 0) {
            screenState.currentDragOffset = newOffset
        }
    }

    fun transitionToComments() {
        animScope.coroutineContext.cancelChildren()
        screenState.currentScreen = Screen.TransitionToComments
    }

    fun expand() {
        animateOffset(
            initialValue = screenState.currentDragOffset,
            targetValue = screenState.maxContentWidth.toFloat()
        ) {
            screenState.currentScreen = Screen.Comments
        }
    }

    fun collapse() {
        animateOffset(
            initialValue = screenState.currentDragOffset,
            targetValue = 0f
        ) {
            screenState.currentScreen = Screen.Player
        }
    }

    fun completeAnimation(currentDragOffset: Float) {
        val shouldExpand =
            currentDragOffset > screenState.maxContentWidth / 2f
        if (shouldExpand) {
            expand()
        } else {
            collapse()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MovingUpSongPanel(
            modifier = Modifier,
            screenState = screenState,
        ) {
            SongInfoContainer(
                height = screenState.songInfoContentHeight.toDp()
            )
            PlayerControlContainer(
                nowPlayingSong = NowPlayingSong(),
            )
        }
        DraggableButton(
            modifier = Modifier
                .navigationBarsPaddingWithOffset(8.dp)
                .align(Alignment.BottomStart)
                .offset(x = screenState.currentDragOffset.toDp())
                .size(draggableButtonSize),
            onClick = {
                transitionToComments()
                expand()
            },
            onOffsetChange = { dragAmount -> calculateNewOffset(dragAmount) },
            onDragStart = { transitionToComments() },
            onDragFinished = { completeAnimation(screenState.currentDragOffset) }
        )
    }
}