@file:Suppress("FunctionName")

package dev.lchang.appue.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import dev.lchang.appue.data.model.FakeData
import dev.lchang.appue.data.model.Playlist
import dev.lchang.appue.ui.theme.DarkSurfaceVariant
import dev.lchang.appue.ui.theme.MySpotifyTheme

@Composable
fun HomeScreen(onPlaylistClick: (String) -> Unit) {
    // Datos (separar locales de remotos para mostrar ambos)
    val allPlaylists = remember { FakeData.playlists }
    val (localPlaylists, remotePlaylists) = allPlaylists.partition { it.coverResId != null }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Featured (horizontal) ---
        item {
            Text(
                text = "Featured Playlists",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                fontWeight = FontWeight.Bold
            )
        }
        item {
            PlaylistsCarousel(
                playlists = remotePlaylists,
                onClick = onPlaylistClick
            )
        }

        // --- Your Library (vertical) ---
        item {
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }
        items(
            items = localPlaylists,
            key = { it.id } // ✅ clave estable
        ) { playlist ->
            PlaylistListItem(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist.id) }
            )
        }
    }
}

@Composable
private fun PlaylistsCarousel(
    playlists: List<Playlist>,
    onClick: (String) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.9f

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = playlists,
            key = { it.id } // ✅ clave estable
        ) { playlist ->
            PlaylistCard(
                playlist = playlist,
                width = cardWidth,
                onClick = { onClick(playlist.id) }
            )
        }
    }
}

@Composable
private fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            model = playlist.coverResId ?: playlist.coverUrl,
            contentDescription = "Cover ${playlist.title}",
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(playlist.title.take(1), fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    SubcomposeAsyncImageContent(contentScale = ContentScale.Crop)
                }
            }
        }

        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                playlist.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                playlist.curator,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PlaylistCard(
    playlist: Playlist,
    width: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
    ) {
        SubcomposeAsyncImage(
            model = playlist.coverResId ?: playlist.coverUrl,
            contentDescription = "Cover ${playlist.title}",
            modifier = Modifier.fillMaxSize()
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color.DarkGray, Color.Gray)
                                )
                            )
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF333333), Color(0xFF111111))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(playlist.title.take(2), fontSize = 42.sp, fontWeight = FontWeight.Black)
                    }
                }
                else -> {
                    SubcomposeAsyncImageContent(contentScale = ContentScale.Crop)
                }
            }
        }

        // Overlay para legibilidad
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA000000))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                playlist.title,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                if (playlist.description.isNotEmpty()) playlist.description else playlist.curator,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFDDDDDD),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// --- Preview opcional ---
@Composable
private fun HomePreviewContent() {
    MySpotifyTheme(darkTheme = true) {
        Surface(color = DarkSurfaceVariant) {
            HomeScreen(onPlaylistClick = {})
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun HomePreview() { HomePreviewContent() }
