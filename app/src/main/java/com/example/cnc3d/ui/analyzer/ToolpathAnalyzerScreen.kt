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
import androidx.compose.ui.unit.dp
import com.example.cnc3d.domain.models.ToolpathWarning
import com.example.cnc3d.viewmodels.ToolpathAnalyzerViewModel

@Composable
fun ToolpathAnalyzerScreen(vm: ToolpathAnalyzerViewModel) {

    val warnings by vm.warnings.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Toolpath Analyzer", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        warnings.forEach { w: ToolpathWarning ->
            Text("Línea ${w.line}: ${w.type} — ${w.message}")
            Spacer(Modifier.height(8.dp))
        }
    }
}
