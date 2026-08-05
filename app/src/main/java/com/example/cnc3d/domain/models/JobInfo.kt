package com.example.cnc3d.domain.models

data class JobInfo(
    val name: String,
    val sizeBytes: Long,
    val uploaded: Boolean,
    val started: Boolean
)
