package me.grey.picquery.common

import androidx.room.Room
import me.grey.picquery.PicQueryApplication
import me.grey.picquery.data.AppDatabase
import me.grey.picquery.data.data_source.AlbumRepository
import me.grey.picquery.data.data_source.EmbeddingRepository
import me.grey.picquery.data.data_source.PhotoRepository
import me.grey.picquery.domain.AlbumManager
import me.grey.picquery.domain.ImageSearcher
import me.grey.picquery.domain.encoder.ImageEncoder
import me.grey.picquery.domain.encoder.TextEncoder
import me.grey.picquery.ui.display.DisplayViewModel
import me.grey.picquery.ui.home.HomeViewModel
import me.grey.picquery.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val viewModelModules = module {
    viewModel {
        HomeViewModel(
            albumManager = get(),
            imageSearcher = get()
        )
    }
    viewModel {
        SearchViewModel(
            albumManager = get(),
            imageSearcher = get()
        )
    }
    viewModel {
        DisplayViewModel(photoRepository = get(), imageSearcher = get())
    }
}

private val dataModules = module {
    // SQLite Database
    single {
        Room.databaseBuilder(
            PicQueryApplication.context,
            AppDatabase::class.java, "app-db"
        ).build()
    }

    single { AlbumRepository(androidContext().contentResolver, database = get()) }
    single { EmbeddingRepository(database = get()) }
    single { PhotoRepository(androidContext().contentResolver) }
}

private val domainModules = module {
    single {
        ImageSearcher(
            imageEncoder = get(),
            textEncoder = get(),
            embeddingRepository = get(),
            contentResolver = androidContext().contentResolver
        )
    }
    single {
        AlbumManager(
            albumRepository = get(),
            photoRepository = get(),
            imageSearcher = get(),
        )
    }

    single { ImageEncoder() }
    single { TextEncoder() }
}

val AppModules = listOf(viewModelModules, dataModules, domainModules)