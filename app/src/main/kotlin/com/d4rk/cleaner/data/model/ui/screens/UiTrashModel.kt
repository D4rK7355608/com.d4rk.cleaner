package com.d4rk.cleaner.data.model.ui.screens

import java.io.File

data class UiTrashModel(
    val trashFiles : List<File> = emptyList() ,
    val selectedFileCount : Int = 0 ,
    val fileSelectionStates : Map<File , Boolean> = emptyMap() ,
)