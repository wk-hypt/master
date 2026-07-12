package com.example.project1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project1.data.DAO.EcoAdsDAO
import com.example.project1.data.DAO.OfferDAO
import com.example.project1.data.DAO.SubmissionDAO
import com.example.project1.data.DAO.UserDAO
import com.example.project1.data.entity.*

@Database(
    entities = [
        EcoBannerEntity::class,
        EcoFeatureEntity::class,
        VoucherEntity::class,
        EcoSubmissionEntity::class,
        UserEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class EcoDatabase : RoomDatabase() {

    abstract fun ecoAdsDAO(): EcoAdsDAO
    abstract fun offerDAO(): OfferDAO
    abstract fun submissionDAO(): SubmissionDAO
    abstract fun userDAO(): UserDAO

    companion object {
        @Volatile
        private var Instance: EcoDatabase? = null

        fun getDatabase(context: Context): EcoDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    EcoDatabase::class.java,
                    "eco_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}