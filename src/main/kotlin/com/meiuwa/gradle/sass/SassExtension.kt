package com.meiuwa.gradle.sass

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project

open class SassExtension(project: Project) {
    var executable: String = DEFAULT_SASS_EXECUTABLE

    var properties: String = "${project.projectDir}/sass.properties"

    var preserved: Boolean = false

    data class Download(var url: String, var version: String, var output: String)

    private val _download: Download = Download(
        url = DEFAULT_DOWNLOAD_URL,
        version = DEFAULT_SASS_VERSION,
        output = "${project.rootDir}/.sass"
    )

    internal var download: Download? = null

    fun download(action: Action<Download>) {
        download = _download.apply(action)
    }

    fun download(closure: Closure<*>) {
        download = _download.apply(closure)
    }

    fun download() {
        download = _download
    }
}
