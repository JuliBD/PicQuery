package me.grey.picquery.ui.result

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.grey.picquery.PicQueryApplication
import me.grey.picquery.common.showToast
import me.grey.picquery.core.ImageSearcher
import me.grey.picquery.data.data_source.PhotoRepository
import me.grey.picquery.data.model.Album
import me.grey.picquery.data.model.Photo

class SearchResultViewModel : ViewModel() {
    companion object {
        private const val TAG = "SearchResultViewModel"
    }

    private val _resultList = MutableStateFlow<List<Photo>>(emptyList())
    val resultList = _resultList.asStateFlow()

    private val _searchingState = MutableStateFlow(true)
    val searchingState = _searchingState.asStateFlow()

    val searchText = mutableStateOf("")

    private val repo = PhotoRepository(PicQueryApplication.context.contentResolver)
    fun startSearch(text: String, albumRange: List<Album> = emptyList()) {
        if (text.trim().isEmpty()) {
            showToast("请输入搜索内容")
            Log.w(TAG, "搜索字段为空")
            return
        }
        searchText.value = text
        viewModelScope.launch(Dispatchers.IO) {
            _searchingState.value = true
            val ids = ImageSearcher.search(text)
            if (ids != null) {
                _resultList.value = repo.getPhotoListByIds(ids)
            }
            _searchingState.value = false
        }
    }

}