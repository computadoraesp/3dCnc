package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.CncViewModel

@Composable
fun CncMdiScreen(viewModel: CncViewModel) {
    var command by remember { mutableStateOf("") }
    val status by viewModel.status.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // DRO Panel
        IndustrialPanel(title = "Coordinates") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IndustrialDro("X", String.format(Locale.US, "%.3f", status.position.first), modifier = Modifier.weight(1f))
                IndustrialDro("Y", String.format(Locale.US, "%.3f", status.position.second), modifier = Modifier.weight(1f))
                IndustrialDro("Z", String.format(Locale.US, "%.3f", status.position.third), modifier = Modifier.weight(1f))
            }
        }

        IndustrialPanel(title = "MDI Console", modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color.Black).padding(8.dp),
                reverseLayout = true
            ) {
                items(status.mdiHistory.reversed()) { line ->
                    Text(
                        text = "> $line",
                        color = IndustrialColors.Success,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }

        IndustrialPanel(title = "Manual Input") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndustrialColors.Accent,
                        unfocusedBorderColor = IndustrialColors.Border
                    ),
                    placeholder = { Text("Enter G-Code...", color = IndustrialColors.TextSecondary) }
                )
                IndustrialButton(
                    text = "Execute",
                    onClick = {
                        if (command.isNotBlank()) {
                            viewModel.sendMdi(command)
                            command = ""
                        }
                    }
                )
            }
        }

        IndustrialPanel(title = "Quick Macros") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialButton("Home All", onClick = { viewModel.sendMdi("G28") }, modifier = Modifier.weight(1f))
                IndustrialButton("Probe Z", onClick = { viewModel.sendMdi("G38.2 Z-50 F100") }, modifier = Modifier.weight(1f))
                IndustrialButton("Go Zero", onClick = { viewModel.goToZero() }, modifier = Modifier.weight(1f))
            }
        }
    }
}
