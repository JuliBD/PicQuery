package me.grey.picquery.ui.feat.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.grey.picquery.PicQueryApplication
import me.grey.picquery.core.ImageSearcher
import me.grey.picquery.core.encoder.TextEncoder
import me.grey.picquery.data.data_source.PhotoRepository
import me.grey.picquery.data.model.Album
import me.grey.picquery.data.model.Photo
import me.grey.picquery.ui.feat.main.SearchScreenState

class SearchResultViewModel : ViewModel() {
    companion object {
        private const val TAG = "SearchResultViewModel"
    }

    private val _resultList = MutableStateFlow<List<Photo>>(listOf())
    val resultList = _resultList.asStateFlow()

    private val _searchingState = MutableStateFlow(true)
    val searchingState = _searchingState.asStateFlow()

    private val repo = PhotoRepository(PicQueryApplication.context.contentResolver)
    fun startSearch(text: String, albumRange: List<Album> = emptyList()) {
        if (text.trim().isEmpty()) {
            Log.w(TAG, "搜索字段为空")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _searchingState.value = true
            val ids = ImageSearcher.search(text)
            if (ids != null) {
                // TODO Test
                Log.d(TAG, "${ids[0]} ${ids[1]} ${ids[2]}")
                _resultList.value = repo.getPhotoListByIds(ids)
//                val p = repo.getPhotoById(ids[0])
//                p?.let { _resultList.value = listOf(it) }
            }
            _searchingState.value = false
        }
    }

}