package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel: CalculatorViewModel by viewModels()
        
        setContent {
            CalculatorTheme {
                val state = viewModel.state
                CalculatorScreen(
                    state = state,
                    onAction = viewModel::onAction
                )
            }
        }
    }
}
