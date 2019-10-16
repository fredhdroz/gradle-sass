package com.meiuwa.gradle.sass

import org.gradle.api.Plugin
import org.gradle.api.Project

open class SassPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("sass", SassExtension::class.java, project)
        project.tasks.create("sassCompile", SassTask::class.java)
    }
}
