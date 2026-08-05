package com.example.cnc3d.app.ui.preview3d

import android.opengl.GLSurfaceView
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cnc3d.ui.preview3d.GcodeRenderer3D
import com.example.cnc3d.viewmodels.GcodePreview3DViewModel

@Composable
fun GcodePreview3DScreen(vm: GcodePreview3DViewModel) {

    val path by vm.path.collectAsState()

    var angleX by remember { mutableStateOf(0f) }
    var angleY by remember { mutableStateOf(0f) }
    var zoom by remember { mutableStateOf(1f) }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gestureZoom, rotation ->
                    angleX += pan.y * 0.5f
                    angleY += pan.x * 0.5f
                    zoom *= gestureZoom
                }
            },
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(
                    GcodeRenderer3D { path }
                        .apply {
                            this.angleX = angleX
                            this.angleY = angleY
                            this.zoom = zoom
                        }
                )
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        }
    )
}
