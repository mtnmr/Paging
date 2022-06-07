package com.example.pagingcat.paging

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingcat.databinding.ViewCatLoadStateBinding

private typealias RetryListener = () -> Unit

class CatPagingLoadStateAdapter(private val retry:RetryListener):
    LoadStateAdapter<CatPagingLoadStateAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ViewCatLoadStateBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            Log.d(this::class.simpleName, "LoadState: $loadState")

            binding.isLoading = loadState is LoadState.Loading
            binding.isError = loadState is LoadState.Error

            binding.retry.setOnClickListener { retry() }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder =
        ViewHolder(
            ViewCatLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}