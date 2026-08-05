package com.example.cnc3d.ui.theme

enum class AppTheme {
    DARK,
    CNC_INDUSTRIAL,
    PRINTER_FRIENDLY
}

object ThemeManager {
    var current: AppTheme = AppTheme.DARK
}
