package com.example.cnc3d.ui.components

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.ui.MeshRenderer3D
import com.example.cnc3d.ui.preview3d.GcodeRenderer3D

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun ToolpathViewer(gcodePath: GcodePath?, modifier: Modifier = Modifier) {
    var angleX by remember { mutableFloatStateOf(0f) }
    var angleY by remember { mutableFloatStateOf(0f) }
    var zoom by remember { mutableFloatStateOf(1f) }

    val renderer = remember { GcodeRenderer3D { gcodePath } }
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoomAmount, _ ->
                    angleY += pan.x / 5f
                    angleX += pan.y / 5f
                    zoom *= zoomAmount
                    renderer.angleX = angleX
                    renderer.angleY = angleY
                    renderer.zoom = zoom
                }
            },
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(1)
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> onResume()
                        Lifecycle.Event.ON_PAUSE -> onPause()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
            }
        }
    )
}

@Composable
fun MeshViewer(mesh: Mesh?, modifier: Modifier = Modifier) {
    var angleX by remember { mutableFloatStateOf(0f) }
    var angleY by remember { mutableFloatStateOf(0f) }
    var zoom by remember { mutableFloatStateOf(1f) }

    val renderer = remember { MeshRenderer3D { mesh } }
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoomAmount, _ ->
                    angleY += pan.x / 5f
                    angleX += pan.y / 5f
                    zoom *= zoomAmount
                    renderer.angleX = angleX
                    renderer.angleY = angleY
                    renderer.zoom = zoom
                }
            },
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(1)
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> onResume()
                        Lifecycle.Event.ON_PAUSE -> onPause()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
            }
        }
    )
}
