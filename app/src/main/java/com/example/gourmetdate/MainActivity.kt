package com.example.gourmetdate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// Enum para controlar la navegación interactiva entre secciones
enum class Seccion(val titulo: String, val ruta: String, val icono: String) {
    ESCRITORIO("Escritorio Principal", "/C/GOURMETDATE/DESKTOP/", "🖥️"),
    INGREDIENTES("Catálogo de Ingredientes", "/C/GOURMETDATE/INGREDIENTES/", "📁"),
    RECETAS("Gestión de Recetas", "/C/GOURMETDATE/RECETAS/", "📄"),
    PROVEEDORES("Directorio de Proveedores", "/C/GOURMETDATE/PROVEEDORES/", "📦"),
    ORDENES("Órdenes de Compra", "/C/GOURMETDATE/ORDENES/", "📋"),
    MERMAS("Ingredientes & Mermas", "/C/GOURMETDATE/MERMAS/", "🧪")
}

// Modelo de datos para ingredientes
data class Ingrediente(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nombre: String,
    val costo: Double,
    val pesoBruto: Double,
    val pesoNeto: Double,
    val unidad: String,
    val rendimiento: Double,
    val costoReal: Double,
    val analisisResumen: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE5DFEE)
                ) {
                    RetroMermasApp()
                }
            }
        }
    }
}

@Composable
fun RetroMermasApp() {
    var seccionActual by remember { mutableStateOf(Seccion.MERMAS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5DFEE))
    ) {
        // --- 1. Ruta / Header Superior Dinámico ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE3DCED))
                .border(1.dp, Color(0xFFB8AECA))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GOURMETDATE",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Black
            )
            Text(
                text = "   ${seccionActual.ruta}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF555555)
            )
        }

        // --- 2. Cuerpo de la Ventana Principal ---
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAF7F0), shape = RoundedCornerShape(10.dp))
                    .border(2.dp, Color.Black, shape = RoundedCornerShape(10.dp))
            ) {
                // Barra de título
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF0072C6),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = seccionActual.titulo,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.White, RoundedCornerShape(50))
                        )
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.LightGray, RoundedCornerShape(50))
                        )
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFFE81123), RoundedCornerShape(50))
                        )
                    }
                }

                // Cambio dinámico de pantalla según sección
                when (seccionActual) {
                    Seccion.MERMAS -> IngredientesFormularioRetro()
                    else -> SeccionEnConstruccion(seccion = seccionActual)
                }
            }
        }

        // --- 3. Dock Inferior Interactivo ---
        RetroBottomDock(
            seccionActual = seccionActual,
            onSeccionSeleccionada = { seccionActual = it }
        )
    }
}

