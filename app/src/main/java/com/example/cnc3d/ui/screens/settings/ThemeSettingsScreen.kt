package com.example.cnc3d.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.theme.AppTheme
import com.example.cnc3d.ui.theme.ThemeManager

@Composable
fun ThemeSettingsScreen() {

    Column(Modifier.padding(16.dp)) {

        Text("Theme", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { ThemeManager.current = AppTheme.DARK }) {
            Text("Dark")
        }

        Button(onClick = { ThemeManager.current = AppTheme.CNC_INDUSTRIAL }) {
            Text("CNC Industrial")
        }

        Button(onClick = { ThemeManager.current = AppTheme.PRINTER_FRIENDLY }) {
            Text("Printer Friendly")
        }
    }
}
