package com.example.project1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDAO {
    // product

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: LuckinProductEntity)

    @Update
    suspend fun updateProduct(product: LuckinProductEntity)

    @Delete
    suspend fun deleteProduct(product: LuckinProductEntity)

    // bestseller
    @Query("SELECT * from luckin_products WHERE isBestSeller = 1 ORDER BY id DESC")
    fun getBestSellersStream(): Flow<List<LuckinProductEntity>>

    // check id
    @Query("SELECT * from luckin_products WHERE id = :id")
    fun getProductStream(id: Int): Flow<LuckinProductEntity>
}