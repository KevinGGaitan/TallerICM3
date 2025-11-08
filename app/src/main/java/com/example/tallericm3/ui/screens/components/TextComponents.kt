package com.example.tallericm3.ui.screens.components

// Archivo que contiene componentes de texto reutilizables (títulos, subtítulos y textos clicables).
// Se usan en diferentes pantallas de la app para mantener coherencia visual.

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tallericm3.ui.theme.Secondary
import com.example.tallericm3.ui.theme.TextColor


@Composable
fun NormalTextComponent(value: String) {
    // Texto base centrado, usado para subtítulos o mensajes secundarios
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = TextColor,
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(value: String) {
    // Texto grande y en negrita, usado para títulos principales
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ),
        color = TextColor,
        textAlign = TextAlign.Center
    )
}


@Composable
fun ClickableTextComponent() {
    // Texto compuesto con partes clicables (ubicación y estado de conexión)
    val initialText = "Para continuar con tu registro, necesitamos tu "
    val privacyPolicyText = "ubicacion"
    val andText = " y "
    val termOfUseText = "estado de conexion"

    // Crea un texto anotado (AnnotatedString) que permite reconocer clics en segmentos
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = TextColor)) {
            append(initialText)
        }
        withStyle(style = SpanStyle(color = Secondary)) {
            // pushStringAnnotation añade una etiqueta para detectar clics en este fragmento
            pushStringAnnotation(tag = privacyPolicyText, annotation = privacyPolicyText)
            append(privacyPolicyText)
        }
        withStyle(style = SpanStyle(color = TextColor)) {
            append(andText)
        }
        withStyle(style = SpanStyle(color = Secondary)) {
            pushStringAnnotation(tag = termOfUseText, annotation = termOfUseText)
            append(termOfUseText)
        }
        append(".")
    }

    // ClickableText detecta clics en texto con anotaciones
    ClickableText(text = annotatedString, onClick = {
        annotatedString.getStringAnnotations(it, it)
            .firstOrNull()?.also { annotation ->
                // Log para depuración: muestra cuál parte fue presionada
                Log.d("ClickableTextComponent", "You have Clicked ${annotation.tag}")
            }
    })
}
