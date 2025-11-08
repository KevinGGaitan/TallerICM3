package com.example.tallericm3.ui.screens.components

// Este componente muestra una línea de texto como
// “¿Ya tienes cuenta? Ingreso” o “¿No tienes cuenta? Registro”
// donde una palabra es clicable y navega entre pantallas (login/registro).

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tallericm3.navigation.Routes
import com.example.tallericm3.ui.theme.Secondary
import com.example.tallericm3.ui.theme.TextColor

@Composable
fun AccountQueryComponent(
    textQuery: String,           // Texto normal (ej. “¿Ya tienes cuenta?”)
    textClickable: String,       // Texto clicable (ej. “Ingreso”)
    navController: NavHostController // Controlador de navegación
) {
    // Crea un texto con estilo diferenciado para la parte clicable
    val annotatedString = buildAnnotatedString {
        // Parte normal del texto
        withStyle(style = SpanStyle(color = TextColor, fontSize = 15.sp)) {
            append(textQuery)
        }
        // Parte clicable (subrayable por color secundario)
        withStyle(style = SpanStyle(color = Secondary, fontSize = 15.sp)) {
            // Se añade una anotación para reconocer clics en esta palabra
            pushStringAnnotation(tag = textClickable, annotation = textClickable)
            append(textClickable)
        }
    }

    // Componente que permite clics en texto con anotaciones
    ClickableText(text = annotatedString, onClick = { offset ->
        // Busca si el clic ocurrió sobre alguna anotación
        annotatedString.getStringAnnotations(offset, offset)
            .firstOrNull()?.let { annotation ->
                // Según el texto clicado, redirige a la pantalla correspondiente
                when (annotation.item) {
                    "Ingreso" -> navController.navigate(Routes.Login.route)
                    "Registro" -> navController.navigate(Routes.Register.route)
                }
            }
    })
}
