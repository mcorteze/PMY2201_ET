package com.example.veterinaria.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Pantalla para recuperación de contraseña
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarPasswordScreen(navController: NavController) {
    // Variable para almacenar el correo electrónico
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            // Barra superior simple con botón de regreso
            TopAppBar(
                title = { Text("Recuperar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp), // Espaciado interno
            horizontalAlignment = Alignment.CenterHorizontally // Centrar contenido
        ) {
            Text(
                text = "Ingresa tu correo electrónico y te enviaremos las instrucciones para restablecer tu contraseña.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo para ingreso de correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para enviar solicitud
            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        // Simulación de envío de correo
                        Toast.makeText(context, "Correo de recuperación enviado", Toast.LENGTH_LONG).show()
                        // Regresar al login
                        navController.navigateUp()
                    } else {
                        Toast.makeText(context, "Por favor ingresa un correo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enviar Instrucciones")
            }
        }
    }
}
