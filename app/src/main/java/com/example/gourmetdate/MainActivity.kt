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
                            
