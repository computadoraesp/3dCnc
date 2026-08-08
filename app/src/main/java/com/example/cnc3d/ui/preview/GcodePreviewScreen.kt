package com.example.cnc3d.ui.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cnc3d.app.viewmodels.GcodePreviewViewModel
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.models.GcodeSegment
import com.example.cnc3d.ui.theme._3dCncTheme

@Composable
fun GcodePreviewScreen(vm: GcodePreviewViewModel) {
    val path by vm.path.collectAsState()
    GcodePreviewContent(path = path)
}

@Composable
fun GcodePreviewContent(path: GcodePath?) {
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

@Preview(showBackground = true)
@Composable
fun GcodePreviewPreview() {
    _3dCncTheme {
        GcodePreviewContent(
            path = GcodePath(
                segments = listOf(
                    GcodeSegment(0f, 0f, 0f, 50f, 50f, 0f, rapid = true),
                    GcodeSegment(50f, 50f, 0f, 100f, 50f, 0f, extrude = true),
                    GcodeSegment(100f, 50f, 0f, 100f, 100f, 0f, extrude = true)
                )
            )
        )
    }
}

