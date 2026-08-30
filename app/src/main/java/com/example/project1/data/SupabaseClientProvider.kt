package com.example.project1.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://apnagyerovxeriqycgku.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFwbmFneWVyb3Z4ZXJpcXljZ2t1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODgwMjAwMTAsImV4cCI6MjEwMzU5NjAxMH0.bCOvZpybZJ8KDWJORgxjYV8IH-vAnWTdU9svgrmVI1s"
    ) {
        // install plugins
        install(Auth)
        install(Postgrest)
        install(Storage)

    }
}