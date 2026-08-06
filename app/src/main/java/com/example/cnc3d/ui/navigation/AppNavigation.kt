package com.example.cnc3d.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.cnc3d.ui.screens.analyzer.AnalyzerScreen
import com.example.cnc3d.ui.screens.camera.CameraScreen
import com.example.cnc3d.ui.screens.cloud.CloudScreen
import com.example.cnc3d.ui.screens.cnc.CncRootScreen
import com.example.cnc3d.ui.screens.home.HomeScreen
import com.example.cnc3d.ui.screens.mesh.MeshScreen
import com.example.cnc3d.ui.screens.printer.PrinterRootScreen
import com.example.cnc3d.ui.screens.settings.SettingsScreen
import com.example.cnc3d.ui.screens.timelapse.TimelapseScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier
    ) {

        composable<Routes.Home> {
            HomeScreen(navController)
        }

        composable<Routes.CNC> { backStackEntry ->
            val route = backStackEntry.toRoute<Routes.CNC>()
            CncRootScreen(navController, hiltViewModel(), route.subScreen)
        }

        composable<Routes.Printer> { backStackEntry ->
            val route = backStackEntry.toRoute<Routes.Printer>()
            PrinterRootScreen(navController, hiltViewModel(), route.subScreen)
        }

        composable<Routes.Camera> {
            CameraScreen(navController)
        }

        composable<Routes.Timelapse> {
            TimelapseScreen(navController, hiltViewModel())
        }

        composable<Routes.Mesh> {
            MeshScreen(navController)
        }

        composable<Routes.Analyzer> {
            AnalyzerScreen(navController, hiltViewModel())
        }

        composable<Routes.Cloud> {
            CloudScreen(navController, hiltViewModel())
        }

        composable<Routes.Settings> {
            SettingsScreen(navController)
        }
    }
}
