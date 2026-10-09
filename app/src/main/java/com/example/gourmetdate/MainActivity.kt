package com.example.gourmetdate

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

enum class Seccion(val titulo: String, val ruta: String, val icono: String) {
    ESCRITORIO("Escritorio Principal", "/C/GOURMETDATE/DESKTOP/", "🖥️"),
    INGREDIENTES("Catálogo de Ingredientes", "/C/GOURMETDATE/INGREDIENTES/", "📁"),
    RECETAS("Gestión de Recetas", "/C/GOURMETDATE/RECETAS/", "📄"),
    PROVEEDORES("Directorio de Proveedores", "/C/GOURMETDATE/PROVEEDORES/", "📦"),
    ORDENES("Órdenes de Compra", "/C/GOURMETDATE/ORDENES/", "📋"),
    MERMAS("Ingredientes & Mermas", "/C/GOURMETDATE/MERMAS/", "🧪")
}

data class Ingrediente(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nombre: String,
    val categoria: String = "General",
    val costo: Double,
    val pesoBruto: Double,
    val pesoNeto: Double,
    val unidad: String,
    val proveedor: String = "Sin proveedor",
    val rendimiento: Double,
    val costoReal: Double,
    val analisisResumen: String
)

data class IngredienteReceta(
    val ingredienteId: String,
    val nombre: String,
    val cantidad: Double,
    val unidad: String,
    val costoCalculado: Double
)

data class Receta(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nombre: String,
    val categoria: String,
    val porciones: Int,
    val tiempoMinutos: Int,
    val descripcion: String,
    val ingredientes: List<IngredienteReceta>,
    val costoTotal: Double,
    val costoPorPorcion: Double
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
    var seccionActual by remember { mutableStateOf(Seccion.INGREDIENTES) }

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("gourmetdate_prefs", Context.MODE_PRIVATE) }
    val gson = remember { Gson() }

    val listaIngredientes = remember {
        val json = sharedPreferences.getString("lista_ingredientes", null)
        val listType = object : TypeToken<ArrayList<Ingrediente>>() {}.type
        val savedList: ArrayList<Ingrediente>? = if (json != null) gson.fromJson(json, listType) else null
        mutableStateListOf<Ingrediente>().apply {
            if (savedList != null) addAll(savedList)
        }
    }

    val listaRecetas = remember {
        val json = sharedPreferences.getString("lista_recetas", null)
        val listType = object : TypeToken<ArrayList<Receta>>() {}.type
        val savedList: ArrayList<Receta>? = if (json != null) gson.fromJson(json, listType) else null
        mutableStateListOf<Receta>().apply {
            if (savedList != null) addAll(savedList)
        }
    }

    fun guardarIngredientes() {
        val json = gson.toJson(listaIngredientes.toList())
        sharedPreferences.edit().putString("lista_ingredientes", json).apply()
    }

    fun guardarRecetas() {
        val json = gson.toJson(listaRecetas.toList())
        sharedPreferences.edit().putString("lista_recetas", json).apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5DFEE))
    ) {
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
                        Box(modifier = Modifier.size(10.dp).background(Color.White, RoundedCornerShape(50)))
                        Box(modifier = Modifier.size(10.dp).background(Color.LightGray, RoundedCornerShape(50)))
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFFE81123), RoundedCornerShape(50)))
                    }
                }

                when (seccionActual) {
                    Seccion.INGREDIENTES -> CatalogoIngredientesRetro(
                        listaIngredientes = listaIngredientes,
                        onGuardarCambios = { guardarIngredientes() },
                        onIrAMermas = { seccionActual = Seccion.MERMAS }
                    )
                    Seccion.MERMAS -> IngredientesFormularioRetro(
                        listaIngredientes = listaIngredientes,
                        onGuardarCambios = { guardarIngredientes() }
                    )
                    Seccion.RECETAS -> RecetasFormularioRetro(
                        listaIngredientes = listaIngredientes,
                        listaRecetas = listaRecetas,
                        onGuardarRecetas = { guardarRecetas() }
                    )
                    else -> SeccionEnConstruccion(seccion = seccionActual)
                }
            }
        }

        RetroBottomDock(
            seccionActual = seccionActual,
            onSeccionSeleccionada = { seccionActual = it }
        )
    }
}

