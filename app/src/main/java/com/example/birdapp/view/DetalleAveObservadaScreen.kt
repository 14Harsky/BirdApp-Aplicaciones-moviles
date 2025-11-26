package com.example.birdapp.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.birdapp.viewmodel.AvesObservadasViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun DetalleAveObservadaScreen(
    navController: NavController,
    viewModel: AvesObservadasViewModel,
    aveId: String
) {
    val avesObservadas by viewModel.avesObservadas.collectAsState()
    val ave = remember(avesObservadas, aveId) {
        avesObservadas.find { it.id == aveId }
    }

    if (ave == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Ave no encontrada")
        }
        return
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var enlargedImage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val latLngParts = ave.ubicacion.split(",").map { it.trim().toDoubleOrNull() }
    val location = if (latLngParts.size == 2 && latLngParts[0] != null && latLngParts[1] != null) {
        LatLng(latLngParts[0]!!, latLngParts[1]!!)
    } else {
        null // O una ubicación por defecto
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(ave.nombreAve, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Observada el: ${ave.fechaObservacion}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Main image from API
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = ave.imageUrl,
                contentDescription = ave.nombreAve,
                modifier = Modifier
                    .height(300.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Observado en:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Map
        if (location != null) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 15f)
            }
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = rememberMarkerState(position = location),
                    title = ave.nombreAve
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f),
                contentAlignment = Alignment.Center
            ) {
                Text("Ubicación no disponible para mostrar en el mapa.")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons are now below the map
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Foto desde Galería")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                selectedImageUri?.let { uri ->
                    viewModel.addAveObservadaImage(aveId, uri.toString())
                    selectedImageUri = null
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedImageUri != null
        ) {
            Text("Guardar Foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section for user-uploaded images
        Text("Tus Fotos:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Display the image that is about to be uploaded
        selectedImageUri?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Nueva foto a guardar:")
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (ave.userImages.isEmpty()) {
            Text("Aún no has subido fotos.")
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ave.userImages) { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Foto de usuario",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { enlargedImage = imageUrl },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    enlargedImage?.let { imageUrl ->
        Dialog(onDismissRequest = { enlargedImage = null }) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Foto de usuario ampliada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { enlargedImage = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}