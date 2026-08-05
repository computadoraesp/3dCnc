package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.CncViewModel

@Composable
fun CncOffsetScreen(viewModel: CncViewModel) {
    val status by viewModel.status.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Work Coordinate System") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("G54", "G55", "G56", "G57", "G58", "G59").forEach { offset ->
                    FilterChip(
                        selected = status.activeOffset == offset,
                        onClick = { viewModel.setWorkOffset(offset) },
                        label = { Text(offset) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndustrialColors.Accent
                        )
                    )
                }
            }
        }

        IndustrialPanel(title = "Axis Offsets") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialButton("Zero X", onClick = { viewModel.zeroAxis("X") }, modifier = Modifier.weight(1f))
                IndustrialButton("Zero Y", onClick = { viewModel.zeroAxis("Y") }, modifier = Modifier.weight(1f))
                IndustrialButton("Zero Z", onClick = { viewModel.zeroAxis("Z") }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            IndustrialButton("Go To Zero", onClick = { viewModel.goToZero() }, modifier = Modifier.fillMaxWidth(), containerColor = IndustrialColors.Warning)
        }

        IndustrialPanel(title = "Offset Table", modifier = Modifier.weight(1f)) {
            // Simplified table using live machine position
            OffsetRow("X", String.format(Locale.US, "%.3f", status.position.first))
            OffsetRow("Y", String.format(Locale.US, "%.3f", status.position.second))
            OffsetRow("Z", String.format(Locale.US, "%.3f", status.position.third))
        }
        
        IndustrialButton("Save Offsets", onClick = { viewModel.saveOffsets() }, modifier = Modifier.fillMaxWidth(), containerColor = IndustrialColors.Success)
    }
}

@Composable
private fun OffsetRow(axis: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(axis, color = IndustrialColors.TextSecondary)
        Text(value, color = IndustrialColors.Accent, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}
