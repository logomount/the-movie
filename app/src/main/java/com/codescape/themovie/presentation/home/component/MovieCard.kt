package com.codescape.themovie.presentation.home.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codescape.themovie.domain.model.Movie
import com.codescape.themovie.presentation.modifier.conditional
import com.codescape.themovie.presentation.modifier.sharedTransition
import com.codescape.themovie.presentation.shared.SharedElementKey
import com.codescape.themovie.presentation.shared.SharedElementType
import com.codescape.themovie.presentation.theme.TheMovieTheme

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalAnimationSpecApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun MovieCard(
    modifier: Modifier = Modifier.Companion,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    origin: String = "",
    movie: Movie
) {
    val imageKey = remember { movie.id.toString() }
    val isPreview = LocalInspectionMode.current
    val cornerSize =
        animatedVisibilityScope?.transition?.animateDp(
            label = "corner",
            transitionSpec = {
                tween(durationMillis = 2000, easing = LinearOutSlowInEasing)
            }
        ) { exitEnter ->
            when (exitEnter) {
                EnterExitState.PreEnter -> 24.dp
                EnterExitState.Visible -> 16.dp
                EnterExitState.PostExit -> 24.dp
            }
        }
    Box {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(movie.posterPath)
                    .placeholderMemoryCacheKey(imageKey)
                    .memoryCacheKey(imageKey)
                    .build(),
            contentScale = ContentScale.Companion.Crop,
            contentDescription = "Icon",
            clipToBounds = true,
            modifier =
                modifier
                    .aspectRatio(0.65f)
                    .conditional(
                        condition = isPreview,
                        ifTrue = {
                            background(TheMovieTheme.colors.outline)
                        }
                    ).sharedTransition(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    ) { sharedTransitionScope, animatedVisibilityScope ->
                        with(sharedTransitionScope) {
                            sharedBounds(
                                sharedContentState =
                                    rememberSharedContentState(
                                        key =
                                            SharedElementKey(
                                                id = movie.id,
                                                origin = origin,
                                                type = SharedElementType.POSTER
                                            )
                                    ),
                                animatedVisibilityScope = animatedVisibilityScope,
                                clipInOverlayDuringTransition =
                                    OverlayClip(
                                        RoundedCornerShape(cornerSize?.value ?: 16.dp)
                                    ),
                                boundsTransform = { initialBounds, targetBounds ->
                                    tween(durationMillis = 2000, easing = LinearOutSlowInEasing)
                                },
                                resizeMode = ResizeMode.ScaleToBounds(),
                                enter = EnterTransition.None,
                                exit = ExitTransition.None
                            )
                        }
                    }.clip(MaterialTheme.shapes.medium)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun MovieCardPreview() {
    TheMovieTheme {
        MovieCard(
            modifier =
                Modifier.size(
                    width = 100.dp,
                    height = 100.dp
                ),
            sharedTransitionScope = null,
            animatedVisibilityScope = null,
            movie =
                Movie(
                    id = 1,
                    title = "Title",
                    overview = "Overview",
                    posterPath = "https://www.google.com",
                    backdropPath = "https://www.google.com",
                    releaseDate = "2023-01-01",
                    voteAverage = 1.0,
                    voteCount = 1,
                    popularity = 1.0,
                    adult = false,
                    originalLanguage = "en",
                    originalTitle = "Original Title",
                    video = false,
                    query = emptyMap()
                )
        )
    }
}
