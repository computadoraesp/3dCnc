package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cnc3d.ui.theme.IndustrialButton
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialPanel
import com.example.cnc3d.ui.theme.IndustrialTextField
import com.example.cnc3d.viewmodels.CncToolViewModel

@Composable
fun CncToolScreen(viewModel: CncToolViewModel) {
    val tools by viewModel.toolLibrary.collectAsState()
    val units by viewModel.units.collectAsState()
    val unit = units.uppercase()
    
    var selectedToolId by remember { mutableIntStateOf(-1) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingToolId by remember { mutableIntStateOf(-1) }

    // Dialog state
    var toolName by remember { mutableStateOf("") }
    var toolLength by remember { mutableStateOf("") }
    var toolDiameter by remember { mutableStateOf("") }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = IndustrialColors.Panel,
            title = {
                Text(
                    if (editingToolId == -1) "ADD NEW TOOL" else "EDIT TOOL T$editingToolId",
                    color = IndustrialColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    IndustrialTextField(
                        value = toolName,
                        onValueChange = { toolName = it },
                        label = "Description"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IndustrialTextField(
                            value = toolLength,
                            onValueChange = { toolLength = it },
                            label = "Length ($unit)",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        IndustrialTextField(
                            value = toolDiameter,
                            onValueChange = { toolDiameter = it },
                            label = "Diameter ($unit)",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            },
            confirmButton = {
                IndustrialButton(
                    text = "SAVE",
                    onClick = {
                        val len = toolLength.toFloatOrNull() ?: 0f
                        val dia = toolDiameter.toFloatOrNull() ?: 0f
                        if (editingToolId == -1) {
                            viewModel.addTool(toolName, len, dia)
                        } else {
                            viewModel.updateTool(editingToolId, toolName, len, dia)
                        }
                        showEditDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCEL", color = IndustrialColors.TextSecondary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Tool Library ($unit)", modifier = Modifier.weight(1f)) {
            LazyColumn {
                item {
                    Row(Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)) {
                        Text(
                            "ID",
                            Modifier.width(40.dp),
                            color = IndustrialColors.TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Description",
                            Modifier.weight(1f),
                            color = IndustrialColors.TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Len",
                            Modifier.width(60.dp),
                            color = IndustrialColors.TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Dia",
                            Modifier.width(60.dp),
                            color = IndustrialColors.TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                items(tools) { tool ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (selectedToolId == tool.id) IndustrialColors.Accent.copy(
                                    alpha = 0.2f
                                ) else Color.Transparent
                            )
                            .clickable { selectedToolId = tool.id }
                            .padding(vertical = 8.dp)
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
                text = "Edit Tool",
                onClick = {
                    val tool = tools.find { it.id == selectedToolId }
                    if (tool != null) {
                        editingToolId = tool.id
                        toolName = tool.name
                        toolLength = tool.length.toString()
                        toolDiameter = tool.diameter.toString()
                        showEditDialog = true
                    }
                }, 
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

        IndustrialButton(
            "Add New Tool",
            onClick = {
                editingToolId = -1
                toolName = "New Tool"
                toolLength = "0.0"
                toolDiameter = "0.0"
                showEditDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = IndustrialColors.Success
        )
    }
}
