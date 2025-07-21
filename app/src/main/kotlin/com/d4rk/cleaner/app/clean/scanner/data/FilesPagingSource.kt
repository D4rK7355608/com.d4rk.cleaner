package com.d4rk.cleaner.app.clean.scanner.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.io.File
import java.util.ArrayDeque

class FilesPagingSource(
    private val root: File,
    private val trashDir: File
) : PagingSource<Int, File>() {

    private val queue = ArrayDeque<File>()
    private var initialized = false

    private fun ensureInit() {
        if (!initialized) {
            queue.add(root)
            initialized = true
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, File> {
        ensureInit()
        val files = mutableListOf<File>()
        val pageSize = params.loadSize
        while (files.size < pageSize && queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current.isDirectory) {
                if (!current.absolutePath.startsWith(trashDir.absolutePath)) {
                    current.listFiles()?.forEach { child ->
                        queue.addLast(child)
                    }
                }
            } else {
                files.add(current)
            }
        }
        val nextKey = if (queue.isEmpty()) null else (params.key ?: 0) + 1
        return LoadResult.Page(
            data = files,
            prevKey = null,
            nextKey = nextKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, File>): Int? = state.anchorPosition
}
