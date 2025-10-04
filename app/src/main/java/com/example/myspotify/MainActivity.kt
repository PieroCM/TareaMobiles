package dev.lchang.appue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.lchang.appue.presentation.navigation.AppNavHost
import dev.lchang.appue.ui.theme.MySpotifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySpotifyTheme(darkTheme = true, dynamicColor = false) {
                AppNavHost()
            }
        }
    }
}
