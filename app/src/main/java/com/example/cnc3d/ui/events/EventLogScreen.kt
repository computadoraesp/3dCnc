package com.example.cnc3d.ui.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.viewmodels.EventViewModel

@Composable
fun EventLogScreen(vm: EventViewModel) {

    val events by vm.history.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Event Log", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        events.sortedByDescending { it.timestamp }.forEach { e ->
            Text("${e.type}: ${e.message}")
            Spacer(Modifier.height(8.dp))
        }
    }
}
