package com.example.birdapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birdapp.viewmodel.UsuarioViewModel

@Composable
fun PerfilScreen(viewModel: UsuarioViewModel, navController: NavController) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        // Información Personal
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow("Nombre:", estado.nombre)
                InfoRow("Correo:", estado.correo)
                InfoRow("Dirección:", estado.direccion)
                InfoRow("Términos:", if (estado.aceptaTerminos) "Aceptados" else "No aceptados")
            }
        }

        // Opciones de Perfil
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "Opciones",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                // Botón Editar Perfil
                Button(
                    onClick = { navController.navigate("formulario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Editar Perfil")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Cambiar Contraseña
                Button(
                    onClick = { /* Futura implementación */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("Cambiar Contraseña")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Cerrar Sesión
                Button(
                    onClick = {
                        // Volver al formulario (simula cerrar sesión)
                        navController.navigate("formulario") {
                            popUpTo("resumen") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value.ifEmpty { "No especificado" })
    }
}