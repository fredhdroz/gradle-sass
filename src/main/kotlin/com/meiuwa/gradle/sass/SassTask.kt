package com.meiuwa.gradle.sass

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.os.OperatingSystem
import java.io.IOException
import java.util.Properties

open class SassTask : SourceTask() {
    @OutputDirectory var output = project.buildDir.resolve("sass")

    init {
        description = "Compiles main Sass source."
        source = project.fileTree("${project.projectDir}/src/main/sass") {
            it.include("**/*.sass", "**/*.scss")
            it.exclude("**/_*.sass", "**/_*.scss")
        }
    }

    @TaskAction fun compile() {
        val ext = project.extensions.findByName("sass") as SassExtension
        val properties = load(ext.properties)
        if (ext.download != null) download(
            ext.download!!.url,
            ext.download!!.version,
            ext.download!!.output
        )
        val executable = when {
            ext.download != null ->
                project.file("${ext.download!!.output}/dart-sass/$DEFAULT_SASS_EXECUTABLE").canonicalPath
            else -> ext.executable
        }
        val arguments = parse(properties)
        val minify = properties.containsKey("--style") &&
            "compressed" == properties.getProperty("--style")
        val preserved = minify && ext.preserved
        execute(executable, arguments, minify, preserved)
    }

    private fun download(baseUrl: String, version: String, outputDir: String) {
        if (!project.file("${outputDir}/dart-sass").exists()) {
            val (zip, tar) = "zip" to "tar.gz"
            var (os, ext) = when {
                OperatingSystem.current().isLinux -> "linux" to tar
                OperatingSystem.current().isMacOsX -> "macos" to tar
                OperatingSystem.current().isWindows -> "windows" to zip
                else -> throw IllegalStateException("Unsupported Operating System")
            }
            val arch = if ("64" in System.getProperty("os.arch")) "x64" else "ia32"
            var archive = "dart-sass-$version-$os-$arch.$ext"
            val separator = if (baseUrl.endsWith("/")) "" else "/"
            val url = when (baseUrl) {
                "$DEFAULT_DOWNLOAD_URL$separator" -> "$baseUrl$separator$version/$archive"
                else -> baseUrl.also {
                    archive = it.substring(it.lastIndexOf("/") + 1)
                    ext = archive.substring(archive.lastIndexOf(".") + 1)
                }
            }
            val release = project.file("$outputDir/$archive")

            project.tasks.register("sassDownload", Download::class.java).get().also {
                it.src(url)
                it.dest(release)
                it.downloadTaskDir(outputDir)
                it.tempAndMove(true)
                it.overwrite(false)
                it.download()
            }

            project.copy {
                it.from(when (ext) {
                    zip -> project.zipTree(release)
                    else -> project.tarTree(release)
                })
                it.into(outputDir)
            }
        }
    }

    private fun load(properties: String): Properties {
        return Properties().also { it.load(project.file(properties).inputStream()) }
    }

    private fun parse(properties: Properties): MutableList<String> {
        return arrayListOf<String>().also { list ->
            properties.stringPropertyNames().forEach { key ->
                val value = properties.getProperty(key).let { value ->
                    when (key) {
                        "--load-path" -> project.file(value).let { path ->
                            if (!path.exists() || !path.isDirectory) {
                                throw IOException("Sass properties entry: $key=$value could not be resolved")
                            }
                            path.canonicalPath
                        }
                        else -> value
                    }
                }
                list.add( if (value.isEmpty()) key else "$key=$value" )
            }
        }
    }

    private fun execute(executable: String, arguments: MutableList<String>, minify: Boolean, preserved: Boolean){
        source.visit { v ->
            if (v.isDirectory) return@visit
            val css = project.file("$output/${v.relativePath.parent.pathString}/${v.file.nameWithoutExtension}".let { n ->
                if (minify) "$n.min.css" else "$n.css"
            })
            val list = arrayListOf(v.file.canonicalPath, css.canonicalPath).also { l -> l.addAll(arguments) }
            project.exec { e ->
                e.executable(executable)
                e.args(list)
            }
            if (preserved) css.writeText(css.readText().replace(CLOSING_DECLARATION_BLOCK, ";}"))
        }
    }
}
