package com.example.calculator

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data object Clear : CalculatorAction()
    data object Delete : CalculatorAction()
    data object Decimal : CalculatorAction()
    data object Calculate : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    data object Percentage : CalculatorAction()
    data object SquareRoot : CalculatorAction()
    data object Power : CalculatorAction()
    data object ToggleHistory : CalculatorAction()
    data object MemoryAdd : CalculatorAction()
    data object MemorySubtract : CalculatorAction()
    data object MemoryRecall : CalculatorAction()
    data object MemoryClear : CalculatorAction()
}

sealed class CalculatorOperation(val symbol: String) {
    data object Add : CalculatorOperation("+")
    data object Subtract : CalculatorOperation("-")
    data object Multiply : CalculatorOperation("×")
    data object Divide : CalculatorOperation("÷")
    data object Power : CalculatorOperation("^")
}

data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: CalculatorOperation? = null,
    val history: List<String> = emptyList(),
    val memory: Double = 0.0,
    val showHistory: Boolean = false
)
