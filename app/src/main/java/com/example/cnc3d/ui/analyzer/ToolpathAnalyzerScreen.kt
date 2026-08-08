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
import com.example.cnc3d.domain.models.ToolpathWarning
import com.example.cnc3d.domain.models.WarningType
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.ToolpathAnalyzerViewModel

@Composable
fun ToolpathAnalyzerScreen(vm: ToolpathAnalyzerViewModel) {
    val warnings by vm.warnings.collectAsState()
    ToolpathAnalyzerContent(warnings = warnings)
}

@Composable
fun ToolpathAnalyzerContent(warnings: List<ToolpathWarning>) {
    Column(Modifier.padding(16.dp)) {

        Text("Toolpath Analyzer", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        warnings.forEach { w: ToolpathWarning ->
            Text("Línea ${w.line}: ${w.type} — ${w.message}")
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToolpathAnalyzerPreview() {
    _3dCncTheme {
        ToolpathAnalyzerContent(
            warnings = listOf(
                ToolpathWarning(WarningType.Z_NEGATIVE, "Z coordinate is negative (-5.0)", 15),
                ToolpathWarning(WarningType.RAPID_INTO_MATERIAL, "G0 move below surface", 42)
            )
        )
    }
}
