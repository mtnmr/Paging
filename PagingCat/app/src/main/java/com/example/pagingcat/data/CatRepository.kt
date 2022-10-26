package com.example.pagingcat.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CatRepository {

    suspend fun cats(limit: Int, page: Int): Result<List<Cat>>
}

const val API_KEY = ""

class CatRepositoryImpl @Inject constructor(private val catSearchApi: CatSearchApi) :
    CatRepository {

    override suspend fun cats(limit: Int, page: Int): Result<List<Cat>> = kotlin.runCatching {
        withContext(Dispatchers.IO) {
            catSearchApi.cats(limit, page)
        }
    }
}