// =====================================================================
// MÓDULO CATÁLOGO DE INGREDIENTES (DISEÑO FIEL A REFERENCIAS)
// =====================================================================

@Composable
fun CatalogoIngredientesRetro(
    listaIngredientes: MutableList<Ingrediente>,
    onGuardarCambios: () -> Unit,
    onIrAMermas: () -> Unit
) {
    var nombreIngrediente by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var unidadSeleccionada by remember { mutableStateOf("kg") }
    var menuUnidadesExpandido by remember { mutableStateOf(false) }
    var costoCompra by remember { mutableStateOf("") }
    var pesoBruto by remember { mutableStateOf("") }
    var pesoNeto by remember { mutableStateOf("") }
    var proveedorSeleccionado by remember { mutableStateOf("Sin proveedor") }
    var menuProveedorExpandido by remember { mutableStateOf(false) }

    var busquedaNombre by remember { mutableStateOf("") }
    var filtroCategoria by remember { mutableStateOf("Todas las categorías") }
    var menuFiltroCategoriaExpandido by remember { mutableStateOf(false) }

    val unidadesList = listOf("kg", "g", "lb", "oz", "ml", "L")
    val proveedoresList = listOf("Sin proveedor", "Distribuidora Central", "Mercado Local", "Proveedor Carnes")

    val categoriasDisponibles = listOf("Todas las categorías") + listaIngredientes.map { it.categoria }.distinct()

    val ingredientesFiltrados = listaIngredientes.filter { ing ->
        val coincideNombre = ing.nombre.contains(busquedaNombre, ignoreCase = true)
        val coincideCategoria = filtroCategoria == "Todas las categorías" || ing.categoria.equals(filtroCategoria, ignoreCase = true)
        coincideNombre && coincideCategoria
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- BLOQUE 1: NUEVO INGREDIENTE ---
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
                        .background(Color(0xFFFAF7F0), shape = RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    // Header Azul del Formulario
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0072C6), shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nuevo ingrediente",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(Color.White, RoundedCornerShape(50)))
                            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, RoundedCornerShape(50)))
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFE81123), RoundedCornerShape(50)))
                        }
                    }

                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RetroInputField(
                            label = "Nombre del ingrediente",
                            value = nombreIngrediente,
                            onValueChange = { nombreIngrediente = it },
                            placeholderText = "ej. Aguacate Hass"
                        )

                        RetroInputField(
                            label = "Categoría",
                            value = categoria,
                            onValueChange = { categoria = it },
                            placeholderText = "ej. Frutas y verduras"
                        )

                        // Selector de Unidad
                        Column {
                            Text("Unidad", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                RetroButtonSmall(
                                    text = "$unidadSeleccionada   ▼",
                                    onClick = { menuUnidadesExpandido = true }
                                )
                                DropdownMenu(
                                    expanded = menuUnidadesExpandido,
                                    onDismissRequest = { menuUnidadesExpandido = false }
                                ) {
                                    unidadesList.forEach { u ->
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

                        RetroInputField(
                            label = "Costo de compra ($)",
                            value = costoCompra,
                            onValueChange = { costoCompra = it },
                            placeholderText = "0.00",
                            keyboardType = KeyboardType.Number
                        )

                        RetroInputField(
                            label = "Peso bruto ($unidadSeleccionada)",
                            value = pesoBruto,
                            onValueChange = { pesoBruto = it },
                            placeholderText = "0.000",
                            keyboardType = KeyboardType.Number
                        )

                        RetroInputField(
                            label = "Peso neto ($unidadSeleccionada)",
                            value = pesoNeto,
                            onValueChange = { pesoNeto = it },
                            placeholderText = "0.000",
                            keyboardType = KeyboardType.Number
                        )

                        // Selector de Proveedor
                        Column {
                            Text("Proveedor (opcional)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                RetroButtonSmall(
                                    text = "$proveedorSeleccionado   ▼",
                                    onClick = { menuProveedorExpandido = true }
                                )
                                DropdownMenu(
                                    expanded = menuProveedorExpandido,
                                    onDismissRequest = { menuProveedorExpandido = false }
                                ) {
                                    proveedoresList.forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p) },
                                            onClick = {
                                                proveedorSeleccionado = p
                                                menuProveedorExpandido = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        RetroButton(
                            text = "Agregar ingrediente",
                            onClick = {
                                val c = costoCompra.toDoubleOrNull() ?: 0.0
                                val pb = pesoBruto.toDoubleOrNull() ?: 0.0
                                val pn = pesoNeto.toDoubleOrNull() ?: 0.0

                                if (nombreIngrediente.isNotBlank() && pb > 0 && pn > 0) {
                                    val rend = (pn / pb) * 100
                                    val cReal = c / pn
                                    val mermaPeso = pb - pn
                                    val pctMerma = 100.0 - rend

                                    val analisis = String.format(
                                        Locale.US,
                                        "Merma: %.2f %s (%.1f%%). Costo real: $%.2f / %s",
                                        mermaPeso, unidadSeleccionada, pctMerma, cReal, unidadSeleccionada
                                    )

                                    listaIngredientes.add(
                                        Ingrediente(
                                            nombre = nombreIngrediente,
                                            categoria = if (categoria.isBlank()) "General" else categoria,
                                            costo = c,
                                            pesoBruto = pb,
                                            pesoNeto = pn,
                                            unidad = unidadSeleccionada,
                                            proveedor = proveedorSeleccionado,
                                            rendimiento = rend,
                                            costoReal = cReal,
                                            analisisResumen = analisis
                                        )
                                    )
                                    onGuardarCambios()

                                    nombreIngrediente = ""
                                    categoria = ""
                                    costoCompra = ""
                                    pesoBruto = ""
                                    pesoNeto = ""
                                    proveedorSeleccionado = "Sin proveedor"
                                }
                            },
                            backgroundColor = Color(0xFF6B21A8)
                        )
                    }
                }
            }
        }

        // --- BLOQUE 2: INVENTARIO (X) ---
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
                        .background(Color(0xFFFAF7F0), shape = RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    // Header Azul del Inventario
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0072C6), shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Inventario (${listaIngredientes.size})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(Color.White, RoundedCornerShape(50)))
                            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, RoundedCornerShape(50)))
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFE81123), RoundedCornerShape(50)))
                        }
                    }

                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RetroInputField(
                            label = "Buscar por nombre",
                            value = busquedaNombre,
                            onValueChange = { busquedaNombre = it },
                            placeholderText = "ej. Aguacate"
                        )

                        // Filtro por Categoría
                        Column {
                            Text("Filtrar por categoría", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                RetroButtonSmall(
                                    text = "$filtroCategoria   ▼",
                                    onClick = { menuFiltroCategoriaExpandido = true }
                                )
                                DropdownMenu(
                                    expanded = menuFiltroCategoriaExpandido,
                                    onDismissRequest = { menuFiltroCategoriaExpandido = false }
                                ) {
                                    categoriasDisponibles.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                filtroCategoria = cat
                                                menuFiltroCategoriaExpandido = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                        if (ingredientesFiltrados.isEmpty()) {
                            Text(
                                text = if (listaIngredientes.isEmpty()) "No hay ingredientes registrados. Agrega el primero con el formulario de arriba." else "No se encontraron ingredientes para esa búsqueda.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            ingredientesFiltrados.forEach { ing ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black, shape = RoundedCornerShape(6.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(x = (-2).dp, y = (-2).dp)
                                            .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp))
                                            .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ing.nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Categoría: ${ing.categoria} | Prov: ${ing.proveedor}", fontSize = 11.sp, color = Color.DarkGray)
                                            Text(
                                                "Costo Real: $${String.format(Locale.US, "%.2f", ing.costoReal)} / ${ing.unidad} (Rend. ${String.format(Locale.US, "%.1f%%", ing.rendimiento)})",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF6B21A8),
                                                fontSize = 11.sp
                                            )
                                        }
                                        IconButton(onClick = {
                                            listaIngredientes.remove(ing)
                                            onGuardarCambios()
                                        }) {
                                            Text("🗑️", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Botón Amarillo Retro para abrir Mermas
                        RetroButton(
                            text = "Abrir Ingredientes & Mermas",
                            onClick = onIrAMermas,
                            backgroundColor = Color(0xFFFFD000)
                        )
                    }
                }
            }
        }
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
                    text = "⚙️ Módulo en desarrollo. Usa el Dock inferior para navegar.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