@Composable
fun SeccionEnConstruccion(seccion: Seccion) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = seccion.icono, fontSize = 48.sp)
            Text(
                text = seccion.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp))
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "⚙️ Módulo en desarrollo. Toca MERMAS abajo para regresar.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun IngredientesFormularioRetro() {
    var nombre by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var pesoBruto by remember { mutableStateOf("") }
    var pesoNeto by remember { mutableStateOf("") }

    val unidades = listOf("kg", "g", "lb", "oz", "ml")
    var unidadSeleccionada by remember { mutableStateOf("kg") }
    var menuUnidadesExpandido by remember { mutableStateOf(false) }

    var rendimientoCalculado by remember { mutableStateOf<Double?>(null) }
    var costoRealCalculado by remember { mutableStateOf<Double?>(null) }
    var analisisTextoCalculado by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    val listaIngredientes = remember { mutableStateListOf<Ingrediente>() }

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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }

        if (listaIngredientes.isNotEmpty()) {
            item {
                val promedioRendimiento = listaIngredientes.map { it.rendimiento }.average()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = (-2).dp, y = (-2).dp)
                            .background(Color(0xFFE0E7FF), shape = RoundedCornerShape(8.dp))
                            .border(1.5.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Insumos", fontSize = 11.sp, color = Color.DarkGray)
                            Text("${listaIngredientes.size}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Rendimiento Promedio", fontSize = 11.sp, color = Color.DarkGray)
                            Text(
                                String.format(Locale.US, "%.1f%%", promedioRendimiento),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF6B21A8)
                            )
                        }
                    }
                }
            }
        }

        item {
            RetroInputField(
                label = "Nombre del ingrediente (ej. Aguacate)",
                value = nombre,
                onValueChange = { nombre = it },
                placeholderText = "(ej. Aguacate)"
            )
        }

        item {
            RetroInputField(
                label = "Costo total de compra ($)",
                value = costo,
                onValueChange = { costo = it },
                placeholderText = "Costo total de compra ($)",
                keyboardType = KeyboardType.Number,
                onFocusLost = { formatearCosto() }
            )
        }

        item {
            Column {
                Text(
                    text = "Peso Bruto (con cáscara/empaque)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1.8f)) {
                        RetroTextFieldRaw(
                            value = pesoBruto,
                            onValueChange = { pesoBruto = it },
                            placeholderText = "Peso Bruto (con cáscara/empaque)",
                            keyboardType = KeyboardType.Number
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        RetroButtonSmall(
                            text = "Unidad: $unidadSeleccionada",
                            onClick = { menuUnidadesExpandido = true }
                        )
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
        }

        item {
            RetroInputField(
                label = "Peso Neto (solo lo utilizable)",
                value = pesoNeto,
                onValueChange = { pesoNeto = it },
                placeholderText = "Peso Neto (solo lo utilizable)",
                keyboardType = KeyboardType.Number
            )
        }

        item {
            RetroButton(
                text = "Calcular Rendimiento",
                onClick = {
                    formatearCosto()
                    val c = costo.toDoubleOrNull() ?: 0.0
                    val pb = pesoBruto.toDoubleOrNull() ?: 0.0
                    val pn = pesoNeto.toDoubleOrNull() ?: 0.0

                    if (pb > 0 && pn > 0 && pn <= pb) {
                        val rend = (pn / pb) * 100
                        val cReal = c / pn
                        val mermaPeso = pb - pn
                        val porcentajeMerma = 100.0 - rend
                        val costoInicial = c / pb

                        rendimientoCalculado = rend
                        costoRealCalculado = cReal

                        analisisTextoCalculado = String.format(
                            Locale.US,
                            "💡 Análisis: Desperdicias %.2f %s de merma (%.1f%%). Tu costo sube de $%.2f a $%.2f por %s utilizable.",
                            mermaPeso, unidadSeleccionada, porcentajeMerma, costoInicial, cReal, unidadSeleccionada
                        )
                        mensajeError = ""
                    } else {
                        mensajeError = "P. Neto debe ser menor o igual a P. Bruto."
                        rendimientoCalculado = null
                        costoRealCalculado = null
                        analisisTextoCalculado = ""
                    }
                },
                backgroundColor = Color(0xFF6B21A8)
            )
        }

        if (mensajeError.isNotEmpty()) {
            item {
                Text(text = mensajeError, color = Color.Red, fontSize = 12.sp)
            }
        }

        if (rendimientoCalculado != null && costoRealCalculado != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = (-2).dp, y = (-2).dp)
                            .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp))
                            .border(1.5.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Resultado: ${if (nombre.isEmpty()) "Ingrediente" else nombre}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Rendimiento: ${String.format(Locale.US, "%.1f%%", rendimientoCalculado)}",
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Costo Real: $${String.format(Locale.US, "%.2f", costoRealCalculado)} / $unidadSeleccionada",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B21A8),
                            fontSize = 14.sp
                        )
                        if (analisisTextoCalculado.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = analisisTextoCalculado, fontSize = 12.sp, color = Color.DarkGray)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        RetroButtonSmall(
                            text = "Guardar Insumo",
                            onClick = {
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
                                        costoReal = costoRealCalculado!!,
                                        analisisResumen = analisisTextoCalculado
                                    )
                                )

                                nombre = ""
                                costo = ""
                                pesoBruto = ""
                                pesoNeto = ""
                                rendimientoCalculado = null
                                costoRealCalculado = null
                                analisisTextoCalculado = ""
                            }
                        )
                    }
                }
            }
        }

        if (listaIngredientes.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Black)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mis Insumos Registrados",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            items(listaIngredientes, key = { it.id }) { ing ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = (-2).dp, y = (-2).dp)
                            .background(Color(0xFFFAF7F0), shape = RoundedCornerShape(8.dp))
                            .border(1.5.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ing.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Rendimiento: ${String.format(Locale.US, "%.1f%%", ing.rendimiento)}", fontSize = 12.sp
