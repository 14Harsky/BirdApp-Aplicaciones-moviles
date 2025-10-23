package com.example.birdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.birdapp.view.FormularioScreen
import com.example.birdapp.view.ResumenScreen
import com.example.birdapp.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val usuarioViewModel: UsuarioViewModel = viewModel()

            NavHost(
                navController = navController,
                startDestination = "formulario"
            ) {
                composable("formulario") {
                    FormularioScreen(navController = navController, viewModel = usuarioViewModel)
                }
                composable("resumen") {
                    HubPrincipalScreen(viewModel = usuarioViewModel, navController = navController)
                }
                composable("aves") {
                    // Aquí irá tu pantalla de aves (la crearemos después)
                    AvesScreen(navController = navController)
                }
                composable("perfil") {
                    // Pantalla de perfil detallado
                    PerfilScreen(viewModel = usuarioViewModel, navController = navController)
                }
                composable("mis-aves") {
                    // Pantalla de aves observadas (futura)
                    MisAvesScreen(navController = navController)
                }
            }
        }
    }
}

