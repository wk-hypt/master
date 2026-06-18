package com.example.project1.data

import kotlinx.coroutines.flow.Flow

interface ProductRespository {

    fun getBestSellersStream(): Flow<List<LuckinProductEntity>>
    fun getProductStream(id: Int): Flow<LuckinProductEntity?>
    suspend fun insertProduct(product: LuckinProductEntity)
    suspend fun deleteProduct(product: LuckinProductEntity)
    suspend fun updateProduct(product: LuckinProductEntity)
}

class OfflineProductRespository(private val productDAO: ProductDAO) : ProductRespository {
    override fun getBestSellersStream(): Flow<List<LuckinProductEntity>> = productDAO.getBestSellersStream()
    override fun getProductStream(id: Int): Flow<LuckinProductEntity?> = productDAO.getProductStream(id)
    override suspend fun insertProduct(product: LuckinProductEntity) = productDAO.insertProduct(product)
    override suspend fun deleteProduct(product: LuckinProductEntity) = productDAO.deleteProduct(product)
    override suspend fun updateProduct(product: LuckinProductEntity) = productDAO.updateProduct(product)
}