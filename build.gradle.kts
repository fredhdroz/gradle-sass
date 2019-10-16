plugins {
    kotlin("jvm") version "1.3.50"
    id("com.gradle.plugin-publish") version "0.10.1"
    `maven-publish`
}

group = "com.meiuwa"
version = "1.0.0"
description = "Integrates Sass executable compiler with Gradle"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation("de.undercouch:gradle-download-task:4.0.0")
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

publishing {
    publications {
        create<MavenPublication>("gradle-sass") {
            from(components["java"])
        }
    }
}
