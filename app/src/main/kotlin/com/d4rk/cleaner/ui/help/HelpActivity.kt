package com.d4rk.cleaner.ui.help

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.settings.display.theme.AppTheme
import com.d4rk.cleaner.utils.Utils
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class HelpActivity : ComponentActivity() {
    private lateinit var reviewManager : ReviewManager
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    HelpComposable(this@HelpActivity)
                }
            }
        }

    }

    fun versionInfo() {
        val builder = MaterialAlertDialogBuilder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_version_info , null)
        val version : MaterialTextView = dialogLayout.findViewById(R.id.version)
        version.text = String.format(getString(R.string.version) , BuildConfig.VERSION_NAME)
        builder.setView(dialogLayout)
        builder.show()
    }

    fun openSourceLicenses() {
        startActivity(Intent(this , OssLicensesMenuActivity::class.java))
    }

    fun feedback() {
        reviewManager = ReviewManagerFactory.create(this)
        val task = reviewManager.requestReviewFlow()
        task.addOnSuccessListener { reviewInfo ->
            reviewManager.launchReviewFlow(this , reviewInfo)
        }.addOnFailureListener {
            Utils.openUrl(
                this ,
                "https://play.google.com/store/apps/details?id=${this.packageName}&showAllReviews=true"
            )
        }
    }
}