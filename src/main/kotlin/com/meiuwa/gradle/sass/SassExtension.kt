package com.meiuwa.gradle.sass

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

open class SassExtension(project: Project) {
    var executable = DEFAULT_SASS_EXECUTABLE

    var properties = "${project.rootDir}/sass.properties"

    var suffix = DEFAULT_CSS_OUTPUT_SUFFIX

    var preserved = false

    data class Download(var url: String, var version: String, var output: String, var enabled: Boolean)

    internal var download: Download = Download(
        url = DEFAULT_DOWNLOAD_URL,
        version = DEFAULT_SASS_VERSION,
        output = "${project.rootDir}/.sass",
        enabled = true
    )

    fun download(action: Action<Download>) { action.execute(download) }

    fun download(closure: Closure<Download>) { ConfigureUtil.configure(closure, download) }
}
