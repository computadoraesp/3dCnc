package com.example.cnc3d.ui.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.app.viewmodels.FileManagerViewModel

@Composable
fun FileManagerScreen(vm: FileManagerViewModel) {

    val files by vm.files.collectAsState()
    val status by vm.status.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Files", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.refresh() }) {
            Text("Refresh")
        }

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")

        Spacer(Modifier.height(16.dp))

        files.forEach { file ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { vm.delete(file) }
                    .padding(8.dp)
            ) {
                Text(file)
            }
        }
    }
}
