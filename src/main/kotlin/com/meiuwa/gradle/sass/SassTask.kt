package com.meiuwa.gradle.sass

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.os.OperatingSystem
import java.util.Properties

open class SassTask : SourceTask() {
    @OutputDirectory var output = project.buildDir.resolve("sass")

    @TaskAction fun compile() {
        val ext = project.extensions.findByName("sass") as SassExtension
        if (ext.download.enabled) download(ext.download.url, ext.download.version, ext.download.output)
        val properties = load(ext.properties)
        val arguments = parse(properties)
        val executable = when {
            ext.download.enabled ->
                project.file("${ext.download.output}/dart-sass/$DEFAULT_SASS_EXECUTABLE").canonicalPath
            else -> ext.executable
        }
        val preserved = when {
            properties != null ->
                properties.containsKey("--style") && "compressed" == properties.getProperty("--style") && ext.preserved
            else -> false
        }
        execute(executable, arguments, ext.suffix, preserved)
    }

    private fun download(baseUrl: String, version: String, output: String) {
        // construct:
        val (zip, tar) = "zip" to "tar.gz"
        var (os, ext) = when {
            OperatingSystem.current().isLinux -> "linux" to tar
            OperatingSystem.current().isMacOsX -> "macos" to tar
            OperatingSystem.current().isWindows -> "windows" to zip
            else -> throw IllegalStateException("Unsupported Operating System")
        }
        val arch = if ("64" in System.getProperty("os.arch")) "x64" else "ia32"
        var archive = "dart-sass-$version-$os-$arch.$ext"
        val url = when (baseUrl) {
            DEFAULT_DOWNLOAD_URL -> "$baseUrl/$version/$archive"
            else -> baseUrl.also {
                archive = it.substring(it.lastIndexOf("/") + 1)
                ext = archive.substring(archive.lastIndexOf(".") + 1)
                if (ext != zip || ext != tar) throw IllegalArgumentException("Invalid URL or unsupported archive format")
            }
        }
        val release = project.file("$output/$archive")
        // download:
        project.tasks.register("sassDownload", Download::class.java).get().apply {
            src(url)
            dest(release)
            downloadTaskDir(output)
            tempAndMove(true)
            overwrite(false)
            download()
        }
        // extract:
        project.copy {
            it.from(when (ext) {
                zip -> project.zipTree(release)
                else -> project.tarTree(release)
            })
            it.into(output)
        }
    }

    private fun load(properties: String): Properties? = when {
        !project.file(properties).exists() -> null
        else -> Properties().apply { load(project.file(properties).inputStream()) }
    }

    private fun parse(properties: Properties?): MutableList<String> = when (properties) {
        null -> arrayListOf()
        else -> arrayListOf<String>().apply {
            for ((key, value) in properties) {
                val entry = when (key) {
                    "--load-path" -> "$key=${project.file(value).canonicalPath}"
                    else -> if ((value as String).isEmpty()) (key as String) else "$key=$value"
                }
                add(entry)
            }
        }
    }

    private fun execute(executable: String, arguments: MutableList<String>, suffix: String, preserved: Boolean){
        source.visit { v ->
            if (v.isDirectory) return@visit
            val css = project.file("$output/${v.relativePath.parent.pathString}/${v.file.nameWithoutExtension}.$suffix")
            arguments.addAll(arrayListOf(v.file.canonicalPath, css.canonicalPath))
            project.exec { e ->
                e.executable(executable)
                e.args(arguments)
            }
            if (preserved) css.writeText(css.readText().replace(CLOSING_CSS_DECLARATION_BLOCK, ";}"))
        }
    }
}
