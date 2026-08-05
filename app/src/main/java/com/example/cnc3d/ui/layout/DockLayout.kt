package com.example.cnc3d.ui.layout

data class PanelConfig(
    val id: String,
    val position: PanelPosition
)

enum class PanelPosition {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    FLOATING
}

data class LayoutConfig(
    val panels: List<PanelConfig>
)
