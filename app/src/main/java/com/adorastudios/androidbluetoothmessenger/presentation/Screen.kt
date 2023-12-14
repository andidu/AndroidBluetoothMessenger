package com.adorastudios.androidbluetoothmessenger.presentation

sealed class Screen(val route: String) {
    object DiscoverScreen : Screen("discover_screen")
}
