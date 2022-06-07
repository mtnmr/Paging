package com.example.pagingcat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.example.pagingcat.databinding.ActivityMainBinding
import com.example.pagingcat.paging.CatPagingAdapter
import com.example.pagingcat.paging.CatPagingLoadStateAdapter
import com.example.pagingcat.paging.CatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding :ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private val viewModel :CatViewModel by viewModels()

    private val listAdapter = CatPagingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.catList.adapter = listAdapter
            .withLoadStateFooter(CatPagingLoadStateAdapter{ listAdapter.retry() })

        listAdapter.addLoadStateListener {
            binding.refreshLayout.isRefreshing = it.refresh is LoadState.Loading
        }
        binding.refreshLayout.setOnRefreshListener {
            listAdapter.refresh()
        }

        viewModel.cat.observe(this){
            lifecycleScope.launchWhenStarted {
                listAdapter.submitData(it)
            }
        }
    }
}