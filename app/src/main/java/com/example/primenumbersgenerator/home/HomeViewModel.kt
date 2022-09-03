package com.example.primenumbersgenerator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

// ViewModel for Home Fragment
// Handles the inputs and verifies their usability for calculation
class HomeViewModel: ViewModel() {
    protected val _finish: MutableSharedFlow<Unit> = MutableSharedFlow()
    val finish: SharedFlow<Unit> = _finish

    protected val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error

    // Prevents blank inputs
    fun nullWarning() {
        viewModelScope.launch {
            _error.emit("Input 1 number in each field")
        }
    }

    // Ensures the range is in the rightward direction of a number line
    fun generate(start: Int, end: Int) {
        viewModelScope.launch {
            if (start > end) { // Failure
                _error.emit("Starting number cannot be greater than ending number")
            } else { // Success
                _finish.emit(Unit)
            }
        }
    }
}