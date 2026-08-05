package com.example.cnc3d.domain.models

data class SlicerProfile(
    val id: String,
    val name: String,
    val type: SlicerType,
    val endpoint: String
)

enum class SlicerType {
    CURA,
    PRUSASLICER,
    ORCA,
    SUPERSLICER,
    CNC_INTERNAL
}
