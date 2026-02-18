package com.example.calculator

import android.os.Build
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
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
    val view = LocalView.current
    
    // Add keyboard support
    DisposableEffect(Unit) {
        val listener = android.view.View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_0 -> onAction(CalculatorAction.Number(0))
                    KeyEvent.KEYCODE_1 -> onAction(CalculatorAction.Number(1))
                    KeyEvent.KEYCODE_2 -> onAction(CalculatorAction.Number(2))
                    KeyEvent.KEYCODE_3 -> onAction(CalculatorAction.Number(3))
                    KeyEvent.KEYCODE_4 -> onAction(CalculatorAction.Number(4))
                    KeyEvent.KEYCODE_5 -> onAction(CalculatorAction.Number(5))
                    KeyEvent.KEYCODE_6 -> onAction(CalculatorAction.Number(6))
                    KeyEvent.KEYCODE_7 -> onAction(CalculatorAction.Number(7))
                    KeyEvent.KEYCODE_8 -> onAction(CalculatorAction.Number(8))
                    KeyEvent.KEYCODE_9 -> onAction(CalculatorAction.Number(9))
                    KeyEvent.KEYCODE_PERIOD -> onAction(CalculatorAction.Decimal)
                    KeyEvent.KEYCODE_PLUS -> onAction(CalculatorAction.Operation(CalculatorOperation.Add))
                    KeyEvent.KEYCODE_MINUS -> onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
                    KeyEvent.KEYCODE_STAR -> onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
                    KeyEvent.KEYCODE_SLASH -> onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
                    KeyEvent.KEYCODE_ENTER -> onAction(CalculatorAction.Calculate)
                    KeyEvent.KEYCODE_DEL -> onAction(CalculatorAction.Delete)
                    KeyEvent.KEYCODE_C -> onAction(CalculatorAction.Clear)
                    else -> return@OnKeyListener false
                }
                true
            } else {
                false
            }
        }
        view.setOnKeyListener(listener)
        onDispose {
            view.setOnKeyListener(null)
        }
    }
    
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
            // Memory Display
            if(state.memory != 0.0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Memory: ${if (state.memory % 1.0 == 0.0) state.memory.toLong() else state.memory}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
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
                "AC", "⌫", "√", "%",
                "7", "8", "9", "÷",
                "4", "5", "6", "×",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "^", "M+", "M-", "MR", "MC", "History"
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                content = {
                    items(buttons) { btn ->
                        if (btn.length <= 3 || btn == "History") {
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
                }
            )
        }
        
        // History Panel
        if(state.showHistory) {
            HistoryPanel(
                history = state.history,
                onDismiss = { onAction(CalculatorAction.ToggleHistory) }
            )
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
        "^" -> CalculatorAction.Power
        "=" -> CalculatorAction.Calculate
        "." -> CalculatorAction.Decimal
        "%" -> CalculatorAction.Percentage
        "√" -> CalculatorAction.SquareRoot
        "M+" -> CalculatorAction.MemoryAdd
        "M-" -> CalculatorAction.MemorySubtract
        "MR" -> CalculatorAction.MemoryRecall
        "MC" -> CalculatorAction.MemoryClear
        "History" -> CalculatorAction.ToggleHistory
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
    // Determine colors based on button type
    val containerColor = when(symbol) {
        "AC", "⌫" -> MaterialTheme.colorScheme.tertiaryContainer
        "÷", "×", "-", "+", "^" -> MaterialTheme.colorScheme.secondaryContainer
        "=" -> MaterialTheme.colorScheme.primary
        "%", "√", "M+", "M-", "MR", "MC", "History" -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surfaceVariant // Numbers and decimal
    }

    val contentColor = when(symbol) {
        "AC", "⌫" -> MaterialTheme.colorScheme.onTertiaryContainer
        "÷", "×", "-", "+", "^" -> MaterialTheme.colorScheme.onSecondaryContainer
        "=" -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .clickable { onClick() }
    ) {
        Text(
            text = symbol,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}

// --- History Panel ---
@Composable
fun HistoryPanel(
    history: List<String>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f))
            .clickable { onDismiss() }
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.8f)
                .padding(16.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text("×", fontSize = 20.sp)
                    }
                }
                
                if(history.isEmpty()) {
                    Text(
                        "No history yet",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(history.size) { index ->
                            Text(
                                text = history[index],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
        }
    }
}
