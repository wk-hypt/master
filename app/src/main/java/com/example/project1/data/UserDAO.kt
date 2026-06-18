package com.example.project1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * from user WHERE id = :id")
    fun getUser(id: Int): Flow<User>

    @Query("SELECT role FROM user WHERE name = :username AND password = :password")
    fun getUserRole(username: String, password: String): String?

    @Query("SELECT * from user ORDER BY name ASC")
    suspend fun getAllUsers(): List<User>
}