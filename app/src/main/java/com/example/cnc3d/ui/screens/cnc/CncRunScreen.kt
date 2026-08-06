package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cnc3d.ui.components.ToolpathViewer
import com.example.cnc3d.ui.theme.IndustrialButton
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialDro
import com.example.cnc3d.ui.theme.IndustrialPanel
import com.example.cnc3d.ui.theme.IndustrialSensor
import com.example.cnc3d.ui.theme.IndustrialTabbedPanel
import com.example.cnc3d.viewmodels.CncViewModel
import java.util.Locale

@Composable
fun CncRunScreen(
    viewModel: CncViewModel,
    onShowInfo: (String) -> Unit = {},
) {
    val status by viewModel.status.collectAsState()
    val gcodePath by viewModel.gcodePath.collectAsState()
    val availableFiles by viewModel.availableFiles.collectAsState()
    val selectedFile by viewModel.selectedFile.collectAsState()
    val scrollState = rememberScrollState()
    
    var showFileLoader by remember { mutableStateOf(value = false) }

    if (showFileLoader) {
        AlertDialog(
            onDismissRequest = { showFileLoader = false },
            confirmButton = {},
            title = { Text("Select Machine File", color = IndustrialColors.TextPrimary) },
            text = {
                Column(Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())) {
                    availableFiles.forEach { file ->
                        ListItem(
                            headlineContent = { Text(file, color = IndustrialColors.Accent) },
                            modifier = Modifier.clickable { 
                                viewModel.selectFile(file)
                                showFileLoader = false
                            }
                        )
                    }
                }
            },
            containerColor = IndustrialColors.Panel
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadFiles()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Toolpath Viewer (Compact)
        IndustrialPanel(
            title = "Live Toolpath Viewer",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            ToolpathViewer(gcodePath, Modifier.fillMaxSize())
        }

        // 2. DRO & Sensors (Tabbed)
        IndustrialTabbedPanel(
            tabs = listOf("DRO", "Status"),
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> Column {
                    IndustrialDro("X Axis", String.format(Locale.US, "%.3f", status.position.first))
                    IndustrialDro("Y Axis", String.format(Locale.US, "%.3f", status.position.second))
                    IndustrialDro("Z Axis", String.format(Locale.US, "%.3f", status.position.third))
                }
                1 -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        IndustrialSensor("Limits", status.sensors["limits"] ?: false, Modifier.weight(1f))
                        IndustrialSensor("Probe", status.sensors["probe"] ?: false, Modifier.weight(1f))
                    }
                    Row(Modifier.fillMaxWidth()) {
                        IndustrialSensor("Spindle", status.sensors["spindle"] ?: false, Modifier.weight(1f))
                        IndustrialSensor("Coolant", status.sensors["coolant"] ?: false, Modifier.weight(1f))
                    }
                    HorizontalDivider(color = IndustrialColors.Border)
                    Text("STATE: ${status.state}", color = IndustrialColors.Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // 3. Command Center (Tabbed)
        IndustrialTabbedPanel(
            tabs = listOf("Cycle", "Jog", "Overrides"),
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(selectedFile?.take(15) ?: "No file", color = IndustrialColors.Accent, fontSize = 12.sp)
                        IndustrialButton("Load", onClick = { showFileLoader = true }, modifier = Modifier.height(36.dp))
                    }
                    HorizontalDivider(color = IndustrialColors.Border)
                    IndustrialButton(
                        text = "Cycle Start", 
                        onClick = { selectedFile?.let { viewModel.start(it) } }, 
                        modifier = Modifier.fillMaxWidth(), 
                        containerColor = IndustrialColors.Success,
                        enabled = selectedFile != null
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IndustrialButton("Hold", onClick = { viewModel.emergencyStop() }, modifier = Modifier.weight(1f), containerColor = IndustrialColors.Warning)
                        IndustrialButton("Stop", onClick = { viewModel.emergencyStop() }, modifier = Modifier.weight(1f), containerColor = IndustrialColors.Emergency)
                    }
                }
                1 -> JogControl(viewModel)
                2 -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OverrideSlider("Feed Rate", status.overrides.first)
                    OverrideSlider("Spindle Speed", status.overrides.second)
                    OverrideSlider("Rapid Rate", status.overrides.third)
                }
            }
        }
    }
}

@Composable
private fun JogControl(viewModel: CncViewModel) {
    var step by remember { mutableFloatStateOf(10f) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // XY Cross Layout (3x3 logic)
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                SmallJogButton("Y+", onClick = { viewModel.jog("Y", step) })
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    SmallJogButton("X-", onClick = { viewModel.jog("X", -step) })
                    Spacer(Modifier.size(56.dp)) // Center gap
                    SmallJogButton("X+", onClick = { viewModel.jog("X", step) })
                }
                SmallJogButton("Y-", onClick = { viewModel.jog("Y", -step) })
            }

            Spacer(Modifier.width(32.dp)) // Space between XY and Z

            // Z Axis Column
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                SmallJogButton("Z+", onClick = { viewModel.jog("Z", step) })
                Spacer(Modifier.height(48.dp))
                SmallJogButton("Z-", onClick = { viewModel.jog("Z", -step) })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(0.1f, 1f, 10f).forEach { s ->
                FilterChip(
                    selected = step == s,
                    onClick = { step = s },
                    label = { Text("${s}mm", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndustrialColors.Accent,
                        selectedLabelColor = Color.White,
                        containerColor = IndustrialColors.Panel,
                        labelColor = IndustrialColors.TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun SmallJogButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun OverrideSlider(label: String, initialValue: Int) {
    var value by remember { mutableStateOf(initialValue.toFloat()) }
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = IndustrialColors.TextSecondary, fontSize = 10.sp)
            Text("${value.toInt()}%", color = IndustrialColors.Accent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        Slider(
            value = value,
            onValueChange = { value = it },
            valueRange = 0f..200f,
            modifier = Modifier.height(24.dp),
            colors = SliderDefaults.colors(
                thumbColor = IndustrialColors.Accent,
                activeTrackColor = IndustrialColors.Accent,
                inactiveTrackColor = IndustrialColors.Border
            )
        )
    }
}
