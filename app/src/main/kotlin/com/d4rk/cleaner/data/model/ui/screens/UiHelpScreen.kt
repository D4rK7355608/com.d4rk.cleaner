package com.d4rk.cleaner.data.model.ui.screens

import com.google.android.play.core.review.ReviewInfo

data class UiHelpScreen(
    var reviewInfo : ReviewInfo? = null ,
    val questions : ArrayList<UiHelpQuestion> = ArrayList()
)

data class UiHelpQuestion(
    val question : String = "" ,
    val answer : String = "" ,
    val isExpanded : Boolean = false
)