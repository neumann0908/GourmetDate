package com.example.gourmetdate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

// Modelo de datos expandido
data class Ingrediente(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nombre: String,
    val costo: Double,
    val pesoBruto: Double,
    val pesoNeto: Double,
    val unidad: String,
    val rendimiento: Double,
    val costoReal: Double
)

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
    // Campos del formulario
    var nombre by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var pesoBruto by remember { mutableStateOf("") }
    var pesoNeto by remember { mutableStateOf("") }

    // Selector de unidad de medida
    val unidades = listOf("kg", "g", "lb", "oz", "ml")
    var unidadSeleccionada by remember { mutableStateOf("kg") }
    var menuUnidadesExpandido by remember { mutableStateOf(false) }

    // Estado del cálculo actual
    var rendimientoCalculado by remember { mutableStateOf<Double?>(null) }
    var costoRealCalculado by remember { mutableStateOf<Double?>(null) }
    var mensajeError by remember { mutableStateOf("") }

    // Inventario de insumos
    val listaIngredientes = remember { mutableStateListOf<Ingrediente>() }

    // Función para formatear el campo costo con 2 decimales
    fun formatearCosto() {
        val valor = costo.toDoubleOrNull()
        if (valor != null) {
            costo = String.format(Locale.US, "%.2f", valor)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🥑 Ingredientes & Mermas",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // --- Tarjeta de Métricas Globales ---
        if (listaIngredientes.isNotEmpty()) {
            item {
                val promedioRendimiento = listaIngredientes.map { it.rendimiento }.average()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Total Insumos", style = MaterialTheme.typography.labelMedium)
                            Text(text = "${listaIngredientes.size}", style = MaterialTheme.typography.titleMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Rendimiento Promedio", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = String.format(Locale.US, "%.1f%%", promedioRendimiento),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }

        // --- Formulario de Entrada ---
        item {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del ingrediente (ej. Salmón)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo Costo con auto-formato de decimales
                OutlinedTextField(
                    value = costo,
                    onValueChange = { costo = it },
                    label = { Text("Costo ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1.5f)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                formatearCosto()
                            }
                        }
                )

                // Dropdown para seleccionar unidad de medida
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { menuUnidadesExpandido = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Unidad: $unidadSeleccionada")
                    }
                    DropdownMenu(
                        expanded = menuUnidadesExpandido,
                        onDismissRequest = { menuUnidadesExpandido = false }
                    ) {
                        unidades.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u) },
                                onClick = {
                                    unidadSeleccionada = u
                                    menuUnidadesExpandido = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pesoBruto,
                    onValueChange = { pesoBruto = it },
                    label = { Text("P. Bruto ($unidadSeleccionada)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = pesoNeto,
                    onValueChange = { pesoNeto = it },
                    label = { Text("P. Neto ($unidadSeleccionada)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- Botones de Acción ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        formatearCosto()
                        val c = costo.toDoubleOrNull() ?: 0.0
                        val pb = pesoBruto.toDoubleOrNull() ?: 0.0
                        val pn = pesoNeto.toDoubleOrNull() ?: 0.0

                        if (pb > 0 && pn > 0 && pn <= pb) {
                            rendimientoCalculado = (pn / pb) * 100
                            costoRealCalculado = c / pn
                            mensajeError = ""
                        } else {
                            mensajeError = "Datos inválidos: El P. Neto debe ser menor o igual al P. Bruto."
                            rendimientoCalculado = null
                            costoRealCalculado = null
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Calcular")
                }

                Button(
                    onClick = {
                        if (rendimientoCalculado != null && costoRealCalculado != null) {
                            val cVal = costo.toDoubleOrNull() ?: 0.0
                            val pbVal = pesoBruto.toDoubleOrNull() ?: 0.0
                            val pnVal = pesoNeto.toDoubleOrNull() ?: 0.0
                            val nom = if (nombre.isBlank()) "Ingrediente ${listaIngredientes.size + 1}" else nombre

                            listaIngredientes.add(
                                Ingrediente(
                                    nombre = nom,
                                    costo = cVal,
                                    pesoBruto = pbVal,
                                    pesoNeto = pnVal,
                                    unidad = unidadSeleccionada,
                                    rendimiento = rendimientoCalculado!!,
                                    costoReal = costoRealCalculado!!
                                )
                            )

                            // Limpiar formulario
                            nombre = ""
                            costo = ""
                            pesoBruto = ""
                            pesoNeto = ""
                            rendimientoCalculado = null
                            costoRealCalculado = null
                        }
                    },
                    enabled = rendimientoCalculado != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }

        if (mensajeError.isNotEmpty()) {
            item {
                Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
            }
        }

        // --- Mostrar Resultado Temporal ---
        if (rendimientoCalculado != null && costoRealCalculado != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Resultado para: ${if (nombre.isEmpty()) "Ingrediente" else nombre}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(text = "Rendimiento: ${String.format(Locale.US, "%.2f%%", rendimientoCalculado)}")
                        Text(
                            text = "Costo Real: $${String.format(Locale.US, "%.2f", costoRealCalculado)} / $unidadSeleccionada",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // --- Lista de Insumos Registrados ---
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Mis Insumos Registrados", style = MaterialTheme.typography.titleMedium)
        }

        items(listaIngredientes, key = { it.id }) { ing ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ing.nombre, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Rendimiento: ${String.format(Locale.US, "%.1f%%", ing.rendimiento)}")
                        Text(
                            text = "Costo Real: $${String.format(Locale.US, "%.2f", ing.costoReal)} / ${ing.unidad}",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Botón para eliminar elemento
                    IconButton(onClick = { listaIngredientes.remove(ing) }) {
                        Text("🗑️")
                    }
                }
            }
        }
    }
}
