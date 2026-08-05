package com.example.cnc3d.core.api

interface SlicerApi {
    suspend fun sliceCura(endpoint: String, stlBytes: ByteArray): ByteArray?
    suspend fun slicePrusa(endpoint: String, stlBytes: ByteArray): ByteArray?
    suspend fun sliceOrca(endpoint: String, stlBytes: ByteArray): ByteArray?
    suspend fun sliceSuper(endpoint: String, stlBytes: ByteArray): ByteArray?
}
