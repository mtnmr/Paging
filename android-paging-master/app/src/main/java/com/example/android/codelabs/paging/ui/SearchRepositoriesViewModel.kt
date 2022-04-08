/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.zip.DeflaterOutputStream

/**
 * ViewModel for the [SearchRepositoriesActivity] screen.
 * The ViewModel works with the [GithubRepository] to get the data.
 */
class SearchRepositoriesViewModel(
    private val repository: GithubRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //日本語版codeLab
//    private var currentQueryValue :String? = null
//
//    private var currentSearchResult: Flow<PagingData<Repo>>? = null

//    fun searchRepo(queryString:String):Flow<PagingData<Repo>>{
//        val lastResult = currentSearchResult
//
//        if (queryString == currentQueryValue && lastResult != null){
//            return lastResult
//        }
//        currentQueryValue = queryString
//        val newResult:Flow<PagingData<Repo>> = repository.getSearchResultStream(queryString)
//            .cachedIn(viewModelScope)
//        currentSearchResult = newResult
//        return newResult
//    }

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<UiModel>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit


    init {
        val initialQuery:String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        val lastQueryScrolled :String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()

        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }

        val queryScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }



        pagingDataFlow = searches
            .flatMapLatest { searchRepo(queryString = it.query) }
            .cachedIn(viewModelScope)

        state = combine(
            searches,
            queryScrolled,
            ::Pair
        ).map{ (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = {action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

//        val queryLiveData =
//            MutableLiveData(savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY)
//
//        state = queryLiveData
//            .distinctUntilChanged()
//            .switchMap { queryString ->
//                liveData {
//                    val uiState = repository.getSearchResultStream(queryString)
//                        .map {
//                            UiState(
//                                query = queryString,
//                                searchResult = it
//                            )
//                        }
//                        .asLiveData(Dispatchers.Main)
//                    emitSource(uiState)
//                }
//            }
//
//        accept = { action ->
//            when (action) {
//                is UiAction.Search -> queryLiveData.postValue(action.query)
//                is UiAction.Scroll -> if (action.shouldFetchMore) {
//                    val immutableQuery = queryLiveData.value
//                    if (immutableQuery != null) {
//                        viewModelScope.launch {
//                            repository.requestMore(immutableQuery)
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    private fun searchRepo(queryString: String): Flow<PagingData<UiModel>> =
        repository.getSearchResultStream(queryString)
           .map { pagingData -> pagingData.map { UiModel.RepoItem(it) } }
           .map {
               it.insertSeparators{ before, after ->
                   if(after == null){
                       // we're at the end of the list
                       return@insertSeparators null
                   }

                   if (before == null) {
                       // we're at the beginning of the list
                       return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                   }

                   // check between 2 items
                   if (before.roundedStarCount > after.roundedStarCount){
                       if (after.roundedStarCount >= 1){
                           UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                       }else {
                           UiModel.SeparatorItem("< 10.000+ stars")
                       }
                   } else {
                       /// no separator
                       null
                   }
           }

    }

}


sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery:String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled:String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
//    val searchResult: RepoSearchResult
)


sealed class UiModel {
    data class RepoItem(val repo:Repo) : UiModel()
    data class SeparatorItem(val description: String) : UiModel()
}

private val UiModel.RepoItem.roundedStarCount : Int
    get() = this.repo.stars / 10_000


private const val VISIBLE_THRESHOLD = 5
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Android"
private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"


