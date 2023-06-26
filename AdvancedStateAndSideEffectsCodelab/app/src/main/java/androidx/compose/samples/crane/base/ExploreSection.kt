/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.samples.crane.base

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.home.OnExploreItemClicked
import androidx.compose.samples.crane.ui.BottomSheetShape
import androidx.compose.samples.crane.ui.crane_caption
import androidx.compose.samples.crane.ui.crane_divider_color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest.Builder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

val b = AtomicInteger().apply {
    Launch(false) {
        while (true) {
            incrementAndGet()
            delay(100)
        }
    }
}

@Composable
fun ExploreSection(
    modifier: Modifier = Modifier,
    title: String,
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color.White, shape = BottomSheetShape) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                Log.e("我被", "外面重组了")
                val listState = rememberLazyListState()
                ExploreList(exploreList, onItemClicked, listState = listState)
                // Show the button if the first visible item is past
                // the first item. We use a remembered derived state to
                // minimize unnecessary compositions
                val showButton by remember {
                    derivedStateOf {
                        listState.firstVisibleItemIndex > 0
                    }
                }
                // 标注了 @Stable 的类型：equals 比较 -> 重组 / 不重组
                // 未标注 @Stable 的类型：
                // 　　如果能被推断为 Stable 类型：equals 比较 -> 重组 / 不重组
                // 　　如果不能被推断为 Stable 类型：重组
                //
                // Compose skips the recomposition of a composable if all the inputs are stable and
                // haven't changed. The comparison uses the equals method.
                //
                // Recomposition is typically triggered by a change to a State<T> object. Compose
                // tracks these and runs all composables in the Composition that read that
                // particular State<T>, and any composables that they call that cannot be skipped.
                if (showButton) {
//                if (listState.firstVisibleItemIndex > 0) {
                    Log.e("我被", "重组了${b.get()}")
                    val coroutineScope = rememberCoroutineScope()
                    if (true) {
                        MyButton(
                            coroutineScope = coroutineScope, modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .navigationBarsPadding()
                                .padding(bottom = 8.dp), listState
                        )
                    } else {
                        FloatingActionButton(
                            backgroundColor = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .navigationBarsPadding()
                                .padding(bottom = 8.dp),
                            onClick = {
                                coroutineScope.launch {
                                    listState.scrollToItem(0)
                                }
                            }
                        ) {
                            Log.e("我被", "重组了!!")
                            Box {
                                Log.e("我被", "重组了@@@@")
                                Text("Up!")
                            }
                            Box {
                                Log.e("我被", "重组了~~~~")
                                Text("Up!" + "${listState.firstVisibleItemIndex > 0}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyButton(coroutineScope: CoroutineScope, modifier: Modifier, listState: LazyListState) {
    FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                listState.scrollToItem(0)
            }
        }
    ) {
        Log.e("我被", "重组了!!")
        Box {
            Log.e("我被", "重组了~~~~")
            MyText(listState = listState)
        }
    }
}

@Composable
private fun MyText(listState: LazyListState) {
    Text("Up!" + "${listState.firstVisibleItemIndex > 0}")
}

@Composable
private fun ExploreList(
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val buffer = object : MutableList<ExploreModel?> by MutableList(
        exploreList.size,
        { null }) {
        override fun equals(other: Any?): Boolean {
            return false
        }
    }
    val fetchResult by produceState(
        buffer,
        exploreList
    ) {
        Launch(false) {
            channelFlow {
                val flowScope = CoroutineScope(currentCoroutineContext())
                exploreList.forEachIndexed { index, exploreModel ->
                    flowScope.Launch(false) {
                        delay((2000L..8000).random())
                        send(Pair(index, exploreModel))
                    }
                }
            }.collect { (index, model) ->
                buffer[index] = model
                value = buffer
            }
        }
    }
    val fetchResultFilteredByLen by remember {
        derivedStateOf(object : SnapshotMutationPolicy<List<ExploreModel?>> {
            override fun equivalent(a: List<ExploreModel?>, b: List<ExploreModel?>): Boolean {
                return a.size == b.size
            }
        }) {
            fetchResult
        }
    }
    val columnTimes = remember {
        AtomicInteger()
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        state = listState
    ) {
        Log.e("列表", "哈哈哈")
        itemsIndexed(fetchResultFilteredByLen) { index, item ->
            val itemN by remember {
                derivedStateOf(structuralEqualityPolicy()) {
                    fetchResult[index]
                }
            }
            Column(Modifier.fillParentMaxWidth()) {
                Log.e("列", "列" + columnTimes.incrementAndGet())
                itemN?.let {
                    ExploreItem(
                        modifier = Modifier.fillParentMaxWidth(),
                        item = it,
                        onItemClicked = onItemClicked
                    )
                } ?: Text(text = "我不存在")
                Divider(color = crane_divider_color)
            }
        }
    }
}

@Composable
private fun ExploreItem(
    modifier: Modifier = Modifier,
    item: ExploreModel,
    onItemClicked: OnExploreItemClicked
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer {
            Box {
                val painter = rememberAsyncImagePainter(
                    model = Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )

                if (painter.state is AsyncImagePainter.State.Loading) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_crane_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center),
                    )
                }
            }
        }
        Spacer(Modifier.width(24.dp))
        Column {
            Text(
                text = item.city.nameToDisplay,
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
        }
    }
}

@Composable
private fun ExploreImageContainer(content: @Composable () -> Unit) {
    Surface(Modifier.size(width = 60.dp, height = 60.dp), RoundedCornerShape(4.dp)) {
        content()
    }
}
