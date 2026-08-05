package com.example.cnc3d.ui.screens.printer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.PrinterViewModel

import java.util.Locale

@Composable
fun PrinterMdiScreen(viewModel: PrinterViewModel) {
    var command by remember { mutableStateOf("") }
    val history by viewModel.mdiHistory.collectAsState()
    val status by viewModel.status.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // DRO Panel at top of MDI
        IndustrialPanel(title = "Machine Coordinates") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IndustrialDro("X", String.format(Locale.US, "%.3f", status.position.first), modifier = Modifier.weight(1f))
                IndustrialDro("Y", String.format(Locale.US, "%.3f", status.position.second), modifier = Modifier.weight(1f))
                IndustrialDro("Z", String.format(Locale.US, "%.3f", status.position.third), modifier = Modifier.weight(1f))
            }
        }

        IndustrialPanel(title = "Machine Status") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("STATE: ${status.state}", color = IndustrialColors.Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("UNITS: MM", color = IndustrialColors.TextSecondary, fontSize = 12.sp)
                Text("MODE: ABS", color = IndustrialColors.TextSecondary, fontSize = 12.sp)
            }
        }
        IndustrialPanel(title = "Console Output", modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color.Black).padding(8.dp),
                reverseLayout = true
            ) {
                items(history.reversed()) { line ->
                    Text(
                        text = "> $line",
                        color = IndustrialColors.Success,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }

        IndustrialPanel(title = "Manual Data Input") {
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
                    text = "Send",
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
                IndustrialButton("Preheat", onClick = { viewModel.sendMdi("M104 S200\nM140 S60") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                IndustrialButton("Cooldown", onClick = { viewModel.sendMdi("M104 S0\nM140 S0") }, modifier = Modifier.weight(1f), containerColor = IndustrialColors.Border)
                IndustrialButton("Clear", onClick = { /* Clear logic */ }, modifier = Modifier.weight(1f), containerColor = IndustrialColors.Emergency)
            }
        }
    }
}
