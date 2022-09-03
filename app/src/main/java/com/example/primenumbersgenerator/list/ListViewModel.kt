package com.example.primenumbersgenerator.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.floor

// ViewModel for processing the start and end ranges from the Home Fragment.
// Also contains the states for loading and signal for the List Fragment to display the
// "No Prime Numbers found" message
class ListViewModel : ViewModel() {
    // Check for empty prime numbers array
    protected val _emptyArr: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val emptyArr: SharedFlow<Boolean> = _emptyArr

    // Loading state
    protected val _loading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val loading: SharedFlow<Boolean> = _loading

    // Function containing the algorithm for creating a list of prime numbers
    suspend fun generateNumbers(start: Int, end: Int): MutableList<Int> {
        // Array to contain integers within the range of (start, end)
        val nums = mutableListOf<Int>()
        // Array of prime numbers to be returned as result
        val primeNums = mutableListOf<Int>()
        // Using Mutex() to sequence the processes in an orderly fashion
        val mutex = Mutex()

        // Initialise array of integers within the range
        for (i in start..end) {
            nums.add(i)
        }

        // If number range is within 1000, just use 1 coroutine
        if (end - start < 1000) {
            coroutineScope {
                async(Dispatchers.Default) {

                    // If there are prime numbers in the range, add them into primeNums array
                    for (i in nums) {
                        if (isPrimeNum(i)) {
                            mutex.withLock {
                                primeNums.add(i)
                            }
                        }
                    }

//                Log.i("listviewmodel", primeNums.toString())

                    viewModelScope.launch {
                        // Checks if there are prime numbers in the array
                        if (primeNums.isEmpty()) {
                            // For (0, 0) range
                            Log.i("listviewmodel", "1")
                            _emptyArr.emit(true)
                            _loading.emit(false)
                        } else {
                            // Non (0, 0) range
                            Log.i("listviewmodel", "2")
                            _loading.emit(false)
                        }
                    }
                }
            }
        // If number range is more than 1000, split the task into different subtasks
        } else {
            val length = end - start
            val numCoroutines = floor(length.toFloat() / 1000).toInt()

            coroutineScope {
                // Calculate remainder numbers within length 1000 in one coroutine
                async(Dispatchers.Default) {
                    for (i in (numCoroutines * 1000)..(nums.size - 1)) {
                        if (isPrimeNum(i)) {
                            mutex.withLock {
                                primeNums.add(nums[i])
                            }
                        }
                    }
                }
                // Calculate for groups of 1000 in multiple coroutines
                for (i in 1..numCoroutines) {
                    for (j in ((i * 1000) - 1000)..(i * 1000)) {
                        async(Dispatchers.Default) {
                            if (isPrimeNum(nums[j])) {
                                mutex.withLock {
                                    primeNums.add(nums[j])
                                }
                            }
                        }
                    }
                }

                // This block helps complete the loading process but can cause crashes due to the
                // different timing of async processes
//                async(Dispatchers.Default) {
//                    // Sort the prime numbers array due to asynchronous additions of numbers
//                    // resulting in disorderly sequencing
//                    primeNums.sort()
//
//                    // Finish loading
//                    viewModelScope.launch {
//                        _loading.emit(false)
//                    }
//                }
            }

            coroutineScope {
                async(Dispatchers.Default) {
                    // Sort the prime numbers array due to asynchronous additions of numbers
                    // resulting in disorderly sequencing
                    primeNums.sort()

                    // Finish loading
                    viewModelScope.launch {
                        _loading.emit(false)
                    }
                }
            }
        }

//        Log.i("listviewmodel", primeNums.toString())
        return primeNums
    }

    // Function for identifying if a number is a prime number
    fun isPrimeNum(num: Int): Boolean {
        if (num < 2) {
            return false
        }

        val half = num / 2

        for (i in 2..half) {
            if (num % i == 0) {
                return false
            }
        }

        return true
    }
}