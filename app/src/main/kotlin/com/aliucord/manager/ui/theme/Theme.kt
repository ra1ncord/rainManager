/*
 * Copyright (c) 2022 Juby210 & zt
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.manager.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.raincord.manager.R

@Composable
fun ManagerTheme(
    theme: Theme = Theme.System,
    dynamicColor: Boolean = true,
    pitchBlack: Boolean = false,
    content: @Composable () -> Unit,
) {
    val dynamicColor = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val darkTheme = theme.isDark()
    val usePitchBlack = darkTheme && pitchBlack
    val baseColorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    val colorScheme = when(usePitchBlack) {
        true -> baseColorScheme.toPitchBlack()
        false -> baseColorScheme
    }
    val customColors = when (darkTheme) {
        true -> DarkCustomColors
        false -> LightCustomColors
    }

    // As usual, Google deprecates accompanist libraries and replaces them with an incomplete and shitty replacement in androidx
    // enableEdgeToEdge() does not work for our use case.
    @Suppress("DEPRECATION")
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.background,
            darkIcons = !darkTheme,
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
        )
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ThemeTypography,
            content = content,
        )
    }
}

enum class Theme {
    System,
    Light,
    Dark;

    @Composable
    fun toDisplayName() = when (this) {
        System -> stringResource(R.string.theme_system)
        Light -> stringResource(R.string.theme_light)
        Dark -> stringResource(R.string.theme_dark)
    }

    @Composable
    fun isDark() = when (this) {
        System -> isSystemInDarkTheme()
        Dark -> true
        Light -> false
    }

    @Composable
    fun toPainter() = when (this) {
        System -> painterResource(R.drawable.ic_sync)
        Light -> painterResource(R.drawable.ic_light)
        Dark -> painterResource(R.drawable.ic_night)
    }
}
private fun ColorScheme.toPitchBlack(): ColorScheme {
    return this.copy(
        background = Color.Black,
        surface = Color.Black,
        surfaceVariant = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )
}
