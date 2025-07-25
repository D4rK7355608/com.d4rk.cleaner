package com.d4rk.cleaner.app.clean.scanner.utils.utils.constants.api

import com.d4rk.android.libs.apptoolkit.core.utils.constants.github.GithubConstants

object ApiHost {
    const val API_REPO: String = "com.d4rk.apis"
    const val API_BRANCH: String = "main"
    const val API_FOLDER_PATH: String = "App%20Toolkit"
}

object ApiEnvironments {
    const val ENV_DEBUG: String = "debug"
    const val ENV_RELEASE: String = "release"
}

object ApiPaths {
    const val DEVELOPER_APPS_API: String = "/en/home/api_android_apps.json"
}

object ApiConstants {
    const val BASE_REPOSITORY_URL: String = "${GithubConstants.GITHUB_RAW}/${ApiHost.API_REPO}/refs/heads/${ApiHost.API_BRANCH}/${ApiHost.API_FOLDER_PATH}"
}