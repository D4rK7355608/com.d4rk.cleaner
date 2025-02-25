package com.d4rk.cleaner.ui.components.navigation

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedButtonDirection
import com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.screens.support.SupportActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarMain(context : Context , navigationIcon : ImageVector , onNavigationIconClick : () -> Unit) {
    TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) } , navigationIcon = {
        AnimatedButtonDirection(
            icon = navigationIcon ,
            contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back) ,
            onClick = {
                onNavigationIconClick()
            } ,
        )
    } , actions = {
        AnimatedButtonDirection(
            fromRight = true ,
            icon = Icons.Outlined.VolunteerActivism ,
            contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back) ,
            onClick = {
                IntentsHelper.openActivity(context , SupportActivity::class.java)
            } ,
        )
    })
}