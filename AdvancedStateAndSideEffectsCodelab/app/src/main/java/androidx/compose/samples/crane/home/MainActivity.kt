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

package androidx.compose.samples.crane.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.samples.crane.CraneApplication.Companion.DataCenter
import androidx.compose.samples.crane.base.Launch
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CraneTheme {
                MainScreen(onExploreItemClicked = {
                    launchDetailsActivity(
                        context = this,
                        item = it
                    )
                }, timeStr = DataCenter.Clock)
            }
        }
    }

    @Composable
    private fun MainScreen(onExploreItemClicked: OnExploreItemClicked, timeStr: String) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(scaffoldState = scaffoldState) {
//            val currentTimeStr by rememberUpdatedState(newValue = timeStr)
//            LaunchedEffect(1) {
//                Launch(false) {
//                    delay(5000)
////                    scaffoldState.snackbarHostState.showSnackbar(currentTimeStr)
//                    scaffoldState.snackbarHostState.showSnackbar(timeStr)
//                }
//            }
            if (DataCenter.ShowTip) {
                LaunchedEffect(1) {
                    Launch(true) {
                        scaffoldState.snackbarHostState.showSnackbar(timeStr)
                        DataCenter.ShowTip = false
                    }
                }
            }
            Surface(color = MaterialTheme.colors.primary) {
                Box(
                    modifier = Modifier.padding(
                        bottom = it.calculateBottomPadding(),
                        top = 40.dp,
                        start = 20.dp
                    )
                ) {
                    val scope = rememberCoroutineScope()
                    if (DataCenter.Ready) CraneHome(onExploreItemClicked = onExploreItemClicked) else {
                        LandingScreen({})
                        DisposableEffect(1) {
                            onDispose {
                                scope.Launch(true) {
                                    scaffoldState.snackbarHostState.showSnackbar("加载完成！")
                                }
                            }
                        }
                    }
                    Text(text = timeStr)
                }
            }
        }
    }
}
