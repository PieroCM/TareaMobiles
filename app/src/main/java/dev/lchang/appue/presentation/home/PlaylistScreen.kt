@file:OptIn(ExperimentalMaterial3Api::class)
package dev.lchang.appue.presentation.home

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun PlaylistScreen(
    playlistId: String,
    onBack: () -> Unit,
    onHomeClick: () -> Unit = {} // Nuevo parámetro para navegar a Home
) {
    val playlist = remember(playlistId) { FakeData.getPlaylist(playlistId) }
    if (playlist == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Playlist not found")
        }
        return
    }

    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Nuevos estados para controlar play/pause y la canción actual
    var isPlaying by remember { mutableStateOf(false) }
    var currentTrackId by remember { mutableStateOf<String?>(null) }

    // Control de estado de reproducción
    val onTrackClick = { track: Track ->
        if (currentTrackId == track.id && isPlaying) {
            // Pausa la canción actual
            mediaPlayer?.pause()
            isPlaying = false
        } else if (currentTrackId == track.id) {
            // Reanuda la canción actual
            mediaPlayer?.start()
            isPlaying = true
        } else {
            // Reproduce una nueva canción
            mediaPlayer?.release() // Release previous player
            currentTrackId = track.id
            track.musicResId?.let { musicRes ->
                mediaPlayer = MediaPlayer.create(context, musicRes).apply {
                    start()
                }
                isPlaying = true
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
                onHomeClick = onHomeClick, // Pasando el callback para el botón Home
                title = playlist.title,
                scrolledRatio = scrolledRatio
            )
        }
    ) { inner ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top = 0.dp,
                bottom = inner.calculateBottomPadding() + 24.dp
            ),
            modifier = Modifier.fillMaxSize() // Asegurarse de que la lista llene toda la pantalla
        ) {
            item {
                PlaylistHeader(
                    playlist = playlist,
                    height = headerHeight,
                    isPlaying = isPlaying && currentTrackId == playlist.tracks.firstOrNull()?.id,
                    onPlay = {
                        playlist.tracks.firstOrNull()?.let(onTrackClick)
                    }
                )
            }

            // Añadir un espacio visible entre el header y las canciones para mejor separación
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Canciones",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Lista de canciones mejorada para mejor desplazamiento
            itemsIndexed(playlist.tracks) { index, track ->
                TrackRow(
                    index = index,
                    track = track,
                    isPlaying = isPlaying && currentTrackId == track.id,
                    onClick = { onTrackClick(track) }
                )
                HorizontalDivider(
                    color = Color(0xFF1F1F1F),
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 72.dp)
                )
            }

            // Espacio adicional al final para mejor desplazamiento
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun PlaylistTopBar(onBack: () -> Unit, onHomeClick: () -> Unit, title: String, scrolledRatio: Float) {
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
        actions = {
            IconButton(onClick = onHomeClick) {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = scrolledRatio * 0.9f),
            scrolledContainerColor = Color.Black
        )
    )
}

@Composable
fun PlaylistHeader(playlist: Playlist, height: Dp, onPlay: () -> Unit, isPlaying: Boolean) {
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
                PlayButton(onClick = onPlay, isPlaying = isPlaying)
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
private fun PlayButton(onClick: () -> Unit, isPlaying: Boolean) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = PlayGreen)
    ) {
        // Icon cambia según el estado de reproducción
        val icon: ImageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
        Icon(icon, contentDescription = if (isPlaying) "Pause" else "Play", tint = Color.Black)
    }
}

@Composable
fun TrackRow(index: Int, track: Track, onClick: () -> Unit, isPlaying: Boolean) {
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
        // Corazón para "me gusta", cambia de color si la canción está en reproducción
        val tintColor by animateFloatAsState(
            targetValue = if (isPlaying) 1f else 0.5f,
            animationSpec = tween(durationMillis = 300)
        )
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Like ${track.title}",
                tint = Color.Red.copy(alpha = tintColor)
            )
        }
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
