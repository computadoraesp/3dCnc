package com.example.cnc3d.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cnc3d.ui.navigation.Routes
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme._3dCncTheme

/**
 * The main landing screen of the application.
 *
 * Displays a status header with live metrics and an industrial grid of tiles
 * for navigating to various machine control and monitoring screens.
 *
 * @param navController The navigation controller used to handle screen transitions.
 */
@Composable
fun HomeScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "3dCNC# Control System",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = IndustrialColors.Accent,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        IndustrialGrid(navController)
    }
}

@Composable
private fun IndustrialGrid(navController: NavHostController) {

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Tile("CNC Control", IndustrialColors.Accent) { navController.navigate(Routes.CNC()) }
            Tile("Printer Control", IndustrialColors.Warning) { navController.navigate(Routes.Printer()) }
        }
    }
}

/**
 * A interactive tile used in the industrial grid.
 *
 * @param title The text displayed on the tile.
 * @param color The primary color theme for the tile's background and text.
 * @param onClick Callback triggered when the tile is tapped.
 */
@Composable
private fun RowScope.Tile(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    var pulse by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        targetValue = if (pulse) color.copy(alpha = 0.20f) else color.copy(alpha = 0.12f),
        label = ""
    )

    LaunchedEffect(Unit) {
        while (true) {
            pulse = !pulse
            kotlinx.coroutines.delay(900)
        }
    }

    Card(
        modifier = Modifier
            .weight(1f)
            .height(150.dp),
        colors = CardDefaults.cardColors(containerColor = animatedColor),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        onClick = onClick
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = color
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    _3dCncTheme {
        HomeScreen(navController = rememberNavController())
    }
}