// =====================================================================
// MÓDULO DE RECETAS
// =====================================================================

@Composable
fun RecetasFormularioRetro(
    listaIngredientes: List<Ingrediente>,
    listaRecetas: MutableList<Receta>,
    onGuardarRecetas: () -> Unit
) {
    var nombreReceta by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var porciones by remember { mutableStateOf("4") }
    var tiempo by remember { mutableStateOf("30") }
    var descripcion by remember { mutableStateOf("") }

    val ingredientesAgregados = remember { mutableStateListOf<IngredienteReceta>() }
    var ingredienteSeleccionado by remember { mutableStateOf<Ingrediente?>(null) }
    var menuIngredientesExpandido by remember { mutableStateOf(false) }
    var cantidadIngrediente by remember { mutableStateOf("1") }

    var busquedaNombre by remember { mutableStateOf("") }

    val costoTotalReceta = ingredientesAgregados.sumOf { it.costoCalculado }
    val numPorciones = porciones.toIntOrNull() ?: 1
    val costoPorPorcion = if (numPorciones > 0) costoTotalReceta / numPorciones else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Nueva receta",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        item {
            RetroInputField(
                label = "Nombre de la receta",
                value = nombreReceta,
                onValueChange = { nombreReceta = it },
                placeholderText = "ej. Guacamole de la casa"
            )
        }

        item {
            RetroInputField(
                label = "Categoría",
                value = categoria,
                onValueChange = { categoria = it },
                placeholderText = "ej. Entradas"
            )
        }

        item {
            RetroInputField(
                label = "Porciones",
                value = porciones,
                onValueChange = { porciones = it },
                placeholderText = "4",
                keyboardType = KeyboardType.Number
            )
        }

        item {
            RetroInputField(
                label = "Tiempo (min)",
                value = tiempo,
                onValueChange = { tiempo = it },
                placeholderText = "30",
                keyboardType = KeyboardType.Number
            )
        }

        item {
            Column {
                Text(
                    text = "Descripción / preparación",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Black, shape = RoundedCornerShape(6.dp))
                ) {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        placeholder = { Text("Describe el procedimiento de la receta...", color = Color.Gray, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = (-2).dp, y = (-2).dp)
                            .background(Color(0xFFFAF7F0), shape = RoundedCornerShape(6.dp))
                    )
                }
            }
        }

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
                        .background(Color(0xFFFAF7F0), shape = RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ingredientes de la receta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(text = "Ingrediente", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        RetroButtonSmall(
                            text = ingredienteSeleccionado?.nombre ?: "Selecciona...",
                            onClick = { menuIngredientesExpandido = true }
                        )
                        DropdownMenu(
                            expanded = menuIngredientesExpandido,
                            onDismissRequest = { menuIngredientesExpandido = false }
                        ) {
                            if (listaIngredientes.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay insumos. Regístralos en MERMAS.") },
                                    onClick = { menuIngredientesExpandido = false }
                                )
                            } else {
                                listaIngredientes.forEach { ing ->
                                    DropdownMenuItem(
                                        text = { Text("${ing.nombre} ($${String.format(Locale.US, "%.2f", ing.costoReal)}/${ing.unidad})") },
                                        onClick = {
                                            ingredienteSeleccionado = ing
                                            menuIngredientesExpandido = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    RetroInputField(
                        label = "Cantidad",
                        value = cantidadIngrediente,
                        onValueChange = { cantidadIngrediente = it },
                        placeholderText = "1",
                        keyboardType = KeyboardType.Number
                    )

                    RetroButton(
                        text = "Añadir",
                        onClick = {
                            val ing = ingredienteSeleccionado
                            val cant = cantidadIngrediente.toDoubleOrNull() ?: 0.0
                            if (ing != null && cant > 0) {
                                val costoCalc = cant * ing.costoReal
                                ingredientesAgregados.add(
                                    IngredienteReceta(
                                        ingredienteId = ing.id,
                                        nombre = ing.nombre,
                                        cantidad = cant,
                                        unidad = ing.unidad,
                                        costoCalculado = costoCalc
                                    )
                                )
                                ingredienteSeleccionado = null
                                cantidadIngrediente = "1"
                            }
                        },
                        backgroundColor = Color(0xFFFACC15)
                    )

                    if (ingredientesAgregados.isEmpty()) {
                        Text(
                            text = "Añade al menos un ingrediente para guardar la receta.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Ingredientes agregados:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        ingredientesAgregados.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "• ${item.nombre}: ${item.cantidad} ${item.unidad} ($${String.format(Locale.US, "%.2f", item.costoCalculado)})",
                                    fontSize = 11.sp
                                )
                                IconButton(
                                    onClick = { ingredientesAgregados.remove(item) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Text("🗑️", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (ingredientesAgregados.isNotEmpty()) {
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
                            .background(Color(0xFFE0E7FF), shape = RoundedCornerShape(8.dp))
                            .border(1.5.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text("💡 Análisis de Costos", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Costo Total Receta: $${String.format(Locale.US, "%.2f", costoTotalReceta)}", fontSize = 12.sp)
                        Text(
                            "Costo Por Porción ($numPorciones): $${String.format(Locale.US, "%.2f", costoPorPorcion)}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B21A8),
                            fontSize = 13.sp
                        )
                        Text(
                            "Precio Venta Sugerido (30% costo): $${String.format(Locale.US, "%.2f", costoPorPorcion / 0.30)}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        item {
            RetroButton(
                text = "Crear receta",
                onClick = {
                    if (nombreReceta.isNotBlank() && ingredientesAgregados.isNotEmpty()) {
                        listaRecetas.add(
                            Receta(
                                nombre = nombreReceta,
                                categoria = if (categoria.isBlank()) "General" else categoria,
                                porciones = numPorciones,
                                tiempoMinutos = tiempo.toIntOrNull() ?: 0,
                                descripcion = descripcion,
                                ingredientes = ingredientesAgregados.toList(),
                                costoTotal = costoTotalReceta,
                                costoPorPorcion = costoPorPorcion
                            )
                        )
                        onGuardarRecetas()

                        nombreReceta = ""
                        categoria = ""
                        porciones = "4"
                        tiempo = "30"
                        descripcion = ""
                        ingredientesAgregados.clear()
                    }
                },
                backgroundColor = Color(0xFF6B21A8)
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black, shape = RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = (-2).dp, y = (-2).dp)
                        .background(Color(0xFFFAF7F0), shape = RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0072C6), shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recetario (${listaRecetas.size})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(Color.White, RoundedCornerShape(50)))
                            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, RoundedCornerShape(50)))
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFE81123), RoundedCornerShape(50)))
                        }
                    }

                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RetroInputField(
                            label = "Buscar por nombre",
                            value = busquedaNombre,
                            onValueChange = { busquedaNombre = it },
                            placeholderText = "ej. Guacamole"
                        )

                        val recetasFiltradas = listaRecetas.filter {
                            it.nombre.contains(busquedaNombre, ignoreCase = true)
                        }

                        if (recetasFiltradas.isEmpty()) {
                            Text(
                                text = "Todavía no hay recetas. Crea la primera con el formulario de arriba.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            recetasFiltradas.forEach { rec ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black, shape = RoundedCornerShape(6.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(x = (-2).dp, y = (-2).dp)
                                            .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp))
                                            .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(rec.nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Categoría: ${rec.categoria} | ${rec.porciones} porciones", fontSize = 11.sp, color = Color.DarkGray)
                                            Text(
                                                "Costo Total: $${String.format(Locale.US, "%.2f", rec.costoTotal)} ($${String.format(Locale.US, "%.2f", rec.costoPorPorcion)}/porción)",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF6B21A8),
                                                fontSize = 11.sp
                                            )
                                        }
                                        IconButton(onClick = {
                                            listaRecetas.remove(rec)
                                            onGuardarRecetas()
                                        }) {
                                            Text("🗑️", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// MÓDULO DE MERMAS E INGREDIENTES
// =====================================================================

@Composable
fun IngredientesFormularioRetro(
    listaIngredientes: MutableList<Ingrediente>,
    onGuardarCambios: () -> Unit
) {
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
                                onGuardarCambios()

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
                            Text("Rendimiento: ${String.format(Locale.US, "%.1f%%", ing.rendimiento)}", fontSize = 12.sp)
                            Text(
                                "Costo Real: $${String.format(Locale.US, "%.2f", ing.costoReal)} / ${ing.unidad}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B21A8),
                                fontSize = 12.sp
                            )
                        }

                        IconButton(onClick = {
                            listaIngredientes.remove(ing)
                            onGuardarCambios()
                        }) {
                            Text("🗑️", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// COMPONENTES RETRO PERSONALIZADOS
// =====================================================================

@Composable
fun RetroInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocusLost: () -> Unit = {}
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        RetroTextFieldRaw(
            value = value,
            onValueChange = onValueChange,
            placeholderText = placeholderText,
            keyboardType = keyboardType,
            onFocusLost = onFocusLost
        )
    }
}

@Composable
fun RetroTextFieldRaw(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocusLost: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black, shape = RoundedCornerShape(6.dp))
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholderText, color = Color.Gray, fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (-2).dp, y = (-2).dp)
                .background(Color(0xFFFAF7F0), shape = RoundedCornerShape(6.dp))
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && onFocusLost != null) {
                        onFocusLost()
                    }
                }
        )
    }
}

@Composable
fun RetroButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color(0xFF6B21A8)
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.Black, shape = RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (-3).dp, y = (-3).dp)
                .background(backgroundColor, shape = RoundedCornerShape(6.dp))
                .border(2.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun RetroButtonSmall(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.Black, shape = RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (-2).dp, y = (-2).dp)
                .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(6.dp))
                .border(1.5.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun RetroBottomDock(
    seccionActual: Seccion,
    onSeccionSeleccionada: (Seccion) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE5DFEE))
                .border(1.dp, Color.Black)
                .padding(vertical = 4.dp, horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockItem(
                icon = Seccion.ESCRITORIO.icono,
                label = "ESCRITORIO",
                isSelected = seccionActual == Seccion.ESCRITORIO,
                onClick = { onSeccionSeleccionada(Seccion.ESCRITORIO) }
            )
            DockItem(
                icon = Seccion.INGREDIENTES.icono,
                label = "INGREDIEN...",
                isSelected = seccionActual == Seccion.INGREDIENTES,
                onClick = { onSeccionSeleccionada(Seccion.INGREDIENTES) }
            )
            DockItem(
                icon = Seccion.RECETAS.icono,
                label = "RECETAS",
                isSelected = seccionActual == Seccion.RECETAS,
                onClick = { onSeccionSeleccionada(Seccion.RECETAS) }
            )
            DockItem(
                icon = Seccion.PROVEEDORES.icono,
                label = "PROVEEDOR...",
                isSelected = seccionActual == Seccion.PROVEEDORES,
                onClick = { onSeccionSeleccionada(Seccion.PROVEEDORES) }
            )
            DockItem(
                icon = Seccion.ORDENES.icono,
                label = "ÓRDENES",
                isSelected = seccionActual == Seccion.ORDENES,
                onClick = { onSeccionSeleccionada(Seccion.ORDENES) }
            )
            DockItem(
                icon = Seccion.MERMAS.icono,
                label = "MERMAS",
                isSelected = seccionActual == Seccion.MERMAS,
                onClick = { onSeccionSeleccionada(Seccion.MERMAS) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD000))
                .border(1.dp, Color.Black)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GOURMET STATUS",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Black
            )
            Text(
                text = "  |  SECCIÓN: ${seccionActual.name}",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Black
            )
        }
    }
}

@Composable
fun DockItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFFFFE600) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) Color.Black else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 3.dp, vertical = 2.dp)
    ) {
        Text(text = icon, fontSize = 14.sp)
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
    }
}
