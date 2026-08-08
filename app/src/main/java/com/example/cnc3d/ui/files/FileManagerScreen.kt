package com.example.cnc3d.ui.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cnc3d.app.viewmodels.FileManagerViewModel
import com.example.cnc3d.ui.theme._3dCncTheme

@Composable
fun FileManagerScreen(vm: FileManagerViewModel) {
    val files by vm.files.collectAsState()
    val status by vm.status.collectAsState()
    FileManagerContent(
        files = files,
        status = status,
        onRefresh = { vm.refresh() },
        onDelete = { vm.delete(it) }
    )
}

@Composable
fun FileManagerContent(
    files: List<String>,
    status: String,
    onRefresh: () -> Unit,
    onDelete: (String) -> Unit
) {
    Column(Modifier.padding(16.dp)) {

        Text("Files", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(onClick = onRefresh) {
            Text("Refresh")
        }

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")

        Spacer(Modifier.height(16.dp))

        files.forEach { file ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onDelete(file) }
                    .padding(8.dp)
            ) {
                Text(file)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FileManagerPreview() {
    _3dCncTheme {
        FileManagerContent(
            files = listOf("part1.gcode", "mount_bracket.nc", "config.yaml"),
            status = "Ready",
            onRefresh = {},
            onDelete = {}
        )
    }
}
