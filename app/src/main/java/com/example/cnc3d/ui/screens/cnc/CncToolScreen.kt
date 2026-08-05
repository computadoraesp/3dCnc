package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.CncViewModel

@Composable
fun CncToolScreen(viewModel: CncViewModel) {
    val tools by viewModel.toolLibrary.collectAsState()
    var selectedToolId by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Tool Library", modifier = Modifier.weight(1f)) {
            LazyColumn {
                item {
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text("ID", Modifier.width(40.dp), color = IndustrialColors.TextSecondary)
                        Text("Description", Modifier.weight(1f), color = IndustrialColors.TextSecondary)
                        Text("Len", Modifier.width(60.dp), color = IndustrialColors.TextSecondary)
                        Text("Dia", Modifier.width(60.dp), color = IndustrialColors.TextSecondary)
                    }
                }
                items(tools) { tool ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (selectedToolId == tool.id) IndustrialColors.Accent.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { selectedToolId = tool.id }
                            .padding(vertical = 4.dp)
                    ) {
                        Text("T${tool.id}", Modifier.width(40.dp), color = IndustrialColors.Accent, fontWeight = FontWeight.Bold)
                        Text(tool.name, Modifier.weight(1f), color = IndustrialColors.TextPrimary)
                        Text("${tool.length}", Modifier.width(60.dp), color = IndustrialColors.TextPrimary)
                        Text("${tool.diameter}", Modifier.width(60.dp), color = IndustrialColors.TextPrimary)
                    }
                    HorizontalDivider(color = IndustrialColors.Border)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IndustrialButton(
                text = "Load Tool", 
                onClick = { /* Load logic */ }, 
                modifier = Modifier.weight(1f),
                enabled = selectedToolId != -1
            )
            IndustrialButton(
                text = "Delete", 
                onClick = { 
                    viewModel.deleteTool(selectedToolId)
                    selectedToolId = -1
                }, 
                modifier = Modifier.weight(1f), 
                containerColor = IndustrialColors.Emergency,
                enabled = selectedToolId != -1
            )
        }
        
        IndustrialButton("Add New Tool", onClick = { viewModel.addTool("New Tool", 0f, 0f) }, modifier = Modifier.fillMaxWidth(), containerColor = IndustrialColors.Success)
    }
}
