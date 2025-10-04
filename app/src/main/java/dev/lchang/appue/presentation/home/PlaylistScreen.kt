@file:OptIn(ExperimentalMaterial3Api::class)
package dev.lchang.appue.presentation.home

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.lchang.appue.data.model.FakeData
import dev.lchang.appue.data.model.Playlist
import dev.lchang.appue.data.model.Track
import dev.lchang.appue.ui.theme.MySpotifyTheme
import dev.lchang.appue.ui.theme.PlayGreen
import dev.lchang.appue.ui.theme.TextSecondary
import kotlin.time.Duration

@Composable
fun PlaylistScreen(playlistId: String, onBack: () -> Unit) {
    val playlist = remember(playlistId) { FakeData.getPlaylist(playlistId) }
    if (playlist == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Playlist not found")
        }
        return
    }

    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val onTrackClick = { track: Track ->
        mediaPlayer?.release() // Release previous player
        track.musicResId?.let { musicRes ->
            mediaPlayer = MediaPlayer.create(context, musicRes).apply {
                start()
            }
        }
    }

    // Release player on exit
    DisposableEffect(playlistId) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val headerHeight = 340.dp
    val toolbarExpandedHeightPx = with(LocalDensity.current) { headerHeight.toPx() }

    val scrolledRatio by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset.toFloat()
            (offset / (toolbarExpandedHeightPx * 0.6f)).coerceIn(0f, 1f)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PlaylistTopBar(
                onBack = onBack,
                title = playlist.title,
                scrolledRatio = scrolledRatio
            )
        }
    ) { inner ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 0.dp, bottom = inner.calculateBottomPadding() + 24.dp)
        ) {
            item {
                PlaylistHeader(
                    playlist = playlist,
                    height = headerHeight,
                    onPlay = {
                        playlist.tracks.firstOrNull()?.let(onTrackClick)
                    }
                )
            }
            itemsIndexed(playlist.tracks) { index, track ->
                TrackRow(index = index, track = track, onClick = { onTrackClick(track) })
                HorizontalDivider(color = Color(0xFF1F1F1F), thickness = 1.dp, modifier = Modifier.padding(start = 72.dp))
            }
        }
    }
}

@Composable
private fun PlaylistTopBar(onBack: () -> Unit, title: String, scrolledRatio: Float) {
    val showTitle = scrolledRatio > 0.6f
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = {
            AnimatedVisibility(visible = showTitle) {
                Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = scrolledRatio * 0.9f),
            scrolledContainerColor = Color.Black
        )
    )
}

@Composable
fun PlaylistHeader(playlist: Playlist, height: Dp, onPlay: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (playlist.coverResId != null) {
            Image(
                painter = painterResource(id = playlist.coverResId),
                contentDescription = "Cover ${playlist.title}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (playlist.coverUrl != null) {
            AsyncImage(
                model = playlist.coverUrl,
                contentDescription = "Cover ${playlist.title}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF444444), Color(0xFF111111))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(playlist.title.take(2), fontSize = 48.sp, fontWeight = FontWeight.Black)
            }
        }
        GradientOverlay()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                playlist.title,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 38.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            val durationMinutes = playlist.totalDuration.inWholeMinutes
            Text(
                "${playlist.curator}  •  ${playlist.saves.toHuman()} saves  •  ${durationMinutes} min",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(16.dp))
            ActionRow()
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                PlayButton(onClick = onPlay)
            }
        }
    }
}

@Composable
fun GradientOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xCC000000))
                )
            )
    )
}

@Composable
private fun ActionRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) { Icon(Icons.Default.Download, contentDescription = "Download") }
        IconButton(onClick = { }) { Icon(Icons.Default.FavoriteBorder, contentDescription = "Like") }
        IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, contentDescription = "More options") }
    }
}

@Composable
private fun PlayButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = PlayGreen)
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
    }
}

@Composable
fun TrackRow(index: Int, track: Track, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${index + 1}", modifier = Modifier.width(32.dp), color = TextSecondary)
        // Mini cover placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.small)
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            Text(track.title.take(1), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(track.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artists, color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(12.dp))
        Text(track.duration.format(), color = TextSecondary, fontSize = 12.sp)
        IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, contentDescription = "More for ${track.title}") }
    }
}

private fun Duration.format(): String {
    val minutes = inWholeMinutes
    val seconds = (inWholeSeconds % 60).toInt()
    return "%d:%02d".format(minutes, seconds)
}

private fun Long.toHuman(): String {
    if (this < 1_000) return toString()
    if (this < 1_000_000) return "${this / 1_000}K"
    return "${this / 1_000_000}M"
}

@Preview
@Composable
private fun PlaylistPreview() {
    MySpotifyTheme(darkTheme = true) {
        PlaylistScreen(playlistId = FakeData.playlistDejaVu.id, onBack = {})
    }
}
