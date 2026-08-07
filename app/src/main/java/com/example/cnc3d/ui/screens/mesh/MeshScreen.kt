package com.example.cnc3d.ui.screens.mesh

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cnc3d.ui.theme.IndustrialButton
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialInfoPanel
import com.example.cnc3d.ui.theme.IndustrialPanel

@Composable
fun MeshScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Surface Analysis") {
            IndustrialInfoPanel(
                title = "Calibration Profile",
                info = mapOf(
                    "Active Profile" to "mesh_2026_08_02.json",
                    "Resolution" to "7x7 Points",
                    "Deviation" to "0.12mm Max",
                    "Method" to "BLTouch Probe"
                )
            )
        }

        IndustrialPanel(title = "Mesh Operations") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialButton(
                    text = "Probe Surface",
                    onClick = { /* Trigger probe */ },
                    modifier = Modifier.weight(1f),
                    containerColor = IndustrialColors.Success
                )
                IndustrialButton(
                    text = "Clear Mesh",
                    onClick = { /* Clear logic */ },
                    modifier = Modifier.weight(1f),
                    containerColor = IndustrialColors.Emergency
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialButton(
                    text = "Z-Wizard",
                    onClick = { /* Start wizard */ },
                    modifier = Modifier.weight(1f)
                )
                IndustrialButton(
                    text = "Self Test",
                    onClick = { /* Trigger self test */ },
                    modifier = Modifier.weight(1f),
                    containerColor = IndustrialColors.Border
                )
            }
        }

        IndustrialPanel(title = "Visual State") {
            Text(
                "PROBE STOWED",
                color = IndustrialColors.Accent,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                "NO ERRORS REPORTED",
                color = IndustrialColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
