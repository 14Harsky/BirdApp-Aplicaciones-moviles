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
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ... (el resto de la UI de PerfilScreen se mantiene igual)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Opciones", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))

                Button(
                    onClick = {
                        viewModel.precargarDatosParaEditar()
                        navController.navigate("formulario/false")
                     },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Editar Perfil")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("formulario/true") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Cambiar Contraseña")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { mostrarDialogoEliminar = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Eliminar Cuenta", color = MaterialTheme.colorScheme.onErrorContainer)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.logout() }, // <-- Solo llama a logout
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarUsuario()
                        mostrarDialogoEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ... (InfoRow Composable)
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value.ifEmpty { "No especificado" })
    }
}