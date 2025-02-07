package com.d4rk.cleaner.ui.screens.help.repository

import android.app.Activity
import android.app.Application
import com.d4rk.cleaner.core.data.model.ui.screens.UiHelpQuestion
import com.google.android.play.core.review.ReviewInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HelpRepository(application : Application) : HelpRepositoryImplementation(application) {

    suspend fun getFAQsRepository(onSuccess : (ArrayList<com.d4rk.cleaner.core.data.model.ui.screens.UiHelpQuestion>) -> Unit) {
        withContext(Dispatchers.IO) {
            val questions = getFAQsImplementation().map { (questionRes , summaryRes) ->
                com.d4rk.cleaner.core.data.model.ui.screens.UiHelpQuestion(
                    question = application.getString(questionRes) , answer = application.getString(summaryRes)
                )
            }.toCollection(destination = ArrayList())

            withContext(Dispatchers.Main) {
                onSuccess(questions)
            }
        }
    }

    suspend fun requestReviewFlowRepository(
        onSuccess : (ReviewInfo) -> Unit , onFailure : () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            requestReviewFlowImplementation(onSuccess = onSuccess , onFailure = onFailure)
        }
    }

    suspend fun launchReviewFlowRepository(activity : Activity , reviewInfo : ReviewInfo) {
        withContext(Dispatchers.IO) {
            launchReviewFlowImplementation(activity = activity , reviewInfo = reviewInfo)
        }
    }
}