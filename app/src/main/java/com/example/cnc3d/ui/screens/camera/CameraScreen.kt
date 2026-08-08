package com.example.cnc3d.ui.screens.camera

import android.graphics.BitmapFactory
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.cnc3d.domain.models.CameraInfo
import com.example.cnc3d.ui.theme.IndustrialButton
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialPanel
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.CameraViewModel

@Composable
fun CameraScreen(
    navController: NavHostController? = null,
    vm: CameraViewModel? = null
) {
    if (vm == null) {
        CameraEmptyState()
        return
    }

    val cameras by vm.cameras.collectAsState()
    val snapshot by vm.snapshot.collectAsState()

    CameraContent(
        cameras = cameras,
        snapshot = snapshot,
        onLoad = { vm.load() },
        onTakeSnapshot = { vm.takeSnapshot(it) }
    )
}

@Composable
private fun CameraEmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text(
            "NO CAMERA HARDWARE DETECTED",
            color = IndustrialColors.Error,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CameraContent(
    cameras: List<CameraInfo>,
    snapshot: ByteArray?,
    onLoad: () -> Unit,
    onTakeSnapshot: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Hardware Monitoring") {
            IndustrialButton(
                text = "Discover Feed",
                onClick = onLoad,
                modifier = Modifier.fillMaxWidth()
            )
        }

        cameras.forEach { cam ->
            IndustrialPanel(title = cam.name) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.Black),
                    factory = { context ->
                        VideoView(context).apply {
                            setVideoPath(cam.streamUrl)
                            start()
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                IndustrialButton(
                    text = "Capture Frame",
                    onClick = { onTakeSnapshot(cam.snapshotUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = IndustrialColors.Border
                )
            }
        }

        snapshot?.let { bytes ->
            IndustrialPanel(title = "Last Snapshot") {
                Image(
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraPreview() {
    _3dCncTheme {
        CameraContent(
            cameras = listOf(
                CameraInfo(
                    "Nozzle Cam",
                    "http://192.168.1.50/stream",
                    "http://192.168.1.50/snapshot"
                ),
                CameraInfo("Room Cam", "http://192.168.1.60/stream", "http://192.168.1.60/snapshot")
            ),
            snapshot = null,
            onLoad = {},
            onTakeSnapshot = {}
        )
    }
}
