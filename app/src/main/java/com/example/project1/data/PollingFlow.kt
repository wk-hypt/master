package com.example.project1.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> pollingFlow(intervalMillis: Long = 3000L, fetch: suspend () -> T): Flow<T> = flow {
    while (true) {
        emit(fetch())
        delay(intervalMillis)
    }
}