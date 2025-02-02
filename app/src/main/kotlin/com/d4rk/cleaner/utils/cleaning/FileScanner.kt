package com.d4rk.cleaner.utils.cleaning

import android.app.Application
import android.os.Environment
import java.io.File

class FileScanner(private val application : Application) {

    fun getAllFiles() : Pair<List<File> , List<File>> {
        val files : MutableList<File> = mutableListOf()
        val emptyFolders : MutableList<File> = mutableListOf()
        val stack : ArrayDeque<File> = ArrayDeque()
        val root : File = Environment.getExternalStorageDirectory()
        stack.addFirst(root)

        val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

        while (stack.isNotEmpty()) {
            val currentFile : File = stack.removeFirst()
            if (currentFile.isDirectory) {
                if (! currentFile.absolutePath.startsWith(trashDir.absolutePath)) {
                    currentFile.listFiles()?.let { children ->
                        if (children.isEmpty()) {
                            emptyFolders.add(currentFile)
                        }
                        else {
                            children.forEach { child ->
                                if (child.isDirectory) {
                                    stack.addLast(child)
                                }
                                else {
                                    files.add(child)
                                }
                            }
                        }
                    }
                }
            }
            else {
                files.add(currentFile)
            }
        }
        return Pair(files , emptyFolders)
    }
}