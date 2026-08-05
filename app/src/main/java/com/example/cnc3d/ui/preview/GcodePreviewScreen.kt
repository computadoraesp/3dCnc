package com.example.cnc3d.ui.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cnc3d.app.viewmodels.GcodePreviewViewModel

@Composable
fun GcodePreviewScreen(vm: GcodePreviewViewModel) {

    val path by vm.path.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("G-code Preview", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Box(Modifier.fillMaxSize()) {

            Canvas(Modifier.fillMaxSize()) {

                path?.segments?.forEach { seg ->

                    val start = Offset(seg.x1, seg.y1)
                    val end = Offset(seg.x2, seg.y2)

                    drawLine(
                        color = when {
                            seg.rapid -> Color.Gray
                            seg.extrude -> Color.Red
                            else -> Color.Blue
                        },
                        start = start,
                        end = end,
                        strokeWidth = 2f
                    )
                }
            }
        }
    }
}

