package com.example.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt
import kotlin.math.pow

class CalculatorViewModel : ViewModel() {

    companion object {
        private const val MAX_NUMBER_LENGTH = 8
    }

    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorAction) {
        when(action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> state = CalculatorState(history = state.history, memory = state.memory)
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
            is CalculatorAction.Percentage -> applyPercentage()
            is CalculatorAction.SquareRoot -> applySquareRoot()
            is CalculatorAction.Power -> enterOperation(CalculatorOperation.Power)
            is CalculatorAction.ToggleHistory -> state = state.copy(showHistory = !state.showHistory)
            is CalculatorAction.MemoryAdd -> addToMemory()
            is CalculatorAction.MemorySubtract -> subtractFromMemory()
            is CalculatorAction.MemoryRecall -> recallMemory()
            is CalculatorAction.MemoryClear -> state = state.copy(memory = 0.0)
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if(state.number1.isNotBlank()) {
            state = state.copy(operation = operation)
        }
    }

    private fun performDeletion() {
        when {
            state.number2.isNotBlank() -> {
                state = state.copy(
                    number2 = state.number2.dropLast(1)
                )
            }
            state.operation != null -> {
                state = state.copy(
                    operation = null
                )
            }
            state.number1.isNotBlank() -> {
                state = state.copy(
                    number1 = state.number1.dropLast(1)
                )
            }
        }
    }

    private fun performCalculation() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        if(number1 != null && number2 != null && state.operation != null) {
            // Handle division by zero
            if(state.operation is CalculatorOperation.Divide && number2 == 0.0) {
                state = CalculatorState(
                    number1 = "Error",
                    number2 = "",
                    operation = null,
                    history = state.history,
                    memory = state.memory
                )
                return
            }
            
            val result = when(state.operation) {
                is CalculatorOperation.Add -> number1 + number2
                is CalculatorOperation.Subtract -> number1 - number2
                is CalculatorOperation.Multiply -> number1 * number2
                is CalculatorOperation.Divide -> number1 / number2
                is CalculatorOperation.Power -> number1.pow(number2)
                null -> return
            }
            
            // Format to remove unnecessary decimal .0
            val formattedResult = if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                result.toString()
            }
            
            // Add to history
            val historyEntry = "$number1 ${state.operation.symbol} $number2 = $formattedResult"
            val newHistory = state.history + historyEntry
            
            state = CalculatorState(
                number1 = formattedResult,
                number2 = "",
                operation = null,
                history = newHistory,
                memory = state.memory
            )
        }
    }

    private fun enterDecimal() {
        if(state.operation == null && !state.number1.contains(".") && state.number1.isNotBlank()) {
            state = state.copy(
                number1 = state.number1 + "."
            )
            return
        }
        if(state.operation != null && !state.number2.contains(".") && state.number2.isNotBlank()) {
            state = state.copy(
                number2 = state.number2 + "."
            )
        }
    }

    private fun enterNumber(number: Int) {
        if(state.operation == null) {
            if(state.number1.length >= MAX_NUMBER_LENGTH) return // Limit length
            state = state.copy(
                number1 = state.number1 + number
            )
            return
        }
        if(state.number2.length >= MAX_NUMBER_LENGTH) return
        state = state.copy(
            number2 = state.number2 + number
        )
    }

    private fun applyPercentage() {
        val currentNumber = if(state.operation == null) {
            state.number1.toDoubleOrNull() ?: return
        } else {
            state.number2.toDoubleOrNull() ?: return
        }
        
        val percentage = currentNumber / 100.0
        val formatted = if (percentage % 1.0 == 0.0) {
            percentage.toLong().toString()
        } else {
            percentage.toString()
        }
        
        if(state.operation == null) {
            state = state.copy(number1 = formatted)
        } else {
            state = state.copy(number2 = formatted)
        }
    }

    private fun applySquareRoot() {
        val currentNumber = if(state.operation == null) {
            state.number1.toDoubleOrNull() ?: return
        } else {
            state.number2.toDoubleOrNull() ?: return
        }
        
        if(currentNumber < 0) {
            return // Can't take square root of negative
        }
        
        val sqrtResult = sqrt(currentNumber)
        val formatted = if (sqrtResult % 1.0 == 0.0) {
            sqrtResult.toLong().toString()
        } else {
            sqrtResult.toString()
        }
        
        if(state.operation == null) {
            state = state.copy(number1 = formatted)
        } else {
            state = state.copy(number2 = formatted)
        }
    }

    private fun addToMemory() {
        val currentNumber = if(state.operation == null) {
            state.number1.toDoubleOrNull() ?: return
        } else {
            state.number2.toDoubleOrNull() ?: return
        }
        state = state.copy(memory = state.memory + currentNumber)
    }

    private fun subtractFromMemory() {
        val currentNumber = if(state.operation == null) {
            state.number1.toDoubleOrNull() ?: return
        } else {
            state.number2.toDoubleOrNull() ?: return
        }
        state = state.copy(memory = state.memory - currentNumber)
    }

    private fun recallMemory() {
        val formatted = if (state.memory % 1.0 == 0.0) {
            state.memory.toLong().toString()
        } else {
            state.memory.toString()
        }
        
        if(state.operation == null) {
            state = state.copy(number1 = formatted)
        } else {
            state = state.copy(number2 = formatted)
        }
    }
}
