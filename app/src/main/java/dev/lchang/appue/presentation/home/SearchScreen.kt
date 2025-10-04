package dev.lchang.appue.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.lchang.appue.ui.theme.MySpotifyTheme

@Composable
fun SearchScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Search Placeholder")
    }
}

@Preview
@Composable
private fun SearchPreview() { MySpotifyTheme(darkTheme = true) { SearchScreen() } }

