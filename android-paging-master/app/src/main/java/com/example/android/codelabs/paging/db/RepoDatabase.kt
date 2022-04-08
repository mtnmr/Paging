package com.example.android.codelabs.paging.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.codelabs.paging.model.Repo


@Database(entities = [Repo::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class RepoDatabase: RoomDatabase() {
    abstract fun reposDao():RepoDao
    abstract fun remoteKeysDao():RemoteKeysDao

    companion object{
        @Volatile
        private var INSTANCE:RoomDatabase ?= null

        fun getInstance(context: Context): RoomDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context):RoomDatabase =
            Room.databaseBuilder(context.applicationContext,
                RepoDatabase::class.java, "GitHub.db")
                .build()
    }
}