package com.example.pagingcat.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.pagingcat.data.Cat
import com.example.pagingcat.data.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatViewModel @Inject constructor(private val repository: CatRepository)  :ViewModel(){

    val cat:LiveData<PagingData<Cat>> =
        Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)){
            CatPagingSource(repository)
        }.liveData.cachedIn(viewModelScope)
}