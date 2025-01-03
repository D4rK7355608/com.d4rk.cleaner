package com.d4rk.cleaner.ui.screens.help.repository

import android.app.Application
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.screens.help.HelpActivity
import com.d4rk.cleaner.utils.helpers.IntentsHelper
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

abstract class HelpRepositoryImplementation(
    val application : Application
) {
    fun getFAQsImplementation() : List<Pair<Int , Int>> {
        return listOf(
            R.string.question_1 to R.string.summary_preference_faq_1 ,
            R.string.question_2 to R.string.summary_preference_faq_2 ,
            R.string.question_3 to R.string.summary_preference_faq_3 ,
            R.string.question_4 to R.string.summary_preference_faq_4 ,
            R.string.question_5 to R.string.summary_preference_faq_5 ,
            R.string.question_6 to R.string.summary_preference_faq_6 ,
            R.string.question_7 to R.string.summary_preference_faq_7 ,
            R.string.question_8 to R.string.summary_preference_faq_8 ,
            R.string.question_9 to R.string.summary_preference_faq_9
        )
    }

    open suspend fun requestReviewFlowImplementation(
        onSuccess : (ReviewInfo) -> Unit , onFailure : () -> Unit
    ) {
        val reviewManager : ReviewManager = ReviewManagerFactory.create(application)
        val request : Task<ReviewInfo> = reviewManager.requestReviewFlow()
        val packageName : String = application.packageName

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(task.result)
            }
            else {
                onFailure()
            }
        }.addOnFailureListener {
            IntentsHelper.openUrl(
                context = application ,
                url = "https://play.google.com/store/apps/details?id=$packageName&showAllReviews=true"
            )
        }
    }

    fun launchReviewFlowImplementation(activity : HelpActivity , reviewInfo : ReviewInfo) {
        val reviewManager : ReviewManager = ReviewManagerFactory.create(activity)
        reviewManager.launchReviewFlow(activity , reviewInfo)
    }
}
