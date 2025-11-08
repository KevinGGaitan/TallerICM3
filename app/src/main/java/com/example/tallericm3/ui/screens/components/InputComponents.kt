package com.example.tallericm3.ui.screens.components

// Este archivo define componentes reutilizables para entradas de texto y un checkbox en Jetpack Compose.
// Incluye campos personalizados para texto normal, contraseñas con alternancia de visibilidad y una casilla básica.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tallericm3.ui.theme.AccentColor
import com.example.tallericm3.ui.theme.BgColor
import com.example.tallericm3.ui.theme.Primary
import com.example.tallericm3.ui.theme.TextColor


// Muestra un campo de texto con ícono al inicio, diseñado con el estilo del tema de la app.
@Composable
fun MyTextFieldComponent(
    labelValue: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        label = { Text(text = labelValue) },
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = AccentColor, // Color del borde al enfocar
            focusedLabelColor = AccentColor, // Color del texto de etiqueta al enfocar
            cursorColor = Primary, // Color del cursor al escribir
            unfocusedContainerColor = BgColor, // Fondo cuando no está enfocado
            focusedLeadingIconColor = AccentColor // Color del ícono al enfocar
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, // Usa la forma por defecto del tema
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = labelValue
            )
        },
        keyboardOptions = KeyboardOptions.Default
    )
}


// Campo de texto especializado para contraseñas, con botón que alterna visibilidad del texto.
@Composable
fun PasswordTextFieldComponent(
    labelValue: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) } // Estado interno para mostrar/ocultar texto

    OutlinedTextField(
        label = { Text(text = labelValue) },
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = AccentColor,
            focusedLabelColor = AccentColor,
            cursorColor = Primary,
            unfocusedContainerColor = BgColor,
            focusedLeadingIconColor = AccentColor
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = labelValue
            )
        },
        trailingIcon = { // Ícono al final que alterna visibilidad del texto
            val iconImage =
                if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
            val description = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"

            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) { // Alterna el estado
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Teclado específico
        visualTransformation = if (isPasswordVisible) VisualTransformation.None // Muestra texto plano
        else PasswordVisualTransformation() // Oculta el texto con puntos
    )
}


// Casilla de verificación simple centrada, útil para aceptar términos o confirmar opciones.
@Composable
fun CheckboxComponent() {
    var isChecked by remember { mutableStateOf(false) } // Estado del checkbox
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it } // Actualiza estado cuando se toca
        )
    }
}
