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

package androidx.compose.samples.crane

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.base.Launch
import androidx.compose.samples.crane.util.UnsplashSizingInterceptor
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

@HiltAndroidApp
class CraneApplication : Application(), ImageLoaderFactory {
    class dataCenterClass {
        var ShowTip by mutableStateOf(false)

        val Ready by mutableStateOf(false).also {
            Launch(true) {
                delay(5000)
                ShowTip = true
                delay(5000)
                it.value = true
            }
        }

        private fun currentTimeString(): String =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault())
                .format(Date(System.currentTimeMillis()))

        val Clock by mutableStateOf(currentTimeString()).apply {
            Launch(true) {
                flow {
                    while (true) {
                        emit(currentTimeString())
                        delay(1)
                    }
                }.collect { this@apply.value = it }
            }
        }
    }

    private val dataCenter = dataCenterClass()

    companion object {
        var App: CraneApplication by object {
            private val f = AtomicReference<CraneApplication>()
            operator fun getValue(thisRef: Any?, property: KProperty<*>): CraneApplication {
                return f.get()
            }

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: CraneApplication) {
                f.set(value)
            }
        }
            private set
        val DataCenter
            get() = App.dataCenter
    }

    /**
     * Create the singleton [ImageLoader].
     * This is used by [rememberImagePainter] to load images in the app.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(UnsplashSizingInterceptor)
            }
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        App = this
    }
}
