package com.d4rk.cleaner.core.data.model.ui.screens

import com.google.android.play.core.review.ReviewInfo

data class UiHelpScreen(
    var reviewInfo : ReviewInfo? = null , val questions : ArrayList<com.d4rk.cleaner.core.data.model.ui.screens.UiHelpQuestion> = ArrayList()
)

data class UiHelpQuestion(
    val question : String = "" , val answer : String = "" , val isExpanded : Boolean = false
)