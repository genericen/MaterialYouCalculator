package com.example.calculator

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Theme Wrapper ---
@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // Dynamic Color is available on Android 12+
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

// --- Main Screen ---
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display Area
            Text(
                text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                fontWeight = FontWeight.Light,
                fontSize = 60.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 60.sp,
                maxLines = 2
            )

            // Button Grid
            val buttons = listOf(
                "AC", "⌫", "", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "=", "" // Last empty string is filler if needed, handled in logic
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    content = {
                        items(buttons) { btn ->
                            CalculatorButton(
                                symbol = btn,
                                modifier = Modifier.aspectRatio(1f),
                                onClick = {
                                    val action = getActionFromSymbol(btn)
                                    if (action != null) onAction(action)
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

// --- Helper to Map String to Action ---
fun getActionFromSymbol(symbol: String): CalculatorAction? {
    return when(symbol) {
        "AC" -> CalculatorAction.Clear
        "⌫" -> CalculatorAction.Delete
        "÷" -> CalculatorAction.Operation(CalculatorOperation.Divide)
        "×" -> CalculatorAction.Operation(CalculatorOperation.Multiply)
        "-" -> CalculatorAction.Operation(CalculatorOperation.Subtract)
        "+" -> CalculatorAction.Operation(CalculatorOperation.Add)
        "=" -> CalculatorAction.Calculate
        "." -> CalculatorAction.Decimal
        "" -> null // Spacer
        else -> if(symbol.toIntOrNull() != null) CalculatorAction.Number(symbol.toInt()) else null
    }
}

// --- Custom Button Component ---
@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (symbol.isEmpty()) {
        Box(modifier = modifier) // Empty spacer
        return
    }

    // Determine colors based on button type
    val containerColor = when(symbol) {
        "AC", "⌫" -> MaterialTheme.colorScheme.tertiaryContainer
        "÷", "×", "-", "+" -> MaterialTheme.colorScheme.secondaryContainer
        "=" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant // Numbers
    }

    val contentColor = when(symbol) {
        "AC", "⌫" -> MaterialTheme.colorScheme.onTertiaryContainer
        "÷", "×", "-", "+" -> MaterialTheme.colorScheme.onSecondaryContainer
        "=" -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape) // Rounded corners (Circle for 1:1 aspect ratio)
            .background(containerColor)
            .clickable { onClick() }
    ) {
        Text(
            text = symbol,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}
