package com.example.project1.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> pollingFlow(intervalMillis: Long = 2000L, fetch: suspend () -> T): Flow<T> = flow {
    while (true) {
        try {
            emit(fetch())
        } catch (_: Exception) {
        }
        delay(intervalMillis)
    }
}