package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cnc3d.domain.models.cnc.CncStatus
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialExpandableModule
import com.example.cnc3d.ui.theme.IndustrialInfoPanel
import com.example.cnc3d.ui.theme.IndustrialLed
import com.example.cnc3d.ui.theme.IndustrialLedStrip
import com.example.cnc3d.ui.theme.LedState
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.CncDiagViewModel

@Composable
fun CncDiagScreen(
    viewModel: CncDiagViewModel,
    onShowInfo: (String) -> Unit = {}
) {
    val status by viewModel.status.collectAsState()
    CncDiagContent(status = status, onShowInfo = onShowInfo)
}

@Composable
fun CncDiagContent(
    status: CncStatus,
    onShowInfo: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 0. System Intelligence (Base Module)
        IndustrialExpandableModule(title = "Controller Intelligence") {
            IndustrialInfoPanel(
                title = "System Stats",
                info = mapOf(
                    "Firmware" to status.firmwareVersion,
                    "Uptime" to status.uptime,
                    "CPU Load" to "12%",
                    "Heap Free" to "45KB"
                )
            )
        }

        // 1. Inputs (Expandable Module) - Diagnostic Health
        IndustrialExpandableModule(title = "Inputs") {
            val isConnected = status.state != "Disconnected"
            val inputGroups = listOf(
                "X-Limits" to listOf("x_min", "x_max"),
                "Y-Limits" to listOf("y_min", "y_max"),
                "Z-Limits" to listOf("z_min", "z_max"),
                "Probe" to listOf("probe"),
                "Safety" to listOf("e_stop", "door"),
                "Faults" to listOf("fault_x", "fault_y", "fault_z"),
                "Aux" to listOf("aux1", "aux2", "aux3")
            )

            inputGroups.forEach { (label, keys) ->
                val activeCount = keys.count { status.sensors[it] == true }
                val state = when {
                    !isConnected -> LedState.INACTIVE // Gray if not connected
                    activeCount == 0 -> LedState.ERROR // Red: failure/unresponsive
                    activeCount < keys.size -> LedState.STARTING // Yellow: partial
                    else -> LedState.ACTIVE // Green: all ok
                }

                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    IndustrialLed(state, label, "Input Group Health", onShowInfo)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        label,
                        color = if (isConnected) IndustrialColors.TextPrimary else IndustrialColors.TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 2. Outputs (Expandable Module) - Activation State
        IndustrialExpandableModule(title = "Outputs") {
            val isConnected = status.state != "Disconnected"
            val outputs = listOf(
                "Spindle Enable" to "spindle_en",
                "Coolant Flood" to "flood",
                "Coolant Mist" to "mist",
                "Vacuum Assist" to "vacuum",
                "Relay 1" to "relay1",
                "Relay 2" to "relay2",
                "Aux 1" to "aux_out1",
                "Aux 2" to "aux_out2"
            )

            outputs.forEach { (label, key) ->
                val isActive = status.sensors[key] == true
                val state = when {
                    !isConnected -> LedState.INACTIVE
                    isActive -> LedState.ACTIVE
                    else -> LedState.INACTIVE
                }

                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    IndustrialLed(state, label, if (isActive) "ACTIVE" else "INACTIVE", onShowInfo)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        label,
                        color = if (isConnected) IndustrialColors.TextPrimary else IndustrialColors.TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 3. Industrial I/O LEDs (Expandable Module) - Combined visual panel
        IndustrialExpandableModule(title = "Industrial I/O LEDs") {
            val isConnected = status.state != "Disconnected"
            Text(
                "INPUT MATRIX",
                color = IndustrialColors.TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            val allInputs = listOf(
                "x_min",
                "x_max",
                "y_min",
                "y_max",
                "z_min",
                "z_max",
                "probe",
                "e_stop",
                "door"
            )
            IndustrialLedStrip(
                leds = allInputs.map {
                    val isActive = status.sensors[it] == true
                    val state = when {
                        !isConnected -> LedState.INACTIVE
                        isActive -> LedState.ACTIVE
                        else -> LedState.INACTIVE
                    }
                    Triple(state, it.uppercase(), "Signal Status")
                },
                onShowInfo = onShowInfo,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "OUTPUT MATRIX",
                color = IndustrialColors.TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            val allOutputs = listOf("spindle_en", "flood", "mist", "vacuum", "relay1", "relay2")
            IndustrialLedStrip(
                leds = allOutputs.map {
                    val isActive = status.sensors[it] == true
                    val state = when {
                        !isConnected -> LedState.INACTIVE
                        isActive -> LedState.ACTIVE
                        else -> LedState.INACTIVE
                    }
                    Triple(state, it.uppercase(), "Actuator Status")
                },
                onShowInfo = onShowInfo,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 4. Limit Switches (Expandable Module) - Real-time process state
        IndustrialExpandableModule(title = "Limit Switches") {
            val isConnected = status.state != "Disconnected"
            val limits = listOf("X-Min", "X-Max", "Y-Min", "Y-Max", "Z-Min", "Z-Max")
            limits.forEach { label ->
                val key = label.lowercase().replace("-", "_")
                val isTriggered = status.sensors[key] == true
                val state = when {
                    !isConnected -> LedState.INACTIVE
                    isTriggered -> LedState.ERROR // Red if triggered
                    else -> LedState.ACTIVE // Green if safe
                }

                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    IndustrialLed(
                        state,
                        label,
                        if (isTriggered) "TRIGGERED" else "SAFE",
                        onShowInfo
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        label,
                        color = if (isTriggered && isConnected) IndustrialColors.Error else if (isConnected) IndustrialColors.TextPrimary else IndustrialColors.TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 5. E-Stop (Expandable Module) - Safety circuit
        IndustrialExpandableModule(title = "E-Stop") {
            val isConnected = status.state != "Disconnected"
            val isEstopActive = status.sensors["e_stop"] == true
            val state = when {
                !isConnected -> LedState.INACTIVE
                isEstopActive -> LedState.ERROR
                else -> LedState.ACTIVE
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    IndustrialLed(
                        state,
                        "Safety Circuit",
                        if (isEstopActive) "TRIPPED" else "CLOSED",
                        onShowInfo
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Safety Circuit State",
                        color = if (isConnected) IndustrialColors.TextPrimary else IndustrialColors.TextSecondary
                    )
                }
                ConfigRow(
                    "Feedback Signal",
                    if (!isConnected) "N/A" else if (isEstopActive) "HIGH (ESTOP)" else "LOW (SAFE)"
                )
            }
        }

        // 6. Probe State (Expandable Module)
        IndustrialExpandableModule(title = "Probe State") {
            val isConnected = status.state != "Disconnected"
            val isProbing = status.sensors["probe"] == true
            val state = when {
                !isConnected -> LedState.INACTIVE
                isProbing -> LedState.ERROR
                else -> LedState.ACTIVE
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                IndustrialLed(
                    state,
                    "Probe Contact",
                    if (isProbing) "TOUCHING" else "OPEN",
                    onShowInfo
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Probe Contact State",
                    color = if (isConnected) IndustrialColors.TextPrimary else IndustrialColors.TextSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CncDiagPreview() {
    _3dCncTheme {
        CncDiagContent(
            status = CncStatus(
                state = "Idle",
                sensors = mapOf(
                    "x_min" to false,
                    "x_max" to false,
                    "e_stop" to false,
                    "probe" to false
                ),
                firmwareVersion = "v1.2.3",
                uptime = "01:23:45"
            )
        )
    }
}

@Composable
private fun ConfigRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = IndustrialColors.TextSecondary, fontSize = 12.sp)
        Text(value, color = IndustrialColors.Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
