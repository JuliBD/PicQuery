package me.grey.picquery.ui.main

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.InternalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import me.grey.picquery.data.model.Album
import me.grey.picquery.ui.widgets.CustomChip
import java.io.File

@OptIn(InternalTextApi::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreenM3(
    searchAbleList: List<Album>?,
    unsearchableList: List<Album>?,
    onAddIndex: (album: Album) -> Unit, // 请求对某个相册编码
    onRemoveIndex: (album: Album) -> Unit, // 移除某个相册的编码
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    Column(
        Modifier.padding(bottom = 56.dp), // 避开bottomBar的遮挡
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Box(Modifier.height(50.dp))
        TextButton(onClick = { viewModel.toDevTest(context) }) {
            Text(text = "DevTest")
        }
        LogoRow()
        Box(modifier = Modifier.padding(horizontal = 12.dp)) {
            SearchInput()
        }
        AlbumList(
            searchAbleList ?: emptyList(),
            unsearchableList ?: emptyList(),
            onClickSearchable = {}
        )
    }
}

@Composable
private fun LogoRow(
    viewModel: MainViewModel = viewModel()
) {
    // FIXME test
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val textStyle = TextStyle(fontSize = 29.sp)
        Text(text = "Pic", style = textStyle)
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "搜索",
            modifier = Modifier.size(39.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "uery", style = textStyle.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@InternalTextApi
@Composable
private fun SearchInput(
    viewModel: MainViewModel = viewModel()
) {
//    var textValue by remember { mutableStateOf(TextFieldValue("")) }
    val text = remember { mutableStateOf("") }
    val active = remember { mutableStateOf(false) }
    val context = LocalContext.current
    fun action() {
        // TODO onSearch
        Log.d("onSearch", text.value)
        viewModel.toSearchResult(context, text.value)
    }
    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = text.value,
        onQueryChange = { text.value = it },
        onSearch = { action() },
        active = false,
        onActiveChange = { },
        placeholder = { Text("对图片的描述...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
    ) {
//        repeat(4) { idx ->
//            val resultText = "Suggestion $idx"
//            ListItem(
//                headlineContent = { Text(resultText) },
//                supportingContent = { Text("Additional info") },
//                leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
//                modifier = Modifier
//                    .clickable {
//                        text.value = resultText
//                        active.value = false
//                    }
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 4.dp)
//            )
//        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun AlbumList(
    searchAbleList: List<Album>,
    unsearchableList: List<Album>,
    onClickSearchable: (Album) -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    LazyColumn(content = {
        stickyHeader {
            AlbumListHeader(
                "可搜索相册 (${searchAbleList.size})",
                leadingIcon = Icons.Filled.CheckCircle,
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Filled.CheckCircle,
//                        contentDescription = ""
//                    )
//                }
            )
        }
//        items(searchAbleList.size) {
//            SearchableAlbum(searchAbleList[it], onClickSearchable)
//        }
//        item {
//            FlowRow(modifier = Modifier.padding(8.dp)) {
//                searchAbleList.forEach {
//                    SearchableAlbum(it, onClickSearchable)
//                }
//            }
//        }
        stickyHeader {
            AlbumListHeader(
                "待索引相册 (${unsearchableList.size})",
                leadingIcon = Icons.Filled.Info,
            )
        }
        items(unsearchableList.size) {
            UnsearchableAlbum(
                unsearchableList[it],
                onAddIndex = { album ->
                    viewModel.encodeAlbum(album)
                },
            )
        }
    })
}

@Composable
private fun SearchableAlbum(album: Album, onClick: (Album) -> Unit) {
    Box(Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
        CustomChip(
            selected = true,
            text = "${album.label} (${album.count})",
            onClick = { onClick(album) },
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 5.dp)
        )
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalGlideComposeApi::class)
@Composable
private fun UnsearchableAlbum(
    album: Album,
    onAddIndex: (Album) -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val state = remember { viewModel.encodingAlbumState }
    val showProgress = remember { mutableStateOf(false) }
    showProgress.value = album.id == state.value.id && state.value.total > 0

    val progress = (state.value.current.toFloat() / state.value.total)

    ListItem(
        leadingContent = {
            Box(Modifier.size(55.dp)) {
                GlideImage(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp)),
                    model = File(album.coverPath),
                    contentDescription = album.label,
                    contentScale = ContentScale.Crop,
                )
            }
        },
        headlineContent = { Text(text = album.label) },
        supportingContent = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = album.count.toString())
                    if (showProgress.value)
                        Text(text = "${(progress * 100).toInt()}%")
                }
                Box(Modifier.height(10.dp))
                if (showProgress.value)
                    LinearProgressIndicator(progress = progress)
            }
        },
        trailingContent = {
            IconButton(onClick = { onAddIndex(album) }) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AlbumListHeader(title: String, leadingIcon: ImageVector) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(32.dp)
            )
            Icon(
                imageVector = leadingIcon,
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 5.dp),
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}