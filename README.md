# gradle-sass

Integrates Sass executable compiler with Gradle.

- [About](#about)
- [Usage](#usage)
	- [Configuration](#configuration)
	- [Properties entry](#properties-entry)
	- [Defined task](#defined-task)

## About

This is a fork of SalomonBry's [Gradle Sass][1] with some changes and additional features:

- Defaults to any globally accessible Sass implementation ([Ruby Sass][2], [Dart Sass][3], [JavaScript Sass][4], etc.).
- Download and use of official [Dart Sass][3] release is now optional.
- Uses [properties](#properties-entry) file for command-line arguments to support other Sass CLI implementation.
- Minified CSS are automatically suffixed with `.min.css` extension.
- Preserve trailing semi-colon of minified CSS to support JavaFX stylesheets.

Advantages over other Sass implementations in Java:

1. [Number unit][5] calculation...
2. Basically, all the latest features of Sass! :smile:


## Usage

Apply using the plugins DSL:

```gradle
plugins {
    id("com.meiuwa.gradle.sass") version "1.0.0"
}
```

### Configuration

By default, it uses the globally accessible Sass command,
you can change this behavior by overriding the default `sass` extension properties:

<table>
	<tr>
		<th>Property name</th>
		<th>Default value</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><code>executable</code></td>
		<td><code>"sass"</code> (or <code>"sass.bat"</code>)</td>
		<td>The sass executable either globally accessible or an absolute file path</td>
	</tr>
	<tr>
		<td><code>properties</code></td>
		<td><code>"$projectDir/sass.properties"</code></td>
		<td>The properties file containing all supported command-line arguments</td>
	</tr>
	<tr>
		<td><code>preserved</code></td>
		<td><code>false</code></td>
		<td>Preserve the trailing semi-colon of declaration block on compressed CSS</td>
	</tr>
</table>

```gradle
sass {
    executable = "$rootDir/sass"
    properties = "$rootDir/sass.properties"
    preserved = true
}
```

If you prefer to download and use the official [Dart Sass][3] release locally,
you can override the following default properties:

<table>
	<tr>
		<th>Property name</th>
		<th>Default value</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><code>url</code></td>
		<td><code>"https://github.com/sass/dart-sass/releases/download/"</code></td>
		<td>A direct URL of Dart Sass archive</td>
	</tr>
	<tr>
		<td><code>version</code></td>
		<td><code>"1.23.0"</code></td>
		<td>The Dart Sass GitHub release version</td>
	</tr>
	<tr>
		<td><code>output</code></td>
		<td><code>"$rootDir/.sass"</code></td>
		<td>The directory path to save and extract the archive</td>
	</tr>
</table>

```gradle
sass {
    properties = "$rootDir/sass.properties"
    download()
}
```

To use a specific version:

```gradle
sass {
    properties = "$rootDir/sass.properties"
    download {
        version = "1.23.0"
    }
}
```

Or specify a direct URL of Dart Sass archive:

```gradle
sass {
    properties = "$rootDir/sass.properties"
    download {
        url = "https://github.com/sass/dart-sass/releases/download/1.23.0/dart-sass-1.23.0-macos-x64.tar.gz"
        output = "$rootDir/.sass"
    }
}
```

> Note: Currently supports only Dart Sass archive with either `zip` or `tar` format.


### Properties entry

All possible command-line arguments supported by the executable are defined as entries in a properties file:

For flags that require a value:

```properties
--style=compressed
```

And for single flags that doesn't require a value:

```properties
--no-source-map=
```


### Defined task

The `sassCompile` task handles both the download and compilation process.
The default input source and output directories are also defined in this task.
It is a type of [SourceTask][6] in which sources can be configured with an
`include` and `exclude` pattern set to filter files within the directory:

Groovy DSL:

```gradle
sassCompile {
    source = fileTree("src/main/sass")
    include("**/*.sass", "**/*.scss")
    exclude("**/_*.sass", "**/_*.scss")
    output = file("build/sass")
}
```

Kotlin DSL:

```gradle
tasks.named("sassCompile") {
    source = fileTree("src/main/sass")
    include("**/*.sass", "**/*.scss")
    exclude("**/_*.sass", "**/_*.scss")
    output = file("build/sass")
}
```


[1]: https://github.com/SalomonBrys/gradle-sass
[2]: https://sass-lang.com/ruby-sass
[3]: https://github.com/sass/dart-sass
[4]: https://www.npmjs.com/package/sass
[5]: http://www.sass-lang.com/documentation/values/numbers#units
[6]: https://docs.gradle.org/current/dsl/org.gradle.api.tasks.SourceTask.html
