package com.meiuwa.gradle.sass

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

internal fun <T> T.apply(action: Action<T>): T = this.also { action.execute(this) }

internal fun <T> T.apply(closure: Closure<*>): T = ConfigureUtil.configure(closure, this)
