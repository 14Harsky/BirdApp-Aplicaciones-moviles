package com.example.birdapp.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.birdapp.model.Ave
import com.example.birdapp.viewmodel.AvesViewModel
import com.example.birdapp.viewmodel.AvesObservadasViewModel
import com.google.android.gms.location.LocationServices
import java.text.Normalizer

@Composable
fun AvesScreen(navController: NavController, avesObservadasViewModel: AvesObservadasViewModel) {
    val avesViewModel: AvesViewModel = viewModel()
    val aves by avesViewModel.aves.collectAsState()
    val isLoading by avesViewModel.isLoading.collectAsState()
    val avesObservadas by avesObservadasViewModel.avesObservadas.collectAsState()

    var aveSeleccionada by remember { mutableStateOf<Ave?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var ubicacion by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                ubicacion = "${location.latitude}, ${location.longitude}"
                            }
                        }
                }
            }
        }
    )

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
                ubicacion = ""
                notas = ""
            },
            title = { Text("Agregar Observación") },
            text = {
                Column {
                    Text("Ave: ${aveSeleccionada?.name?.spanish ?: ""}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) -> {
                                fusedLocationClient.lastLocation
                                    .addOnSuccessListener { location ->
                                        if (location != null) {
                                            ubicacion = "${location.latitude}, ${location.longitude}"
                                        }
                                    }
                            }
                            else -> {
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    }) {
                        Text("Obtener Ubicación")
                    }
                    if (ubicacion.isNotEmpty()) {
                        Text("Ubicación: $ubicacion")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = { Text("Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        aveSeleccionada?.let { ave ->
                            avesObservadasViewModel.agregarAveObservada(
                                ave = ave,
                                ubicacion = ubicacion,
                                notas = notas
                            )
                            mostrarDialogo = false
                            ubicacion = ""
                            notas = ""
                        }
                    },
                    enabled = ubicacion.isNotBlank()
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                        ubicacion = ""
                        notas = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val avesFiltradas = aves.filter { ave ->
        val noObservada = avesObservadas.none { it.nombreAve == ave.name.spanish }
        val coincideBusqueda = ave.name.spanish.unaccent().contains(searchText.unaccent(), ignoreCase = true)
        noObservada && coincideBusqueda
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Explorar Aves",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscar ave") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(avesFiltradas) { ave ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        onClick = {
                            aveSeleccionada = ave
                            mostrarDialogo = true
                        }
                    ) {
                        Column {
                            AsyncImage(
                                model = ave.images.main,
                                contentDescription = ave.name.spanish,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    ave.name.spanish,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    ave.name.english,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Toca para agregar observación",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun String.unaccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    return regex.replace(temp, "")
}
