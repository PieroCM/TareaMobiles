package dev.lchang.appue.data.model

import dev.lchang.appue.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class Track(
    val id: String,
    val title: String,
    val artists: String,
    val duration: Duration,
    val coverUrl: String? = null,
    val musicResId: Int? = null // Para archivos MP3 locales
)

data class Playlist(
    val id: String,
    val title: String,
    val curator: String,
    val saves: Long,
    val description: String = "",
    val coverUrl: String? = null,
    val coverResId: Int? = null, // Para imágenes locales
    val tracks: List<Track>
) {
    val totalDuration: Duration = tracks.fold(Duration.ZERO) { acc, t -> acc + t.duration }
}

object FakeData {
    // constructor para tracks remotos
    private fun t(id: String, title: String, artists: String, m: Int, s: Int) = Track(
        id = id,
        title = title,
        artists = artists,
        duration = (m.minutes + s.seconds)
    )

    // constructor para tracks locales con recursos
    private fun tLocal(id: String, title: String, artists: String, m: Int, s: Int, musicResId: Int) = Track(
        id = id,
        title = title,
        artists = artists,
        duration = (m.minutes + s.seconds),
        musicResId = musicResId
    )

    // Playlists con datos locales
    val playlistDejaVu = Playlist(
        id = "deja_vu_playlist",
        title = "Deja Vu",
        curator = "Takumi Fujiwara",
        saves = 1986,
        description = "Initial D Eurobeat",
        coverResId = R.drawable.deja_vu,
        tracks = listOf(
            tLocal("deja_vu_track", "DEJA VU", "DAVE RODGERS", 4, 22, R.raw.dejabu_music)
        )
    )

    val playlistGasGasGas = Playlist(
        id = "gas_gas_gas_playlist",
        title = "Gas Gas Gas",
        curator = "Manuel",
        saves = 2000,
        description = "I'm gonna step on the gas!",
        coverResId = R.drawable.gas_gas_gas,
        tracks = listOf(
            tLocal("gas_gas_gas_track", "GAS GAS GAS", "MANUEL", 3, 21, R.raw.gas_gas_gas)
        )
    )

    val playlistTopHits = Playlist(
        id = "todays_top_hits",
        title = "Today's Top Hits",
        curator = "Spotify",
        saves = 55242100,
        coverUrl = "https://i.scdn.co/image/ab67706f00000003b0c6f2283038686604245f06",
        description = "The hottest 60 Cover: Sabrina Carpenter",
        tracks = listOf(
            t("busy_woman", "Busy Woman", "Sabrina Carpenter", 3, 5),
            t("die_with_smile", "Die With A Smile", "Lady Gaga, Bruno Mars", 4, 2),
            t("luther", "luther (with sza)", "Kendrick Lamar, SZA", 3, 45),
            t("messy", "Messy", "Lolo Young", 2, 58),
            t("midnight_city", "Midnight City", "M83", 4, 18),
            t("sunset_drive", "Sunset Drive", "Neon Vibes", 3, 12),
            t("golden_hour", "Golden Hour", "JVKE", 3, 29),
            t("ocean_eyes", "Ocean Eyes", "Billie Eilish", 3, 15)
        )
    )

    val playlistVivaLatino = Playlist(
        id = "viva_latino",
        title = "Viva Latino",
        curator = "Spotify",
        saves = 169693200,
        coverUrl = "https://i.scdn.co/image/ab67706f000000037344c26447594943f59043c9",
        description = "Top Latin hits elevando nuestra música.",
        tracks = listOf(
            t("morena", "Morena", "Peso Pluma", 3, 22),
            t("7_dias", "7 Días", "El Caballo Dorado", 2, 49),
            t("bailenolvidable", "BAILE INeOlVIDABLE", "Bad Bunny", 3, 55),
            t("khe", "Khe?", "Nuevo Alejandro, Romeo Santos", 3, 31),
            t("calor", "Calor", "Karol G", 3, 11),
            t("fuego", "Fuego", "Rosalía", 2, 59),
            t("sabor", "Sabor", "J Balvin", 3, 9),
            t("noches", "Noches", "Ozuna", 2, 54)
        )
    )

    val playlistRapCaviar = Playlist(
        id = "rapcaviar",
        title = "RapCaviar",
        curator = "Spotify",
        saves = 153328300,
        coverUrl = "https://i.scdn.co/image/ab67706f00000003c535444e21b03831ed7324ab",
        description = "New music from Drake, Offset and Young Nudy.",
        tracks = listOf(
            t("gimme_a_hug", "GIMME A HUG", "Drake", 3, 36),
            t("tv_off", "TV off (feat. leafy gunplay)", "Kendrick Lamar, Leafy Gunplay", 4, 1),
            t("dumb_dumb", "Dum, Dumb, and Dumber (with Young Future)", "Drake, Young Future", 3, 57),
            t("brian", "BRIAN STEELE", "El Drake", 2, 52),
            t("storm", "Storm", "Lil Vibes", 3, 15),
            t("echo", "Echo", "Offset", 2, 49),
            t("rhythm", "Rhythm", "Young Nudy", 3, 5),
            t("night_flow", "Night Flow", "Metro Boomin", 2, 59)
        )
    )

    val playlists = listOf(playlistDejaVu, playlistGasGasGas, playlistTopHits, playlistVivaLatino, playlistRapCaviar)

    fun getPlaylist(id: String): Playlist? = playlists.find { it.id == id }
}
