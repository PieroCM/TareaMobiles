package dev.lchang.appue.presentation.navigation

object AppDestinations {
    const val HOME = "home"
    const val SEARCH = "search"
    const val LIBRARY = "library"
    const val PLAYLIST = "playlist"
    const val PLAYLIST_ID_ARG = "playlistId"
    const val PLAYLIST_ROUTE = "playlist/{$PLAYLIST_ID_ARG}"

    fun playlistRoute(id: String) = "$PLAYLIST/$id"
}

