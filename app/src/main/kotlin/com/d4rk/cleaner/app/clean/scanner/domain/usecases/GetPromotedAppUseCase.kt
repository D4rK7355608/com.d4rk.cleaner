package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.PromotedApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class GetPromotedAppUseCase {
    suspend operator fun invoke(currentPackage: String): PromotedApp? = withContext(Dispatchers.IO) {
        runCatching {
            val apiUrl = "https://raw.githubusercontent.com/D4rK7355608/apps/main/apps.json"
            val response = URL(apiUrl).readText()
            val appsJson = JSONObject(response)
                .getJSONObject("data")
                .getJSONArray("apps")
            val filtered = mutableListOf<PromotedApp>()
            for (i in 0 until appsJson.length()) {
                val item = appsJson.getJSONObject(i)
                val category = item.optString("category")
                if (category.equals("tools", true) || category.equals("utilities", true)) {
                    val pkg = item.getString("packageName")
                    if (pkg != currentPackage) {
                        filtered.add(
                            PromotedApp(
                                name = item.getString("name"),
                                packageName = pkg,
                                iconLogo = item.getString("iconLogo")
                            )
                        )
                    }
                }
            }
            filtered.randomOrNull()
        }.getOrNull()
    }
}
