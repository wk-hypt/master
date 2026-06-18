package com.example.project1.data

import kotlinx.coroutines.flow.Flow
import com.example.project1.data.User

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface UserRespository {

    suspend fun getAllUsersStream(): List<User>

    fun getUserStream(id: Int): Flow<User?>

    suspend fun insertUser(user: User)

    suspend fun deleteUser(user: User)

    suspend fun updateUser(user: User)
}