package com.example.cnc3d.ui.navigation


import kotlinx.serialization.Serializable

/**
 * Defines all possible navigation destinations within the application.
 *
 * Each route is marked as @Serializable to support Jetpack Compose Navigation's
 * type-safe routing.
 */
@Serializable
sealed interface Routes {
    /** Main dashboard screen. */
    @Serializable data object Home : Routes
    /** Root screen for CNC machine control. */
    @Serializable data class CNC(val subScreen: CncSubScreen = CncSubScreen.RUN) : Routes
    /** Root screen for 3D Printer control. */
    @Serializable data class Printer(val subScreen: PrinterSubScreen = PrinterSubScreen.RUN) : Routes
    /** Camera feed screen. */
    @Serializable data object Camera : Routes
    /** Timelapse management screen. */
    @Serializable data object Timelapse : Routes
    /** Bed mesh calibration and tools. */
    @Serializable data object Mesh : Routes
    /** G-code or machine state analyzer. */
    @Serializable data object Analyzer : Routes
    /** Cloud synchronization settings and status. */
    @Serializable data object Cloud : Routes
    /** General application settings. */
    @Serializable data object Settings : Routes
}

/**
 * Sub-screens available within the CNC control section.
 */
@Serializable
enum class CncSubScreen {
    RUN, MDI, OFFSETS, TOOLS, DIAGNOSTICS, CONFIG, RENDERER
}

/**
 * Sub-screens available within the 3D Printer control section.
 */
@Serializable
enum class PrinterSubScreen {
    RUN, MDI, Z_OFFSET, MESH, FILAMENT, CONFIG, RENDERER
}
