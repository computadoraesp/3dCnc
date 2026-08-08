package com.example.cnc3d.app.ui.preview3d

import android.opengl.GLSurfaceView
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.ui.preview3d.GcodeRenderer3D
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.GcodePreview3DViewModel

@Composable
fun GcodePreview3DScreen(vm: GcodePreview3DViewModel) {
    val path by vm.path.collectAsState()
    GcodePreview3DContent(path = path)
}

@Composable
fun GcodePreview3DContent(path: GcodePath?) {
    var angleX by remember { mutableStateOf(0f) }
    var angleY by remember { mutableStateOf(0f) }
    var zoom by remember { mutableStateOf(1f) }

    val renderer = remember { GcodeRenderer3D { path } }

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
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        },
        update = {
            renderer.angleX = angleX
            renderer.angleY = angleY
            renderer.zoom = zoom
        }
    )
}

// Minimal renderer mock or just use the real one if it works.
// Note: GLSurfaceView might not render in all Preview environments.

@Preview(showBackground = true)
@Composable
fun GcodePreview3DPreview() {
    _3dCncTheme {
        GcodePreview3DContent(path = null)
    }
}
