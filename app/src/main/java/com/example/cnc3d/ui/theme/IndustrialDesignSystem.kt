package com.example.cnc3d.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

object IndustrialColors {
    val Background = Color(0xFF0A0A0A)
    val Panel = Color(0xFF1E1E1E)
    val Accent = Color(0xFF00B0FF) // Siemens Blue
    val Emergency = Color(0xFFD50000)
    val Success = Color(0xFF43A047) // Green
    val Warning = Color(0xFFFFB300) // Yellow
    val Error = Color(0xFFD32F2F)   // Red
    val TextPrimary = Color(0xFFE0E0E0)
    val TextSecondary = Color(0xFF9E9E9E)
    val Border = Color(0xFF333333)
}

enum class LedState {
    ACTIVE, INACTIVE, STARTING, ERROR
}

@Composable
fun IndustrialLed(
    state: LedState,
    label: String,
    description: String,
    onShowInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val color = when (state) {
        LedState.ACTIVE -> IndustrialColors.Success
        LedState.INACTIVE -> IndustrialColors.Border
        LedState.STARTING -> IndustrialColors.Warning
        LedState.ERROR -> IndustrialColors.Error
    }

    Box(
        modifier = modifier
            .size(14.dp)
            .background(color, RoundedCornerShape(2.dp))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        // If finger is over this box
                        if (event.changes.any { it.pressed }) {
                            onShowInfo("$label: $description")
                        }
                    }
                }
            }
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
    )
}

@Composable
fun IndustrialLedStrip(
    leds: List<Triple<LedState, String, String>>,
    onShowInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(IndustrialColors.Panel)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leds.forEach { (state, label, desc) ->
            IndustrialLed(state, label, desc, onShowInfo)
        }
    }
}

@Composable
fun IndustrialPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .background(IndustrialColors.Panel, RoundedCornerShape(2.dp))
            .border(BorderStroke(1.dp, IndustrialColors.Border), RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(IndustrialColors.Border)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = title.uppercase(),
                color = IndustrialColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
fun IndustrialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = IndustrialColors.Accent,
    contentColor: Color = Color.White,
    enabled: Boolean = true
) {
    val snackbarHost = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()
    
    Button(
        onClick = {
            scope.launch {
                snackbarHost.currentSnackbarData?.dismiss()
                snackbarHost.showSnackbar("Selected: ${text.uppercase()}")
            }
            onClick()
        },
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.3f)
        ),
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun IndustrialEmergencyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(4.dp)
) {
    val snackbarHost = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                snackbarHost.currentSnackbarData?.dismiss()
                snackbarHost.showSnackbar("EMERGENCY STOP TRIGGERED")
            }
            onClick()
        },
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = IndustrialColors.Emergency,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "EMERGENCY STOP",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun IndustrialDro(
    label: String,
    value: String,
    unit: String = "mm",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = IndustrialColors.TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = IndustrialColors.Accent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = unit,
                color = IndustrialColors.TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun IndustrialSensor(
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    if (active) IndustrialColors.Success else IndustrialColors.Border,
                    RoundedCornerShape(2.dp)
                )
        )
        Text(
            text = label.uppercase(),
            color = if (active) IndustrialColors.TextPrimary else IndustrialColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IndustrialVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier
        .fillMaxHeight()
        .width(48.dp), contentAlignment = Alignment.Center) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = -90f
                    translationX = 0f
                }
                .width(200.dp), // Adjust height of the vertical slider
            colors = SliderDefaults.colors(
                thumbColor = IndustrialColors.Accent,
                activeTrackColor = IndustrialColors.Accent,
                inactiveTrackColor = IndustrialColors.Border
            )
        )
    }
}

@Composable
fun IndustrialTabbedPanel(
    tabs: List<String>,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .background(IndustrialColors.Panel, RoundedCornerShape(2.dp))
            .border(BorderStroke(1.dp, IndustrialColors.Border), RoundedCornerShape(2.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(IndustrialColors.Border)
        ) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (selectedTab == index) IndustrialColors.Panel else IndustrialColors.Border)
                        .clickable { selectedTab = index }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.uppercase(),
                        color = if (selectedTab == index) IndustrialColors.Accent else IndustrialColors.TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
        Box(modifier = Modifier.padding(12.dp)) {
            content(selectedTab)
        }
    }
}

@Composable
fun IndustrialTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            color = IndustrialColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontFamily = FontFamily.Monospace
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = IndustrialColors.TextSecondary.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = IndustrialColors.Accent,
                unfocusedBorderColor = IndustrialColors.Border,
                cursorColor = IndustrialColors.Accent,
                focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(2.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions
        )
    }
}

@Composable
fun IndustrialInfoPanel(
    title: String,
    info: Map<String, String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(value = false) }

    Column(
        modifier = modifier
            .background(IndustrialColors.Panel, RoundedCornerShape(2.dp))
            .border(BorderStroke(1.dp, IndustrialColors.Border), RoundedCornerShape(2.dp))
            .clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(IndustrialColors.Border)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                color = IndustrialColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (expanded) "▼" else "▶",
                color = IndustrialColors.TextSecondary,
                fontSize = 10.sp
            )
        }
        if (expanded) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                info.forEach { (k, v) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(k, color = IndustrialColors.TextSecondary, fontSize = 11.sp)
                        Text(v, color = IndustrialColors.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
