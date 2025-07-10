package com.d4rk.cleaner.core.utils.helpers

import java.io.File

object DirectoryScanner {
    fun scan(root: File, skipDir: (File) -> Boolean = { false }, onFile: (File) -> Unit) {
        if (!root.exists()) return
        root.walkTopDown()
            .onEnter { dir -> !skipDir(dir) }
            .forEach { file ->
                if (file.isFile) onFile(file)
            }
    }
}
