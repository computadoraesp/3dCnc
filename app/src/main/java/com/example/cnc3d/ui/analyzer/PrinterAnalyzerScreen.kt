package com.example.cnc3d.ui.analyzer

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cnc3d.domain.models.PrinterWarning
import com.example.cnc3d.domain.models.PrinterWarningType
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.PrinterAnalyzerViewModel

@Composable
fun PrinterAnalyzerScreen(vm: PrinterAnalyzerViewModel) {
    val warnings by vm.warnings.collectAsState()
    PrinterAnalyzerContent(warnings = warnings)
}

@Composable
fun PrinterAnalyzerContent(warnings: List<PrinterWarning>) {
    Column(Modifier.padding(16.dp)) {

        Text("Printer G-code Analyzer", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        warnings.forEach { w: PrinterWarning ->
            Text("Línea ${w.line}: ${w.type} — ${w.message}")
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrinterAnalyzerPreview() {
    _3dCncTheme {
        PrinterAnalyzerContent(
            warnings = listOf(
                PrinterWarning(
                    PrinterWarningType.SPEED_TOO_HIGH,
                    "F3000 exceeds machine limits",
                    10
                ),
                PrinterWarning(
                    PrinterWarningType.TEMP_TOO_HIGH,
                    "M104 S300 exceeds safety limit (280)",
                    25
                )
            )
        )
    }
}
