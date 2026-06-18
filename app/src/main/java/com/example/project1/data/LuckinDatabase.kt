package com.example.project1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, LuckinBannerEntity::class, LuckinFeatureEntity::class, LuckinPromotionEntity::class, LuckinVoucherEntity::class, LuckinProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LuckinDatabase : RoomDatabase() {

    abstract fun userDao(): UserDAO
    abstract fun luckinAdsDao(): LuckinAdsDAO
    abstract fun offerDao(): OfferDAO
    abstract fun productDao(): ProductDAO

    companion object {
        @Volatile
        private var Instance: LuckinDatabase? = null

        fun getDatabase(context: Context): LuckinDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    LuckinDatabase::class.java,
                    "luckin_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}