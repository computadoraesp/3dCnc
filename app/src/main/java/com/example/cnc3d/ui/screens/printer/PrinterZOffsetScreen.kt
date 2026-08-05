package com.example.cnc3d.ui.screens.printer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.PrinterViewModel

import java.util.Locale

@Composable
fun PrinterZOffsetScreen(viewModel: PrinterViewModel) {
    val currentOffset by viewModel.zOffset.collectAsState()

    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vertical Slider for coarse adjustment
        IndustrialPanel(title = "Coarse Adj", modifier = Modifier.fillMaxHeight().width(80.dp)) {
            IndustrialVerticalSlider(
                value = currentOffset,
                onValueChange = { viewModel.adjustZOffset(it - currentOffset) },
                valueRange = -5f..5f,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IndustrialPanel(title = "Live Calibration") {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("CURRENT Z-OFFSET", color = IndustrialColors.TextSecondary, fontSize = 12.sp)
                    Text(
                        text = String.format(Locale.US, "%.3f mm", currentOffset),
                        color = IndustrialColors.Accent,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            IndustrialPanel(title = "Fine Adjustment") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OffsetButton("+0.100", 0.1f) { viewModel.adjustZOffset(it) }
                    OffsetButton("+0.050", 0.05f) { viewModel.adjustZOffset(it) }
                    OffsetButton("+0.010", 0.01f) { viewModel.adjustZOffset(it) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    OffsetButton("-0.100", -0.1f) { viewModel.adjustZOffset(it) }
                    OffsetButton("-0.050", -0.05f) { viewModel.adjustZOffset(it) }
                    OffsetButton("-0.010", -0.01f) { viewModel.adjustZOffset(it) }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IndustrialButton(
                    text = "Probe Test",
                    onClick = { viewModel.probeTest() },
                    modifier = Modifier.weight(1f),
                    containerColor = IndustrialColors.Warning
                )
                IndustrialButton(
                    text = "Save to Config",
                    onClick = { viewModel.saveZOffset() },
                    modifier = Modifier.weight(1f),
                    containerColor = IndustrialColors.Success
                )
            }
        }
    }
}

@Composable
private fun RowScope.OffsetButton(label: String, delta: Float, onClick: (Float) -> Unit) {
    IndustrialButton(
        text = label,
        onClick = { onClick(delta) },
        modifier = Modifier.weight(1f),
        containerColor = IndustrialColors.Border
    )
}
