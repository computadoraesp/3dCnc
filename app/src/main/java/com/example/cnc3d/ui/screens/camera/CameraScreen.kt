package com.example.cnc3d.ui.screens.camera

import android.graphics.BitmapFactory
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.cnc3d.viewmodels.CameraViewModel

@Composable
fun CameraScreen(
    navController: NavHostController? = null,
    vm: CameraViewModel? = null
) {
    if (vm == null) {
        Text("Camera view unavailable", style = MaterialTheme.typography.bodyLarge)
        return
    }

    val cameras by vm.cameras.collectAsState()
    val snapshot by vm.snapshot.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Camera", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.load() }) {
            Text("Load Cameras")
        }

        Spacer(Modifier.height(16.dp))

        cameras.forEach { cam ->
            Text(cam.name)

            Spacer(Modifier.height(8.dp))

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                factory = { context ->
                    VideoView(context).apply {
                        setVideoPath(cam.streamUrl)
                        start()
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = { vm.takeSnapshot(cam.snapshotUrl) }) {
                Text("Snapshot")
            }

            Spacer(Modifier.height(16.dp))
        }

        snapshot?.let { bytes ->
            Image(
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
