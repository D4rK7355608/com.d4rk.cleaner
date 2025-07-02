package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.PromotedApp
import com.d4rk.cleaner.app.clean.scanner.utils.utils.constants.api.ApiConstants
import com.d4rk.cleaner.app.clean.scanner.utils.utils.constants.api.ApiEnvironments
import com.d4rk.cleaner.app.clean.scanner.utils.utils.constants.api.ApiPaths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GetPromotedAppUseCase {
    suspend operator fun invoke(currentPackage: String): PromotedApp? = withContext(Dispatchers.IO) {
        runCatching {
            val client = URL(BuildConfig.DEBUG.let { isDebug ->
                val environment = if (isDebug) ApiEnvironments.ENV_DEBUG else ApiEnvironments.ENV_RELEASE
                "${ApiConstants.BASE_REPOSITORY_URL}/$environment${ApiPaths.DEVELOPER_APPS_API}"
            })
            val connection = client.openConnection() as HttpURLConnection
            val response = connection.inputStream.bufferedReader().use { it.readText() }
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
