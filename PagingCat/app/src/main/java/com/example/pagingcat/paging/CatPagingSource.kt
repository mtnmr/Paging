package com.example.pagingcat.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pagingcat.data.Cat
import com.example.pagingcat.data.CatRepository

class CatPagingSource(private val catRepository: CatRepository): PagingSource<Int, Cat>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {
        val currentKey = params.key ?: 1
        return catRepository.cats(params.loadSize,currentKey)
            .fold(
                onSuccess = {
                    LoadResult.Page(
                        data = it,
                        prevKey = (currentKey -1).takeIf { key -> key>0 },
                        nextKey = (currentKey +1).takeIf { _ -> it.isNotEmpty()}
                    )
                },
                onFailure = {
                    Log.d("CatApp", "LoadResult: $it")
                    LoadResult.Error(it)
                }
            )
    }

    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}