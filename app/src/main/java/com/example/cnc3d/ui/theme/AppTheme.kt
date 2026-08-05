package com.example.cnc3d.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = when (ThemeManager.current) {
        AppTheme.DARK -> darkColorScheme(
            primary = Color(0xFF00BCD4),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
        AppTheme.CNC_INDUSTRIAL -> darkColorScheme(
            primary = Color(0xFFFFA000),
            background = Color(0xFF0A0A0A),
            surface = Color(0xFF202020)
        )
        AppTheme.PRINTER_FRIENDLY -> darkColorScheme(
            primary = Color(0xFF3F51B5),
            background = Color(0xFFF5F5F5),
            surface = Color(0xFFFFFFFF)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
