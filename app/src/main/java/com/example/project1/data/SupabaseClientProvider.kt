package com.example.project1.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://nclhilldpwxtpvbzustw.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5jbGhpbGxkcHd4dHB2Ynp1c3R3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODQ3NTIyNzEsImV4cCI6MjEwMDMyODI3MX0.kSbmbjj2zrzhvhffU8OX_a2xknu3Y9YWCQX_Lrv4-BY"
    ) {
        install(Postgrest)
        install(Storage)
    }
}