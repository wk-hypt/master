package com.example.project1.common

import com.example.project1.data.model.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormatter by lazy {
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
}

fun Long.toFormattedDate(): String = dateFormatter.format(Date(this))

fun TaskEntity.normalizedStatusText(): String = when {
    status.equals("Approved", ignoreCase = true) -> "Approved"
    status.equals("Pending", ignoreCase = true) -> "Pending Approval"
    else -> "In Progress"
}

val TaskEntity.isApproved: Boolean
    get() = status.equals("Approved", ignoreCase = true)

val TaskEntity.isPending: Boolean
    get() = status.equals("Pending", ignoreCase = true)

val TaskEntity.isTargetReached: Boolean
    get() = currentQuantity >= taskQuantity