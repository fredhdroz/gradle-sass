plugins {
    kotlin("jvm") version "1.3.61"
    id("com.gradle.plugin-publish") version "0.10.1"
}

group = "com.meiuwa"
version = "2.0.0"
description = "Integrates Sass executable compiler with Gradle"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation("de.undercouch:gradle-download-task:4.0.2")
}

pluginBundle {
    website = "https://github.com/meiuwa/gradle-sass"
    vcsUrl = "https://github.com/meiuwa/gradle-sass.git"
    plugins {
        create("gradle-sass") {
            id = "com.meiuwa.gradle.sass"
            displayName = "gradle-sass"
            description = "Integrates Sass executable compiler with Gradle"
            tags = listOf("sass", "scss", "css", "compiler", "web")
        }
    }
}
