package com.example.birdapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birdapp.viewmodel.NavigationEvent
import com.example.birdapp.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FormularioScreen(
    navController: NavController,
    viewModel: UsuarioViewModel,
    esCambioClave: Boolean?
) {
    val estado by viewModel.estado.collectAsState()

    
    if (esCambioClave == null) {
        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collectLatest { event ->
                if (event is NavigationEvent.NavigateToLogin) {
                    navController.navigate("login") {
                        popUpTo("registro") { inclusive = true }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val titulo = when (esCambioClave) {
            true -> "Cambiar Contraseña"
            false -> "Editar Perfil"
            null -> "Registro de Usuario"
        }
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (esCambioClave == null || esCambioClave == false) { 
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre") },
                isError = estado.errores.nombre != null,
                supportingText = {
                    estado.errores.nombre?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Correo") },
                isError = estado.errores.correo != null,
                supportingText = {
                    estado.errores.correo?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (esCambioClave == null || esCambioClave == true) {
            OutlinedTextField(
                value = estado.clave,
                onValueChange = viewModel::onClaveChange,
                label = { Text(if (esCambioClave == true) "Contraseña Actual" else "Clave") },
                visualTransformation = PasswordVisualTransformation(),
                isError = estado.errores.clave != null,
                supportingText = {
                    estado.errores.clave?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.repetirClave,
                onValueChange = viewModel::onRepetirClaveChange,
                label = { Text(if (esCambioClave == true) "Nueva Contraseña" else "Repetir Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = estado.errores.repetirClave != null,
                supportingText = {
                    estado.errores.repetirClave?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (esCambioClave == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = viewModel::onAceptarTerminosChange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Acepto los términos y condiciones")
            }
        }

        Button(
            onClick = {
                when (esCambioClave) {
                    true -> {
                        viewModel.cambiarClave()
                        navController.popBackStack()
                    }
                    false -> {
                        viewModel.actualizarPerfil()
                        navController.popBackStack()
                    }
                    null -> {
                        viewModel.registrarUsuario()
                    }
                }
            }
        ) {
            val textoBoton = when (esCambioClave) {
                true -> "Confirmar Cambio"
                false -> "Guardar Cambios"
                null -> "Registrar"
            }
            Text(textoBoton)
        }
    }
}
