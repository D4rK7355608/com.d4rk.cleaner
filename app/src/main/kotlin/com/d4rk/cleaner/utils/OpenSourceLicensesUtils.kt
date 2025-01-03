package com.d4rk.cleaner.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.helpers.getStringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object OpenSourceLicensesUtils {
    private const val PACKAGE_NAME = BuildConfig.APPLICATION_ID

    private suspend fun getChangelogMarkdown(): String {
        return withContext(Dispatchers.IO) {
            val url = URL("https://raw.githubusercontent.com/D4rK7355608/$PACKAGE_NAME/refs/heads/master/CHANGELOG.md")
            (url.openConnection() as? HttpURLConnection)?.let { connection ->
                try {
                    connection.requestMethod = "GET"
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                            return@withContext reader.readText()
                        }
                    } else {
                        getStringResource(id= R.string.error_loading_changelog)
                    }
                } finally {
                    connection.disconnect()
                }
            } ?: getStringResource(id= R.string.error_loading_changelog)
        }
    }

    private suspend fun getEulaMarkdown(): String {
        return withContext(Dispatchers.IO) {
            val url = URL("https://raw.githubusercontent.com/D4rK7355608/$PACKAGE_NAME/refs/heads/master/EULA.md")
            (url.openConnection() as? HttpURLConnection)?.let { connection ->
                try {
                    connection.requestMethod = "GET"
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                            return@withContext reader.readText()
                        }
                    } else {
                        getStringResource(id= R.string.error_loading_eula)
                    }
                } finally {
                    connection.disconnect()
                }
            } ?: getStringResource(id= R.string.error_loading_eula)
        }
    }

    private fun extractLatestVersionChangelog(markdown: String): String {
        val currentVersion = BuildConfig.VERSION_NAME
        val regex = Regex(pattern = "(# Version\\s+$currentVersion:\\s*[\\s\\S]*?)(?=# Version|$)")
        val match = regex.find(markdown)
        return match?.groups?.get(1)?.value?.trim() ?: "No changelog available for version $currentVersion"
    }

    private fun convertMarkdownToHtml(markdown: String): String {
        val parser = Parser.builder().build()
        val renderer = HtmlRenderer.builder().build()
        val document = parser.parse(markdown)
        return renderer.render(document)
    }

    suspend fun loadHtmlData(): Pair<String?, String?> {
        val changelogMarkdown = getChangelogMarkdown()
        val extractedChangelog = extractLatestVersionChangelog(changelogMarkdown)
        val changelogHtml = convertMarkdownToHtml(extractedChangelog)

        val eulaMarkdown = getEulaMarkdown()
        val eulaHtml = convertMarkdownToHtml(eulaMarkdown)

        return changelogHtml to eulaHtml
    }
}

@Composable
fun rememberHtmlData(): State<Pair<String?, String?>> {
    val htmlDataState = remember { mutableStateOf<Pair<String?, String?>>(value = null to null) }

    LaunchedEffect(Unit) {
        htmlDataState.value = OpenSourceLicensesUtils.loadHtmlData()
    }

    return htmlDataState
}