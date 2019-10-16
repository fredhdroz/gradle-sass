package com.meiuwa.gradle.sass

import org.gradle.internal.os.OperatingSystem

internal val CLOSING_DECLARATION_BLOCK = Regex("(?<!})}")

internal const val DEFAULT_DOWNLOAD_URL = "https://github.com/sass/dart-sass/releases/download/"

internal const val DEFAULT_SASS_VERSION = "1.23.0"

internal val DEFAULT_SASS_EXECUTABLE = if (OperatingSystem.current().isWindows) "sass.bat" else "sass"
