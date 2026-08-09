package com.example.project1.data.repository

import com.example.project1.data.SupabaseClientProvider
import com.example.project1.data.model.LeaderBroadData
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

interface LeaderBoarduiRepository {
    suspend fun getLeaderboardProfiles(): List<LeaderBroadData>
    fun getCurrentUserId(): String?
}

class LeaderboardRepositoryImpl : LeaderBoarduiRepository {

    override suspend fun getLeaderboardProfiles(): List<LeaderBroadData> {
        return SupabaseClientProvider.client
            .from("profiles") // Replace with your Supabase table name
            .select {
                order(column = "points", order = Order.DESCENDING)
            }
            .decodeList<LeaderBroadData>()
    }

    override fun getCurrentUserId(): String? {
        return runCatching {
            SupabaseClientProvider.client.auth.currentUserOrNull()?.id
        }.getOrNull()
    }
}