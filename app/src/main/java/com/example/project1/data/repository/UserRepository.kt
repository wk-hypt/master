package com.example.project1.data.repository

import com.example.project1.data.DAO.UserDAO
import com.example.project1.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getWeeklyLeaderboardStream(): Flow<List<UserEntity>>
    fun getPlasticsLeaderboardStream(): Flow<List<UserEntity>>
    fun getUserStream(studentId: String): Flow<UserEntity?>
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun getUserById(studentId: String): UserEntity?
}

class OfflineUserRepository(private val userDAO: UserDAO) : UserRepository {
    override fun getWeeklyLeaderboardStream(): Flow<List<UserEntity>> = userDAO.getWeeklyLeaderboardStream()
    override fun getPlasticsLeaderboardStream(): Flow<List<UserEntity>> = userDAO.getPlasticsLeaderboardStream()
    override fun getUserStream(studentId: String): Flow<UserEntity?> = userDAO.getUserStream(studentId)
    override suspend fun insertUser(user: UserEntity) = userDAO.insertUser(user)
    override suspend fun updateUser(user: UserEntity) = userDAO.updateUser(user)
    override suspend fun getUserById(studentId: String): UserEntity? = userDAO.getUserById(studentId)
}