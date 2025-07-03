package com.d4rk.cleaner.app.clean.whatsapp.navigation

sealed class WhatsAppRoute(val route: String) {
    data object Permission : WhatsAppRoute("permission")
    data object Summary : WhatsAppRoute("summary")
    data class Details(val type: String) : WhatsAppRoute("details/{type}") {
        companion object {
            const val TYPE = "type"
            fun create(type: String) = "details/$type"
        }
    }
}
