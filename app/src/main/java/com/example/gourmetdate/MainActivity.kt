package com.example.gourmetdate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IngredientesScreen()
                }
            }
        }
    }
}

@Composable
fun IngredientesScreen() {
    // Variables de entrada
    var nombre by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var pesoBruto by remember { mutableStateOf("") }
    var pesoNeto by remember { mutableStateOf("") }

    // Nuevas variables para mostrar los resultados en pantalla
    var rendimientoTexto by remember { mutableStateOf("") }
    var costoRealTexto by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🥑 Ingredientes & Mermas", 
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del ingrediente (ej. Aguacate)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = costo,
            onValueChange = { costo = it },
            label = { Text("Costo total de compra ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pesoBruto,
            onValueChange = { pesoBruto = it },
            label = { Text("Peso Bruto (con cáscara/empaque)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pesoNeto,
            onValueChange = { pesoNeto = it },
            label = { Text("Peso Neto (solo lo utilizable)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { 
                // Lógica de cálculo al presionar el botón
                val c = costo.toDoubleOrNull() ?: 0.0
                val pb = pesoBruto.toDoubleOrNull() ?: 0.0
                val pn = pesoNeto.toDoubleOrNull() ?: 0.0

                if (pb > 0 && pn > 0 && pn <= pb) {
                    val porcentajeRendimiento = (pn / pb) * 100
                    val costoVerdadero = c / pn
                    
                    rendimientoTexto = String.format("%.2f %%", porcentajeRendimiento)
                    costoRealTexto = String.format("$ %.2f por unidad utilizable", costoVerdadero)
                    mensajeError = ""
                } else {
                    mensajeError = "Revisa los datos: El peso neto no puede ser mayor al bruto, y deben ser mayores a 0."
                    rendimientoTexto = ""
                    costoRealTexto = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular Rendimiento")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar un mensaje de error si los datos están mal ingresados
        if (mensajeError.isNotEmpty()) {
            Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
        }

        // Mostrar la tarjeta de resultados si el cálculo fue exitoso
        if (rendimientoTexto.isNotEmpty() && costoRealTexto.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resultados para: ${if (nombre.isEmpty()) "Ingrediente" else nombre}", 
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Rendimiento: $rendimientoTexto")
                    Text(text = "Costo Real: $costoRealTexto", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
