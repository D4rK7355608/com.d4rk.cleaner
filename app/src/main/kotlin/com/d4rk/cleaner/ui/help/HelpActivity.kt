package com.d4rk.cleaner.ui.help

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.ui.settings.display.theme.style.AppTheme
import com.d4rk.cleaner.utils.Utils
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class HelpActivity : AppCompatActivity() {
    private lateinit var reviewManager: ReviewManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    HelpComposable(this@HelpActivity)
                }
            }
        }

    }

    /**
     * Initiates the feedback process for the app.
     *
     * This function uses the Google Play In-App Review API to prompt the user for feedback.
     * If the request to launch the in-app review flow is successful, the review dialog is displayed.
     * If the request fails, it opens the Google Play Store page for the app's reviews.
     *
     * @see com.google.android.play.core.review.ReviewManagerFactory
     * @see com.google.android.play.core.review.ReviewManager
     * @param context The context used to create the ReviewManager instance and launch review flows.
     */
    fun feedback() {
        reviewManager = ReviewManagerFactory.create(this)
        val task = reviewManager.requestReviewFlow()
        task.addOnSuccessListener { reviewInfo ->
            reviewManager.launchReviewFlow(this, reviewInfo)
        }.addOnFailureListener {
            Utils.openUrl(
                this,
                "https://play.google.com/store/apps/details?id=${this.packageName}&showAllReviews=true"
            )
        }.addOnFailureListener {
            Utils.sendEmailToDeveloper(this)
        }
    }
}