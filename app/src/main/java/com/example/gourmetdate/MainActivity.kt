package com.example.gourmetdate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GourmetDateApp()
        }
    }
}

@Composable
fun GourmetDateApp() {
    var currentScreen by remember { mutableStateOf("splash") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        when (currentScreen) {
            "splash" -> SplashScreen(onTimeout = { currentScreen = "home" })
            "home" -> HomeScreen(onNavigate = { screen -> currentScreen = screen })
            "recipes" -> ModulePlaceholderScreen(
                title = "📖 Recetario Base", 
                description = "Aquí podrás crear, editar, desglosar y archivar tus recetas.", 
                onBack = { currentScreen = "home" }
            )
            "ingredients" -> ModulePlaceholderScreen(
                title = "🥑 Ingredientes & Mermas", 
                description = "Aquí registrarás el precio bruto y el % de merma por ingrediente.", 
                onBack = { currentScreen = "home" }
            )
            "costing" -> ModulePlaceholderScreen(
                title = "💰 Calculadora de Costos", 
                description = "Aquí calcularás el costo total de producción y el precio de venta sugerido.", 
                onBack = { currentScreen = "home" }
            )
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "gourmetDate",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFB74D)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gestión & Costeo Gastronómico",
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "gourmetDate",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFB74D)
        )
        Text(
            text = "Panel de Control",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        MenuCard(title = "📖 Recetario", subtitle = "Crear, desglosar y archivar recetas") {
            onNavigate("recipes")
        }
        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(title = "🥑 Ingredientes & Mermas", subtitle = "Costos brutos y % de desperdicio") {
            onNavigate("ingredients")
        }
        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(title = "💰 Calculadora de Costos", subtitle = "Costo de producción vs. precio de venta") {
            onNavigate("costing")
        }
    }
}

@Composable
fun MenuCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ModulePlaceholderScreen(title: String, description: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C))
            ) {
                Text("← Volver al Menú", color = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB74D))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = description, fontSize = 16.sp, color = Color.LightGray)
        }
    }
}
