package com.kylemadsen.core.koin

import com.kylemadsen.core.BuildConfig
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import kotlin.properties.Delegates

/**
 * Sometimes you need to create a module beyond onCreate (such as map load).
 * Remember to unload the module when done.
 *
 * Note that this class is not needed with proper Koin Scopes. I only kept it around
 * in case I wanted it when it's not tied to an activity scope
 */
class KoinLateLoadModule {
    private lateinit var module: Module
    private var callingThread by Delegates.notNull<Long>()
    fun load(module: Module) {
        this.module = module
        this.callingThread = Thread.currentThread().id
        loadKoinModules(module)
    }
    fun unload() {
        if (::module.isInitialized) {
            checkThread()
            unloadKoinModules(this.module)
        }
    }

    private fun checkThread() {
        if (BuildConfig.DEBUG && callingThread != Thread.currentThread().id) {
            // Calling these lifecycle events from different
            // threads will result in unknown behavior
            throw IllegalStateException("Make sure you call load and unload from the same thread")
        }
    }
}