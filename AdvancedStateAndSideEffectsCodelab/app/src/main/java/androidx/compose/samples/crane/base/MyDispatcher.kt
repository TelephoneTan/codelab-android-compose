package androidx.compose.samples.crane.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.coroutines.EmptyCoroutineContext

private val myDispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val emptyScope = CoroutineScope(EmptyCoroutineContext)

fun Launch(
    main: Boolean,
    scope: CoroutineScope = emptyScope,
    changeDispatcher: Boolean = true,
    forceSupervisor: Boolean = true,
    block: suspend CoroutineScope.() -> Unit
) = scope.launch(context = scope.coroutineContext.let {
    var context = it
    if (changeDispatcher) {
        context += (if (main) Dispatchers.Main.immediate else myDispatcher)
    }
    if (forceSupervisor) {
        context += SupervisorJob(context.job)
    }
    context
}, start = CoroutineStart.DEFAULT, block = block)

fun CoroutineScope.Launch(
    main: Boolean,
    changeDispatcher: Boolean = true,
    forceSupervisor: Boolean = true,
    block: suspend CoroutineScope.() -> Unit
) = Launch(main, this, changeDispatcher, forceSupervisor, block)