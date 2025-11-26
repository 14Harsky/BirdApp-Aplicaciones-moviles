package com.example.birdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.birdapp.view.AvesScreen
import com.example.birdapp.view.DetalleAveObservadaScreen
import com.example.birdapp.view.FormularioScreen
import com.example.birdapp.view.LoginScreen
import com.example.birdapp.view.MisAvesScreen
import com.example.birdapp.view.PerfilScreen
import com.example.birdapp.view.ResumenScreen
import com.example.birdapp.viewmodel.AvesObservadasViewModel
import com.example.birdapp.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val usuarioViewModel: UsuarioViewModel = viewModel()
            val avesObservadasViewModel: AvesObservadasViewModel = viewModel()
            val isSessionActive by usuarioViewModel.isSessionActive.collectAsState()

            //logout o cuenta eliminada
            LaunchedEffect(isSessionActive) {
                if (!isSessionActive && navController.currentDestination?.route != "login") {
                     navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(navController = navController, viewModel = usuarioViewModel)
                }

                composable("registro") {
                    FormularioScreen(navController = navController, viewModel = usuarioViewModel, esCambioClave = null)
                }

                composable(
                    "formulario/{esCambioClave}",
                    arguments = listOf(navArgument("esCambioClave") { type = NavType.StringType })
                ) { backStackEntry ->
                    val esCambioClaveStr = backStackEntry.arguments?.getString("esCambioClave")
                    val esCambioClave = when (esCambioClaveStr) {
                        "true" -> true
                        "false" -> false
                        else -> null
                    }
                    FormularioScreen(navController, usuarioViewModel, esCambioClave)
                }

                composable("resumen") {
                    ResumenScreen(viewModel = usuarioViewModel, navController = navController)
                }

                composable("aves") {
                    AvesScreen(navController = navController, avesObservadasViewModel = avesObservadasViewModel)
                }

                composable("perfil") {
                    PerfilScreen(viewModel = usuarioViewModel, navController = navController)
                }

                composable("mis-aves") {
                    MisAvesScreen(navController = navController, avesObservadasViewModel = avesObservadasViewModel)
                }

                composable(
                    "detalle_ave/{aveId}",
                    arguments = listOf(navArgument("aveId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val aveId = backStackEntry.arguments?.getString("aveId")
                    if (aveId != null) {
                        DetalleAveObservadaScreen(navController, avesObservadasViewModel, aveId)
                    }
                }
            }
        }
    }

}
