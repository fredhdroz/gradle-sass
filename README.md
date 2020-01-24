# gradle-sass

Integrates Sass executable compiler with Gradle.

- [Version 2.0.0](#version-200)
- [Usage](#usage)
	- [Configuration](#configuration)
	- [Properties entry](#properties-entry)
	- [Defined task](#defined-task)
- [Example](#example)

## Version 2.0.0

- Code clean up (minimized unnecessary lines of code)
- Updated dependencies, Kotlin, and Gradle version
- Updated default Dart Sass version to `1.25.0`
- Defaults to download Dart Sass executable to project root
- Added `suffix` extension property for custom CSS output extension
- [Properties entry](#properties-entry) for command-line arguments is set to optional


## Usage

Apply using the plugins DSL:

```gradle
plugins {
    id("com.meiuwa.gradle.sass") version "2.0.0"
}
```

### Configuration

Default `sass` extension properties:

<table>
	<tr>
		<th>Property name</th>
		<th>Default value</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><code>executable</code></td>
		<td><code>"sass"</code> (or <code>"sass.bat"</code>)</td>
		<td>The Sass executable either globally accessible or an absolute file path (defaults to Dart Sass executable if <code>download</code> is set to <code>enabled<code>)</td>
	</tr>
	<tr>
		<td><code>properties</code></td>
		<td><code>"$rootDir/sass.properties"</code></td>
		<td>The properties file containing all other supported command-line arguments (optional)</td>
	</tr>
	<tr>
		<td><code>suffix</code></td>
		<td><code>"css"</code></td>
		<td>Custom CSS output extension (define the compiled CSS extension)</td>
	</tr>
	<tr>
		<td><code>preserved</code></td>
		<td><code>false</code></td>
		<td>Preserve the trailing semi-colon of declaration block on compressed CSS</td>
	</tr>
</table>

Default Dart Sass `download` extension properties:

<table>
	<tr>
		<th>Property name</th>
		<th>Default value</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><code>url</code></td>
		<td><code>"https://github.com/sass/dart-sass/releases/download"</code></td>
		<td>A direct URL of Dart Sass archive (defaults to Dart Sass GitHub release which automatically determine the appropriate executable for the current running platform)</td>
	</tr>
	<tr>
		<td><code>version</code></td>
		<td><code>"1.25.0"</code></td>
		<td>The Dart Sass GitHub release version (not required if <code>url</code> is set to direct download)</td>
	</tr>
	<tr>
		<td><code>output</code></td>
		<td><code>"$rootDir/.sass"</code></td>
		<td>The directory path to save and extract the archive</td>
	</tr>
    <tr>
		<td><code>enabled</code></td>
		<td><code>true</code></td>
		<td>Whether download and use the specified Dart Sass release, or not (requires <code>executable</code> to be accessible if set to <code>false</code>)</td>
	</tr>
</table>

`sass` extension properties configuration:

```gradle
sass {
    properties = "$rootDir/sass.properties"
    suffix = 'min.css'
    preserved = true
    download {
        version = '1.25.0'
        enabled = true
    }
}
```

Dart Sass is the default executable, if you want to use any global executable instead:

```gradle
sass {
    executable = 'sass'
    download {
        enabled = false
    }
}
```

Or if you just want to use a different version of Dart Sass:

```gradle
sass {
    download {
        version = '1.25.0'
    }
}
```

Or specify a direct URL of Dart Sass archive with `zip` or `tar` format:

```gradle
sass {
    properties = "$rootDir/sass.properties"
    download {
        url = 'https://github.com/sass/dart-sass/releases/download/1.25.0/dart-sass-1.25.0-macos-x64.tar.gz'
        output = "$rootDir/.sass"
    }
}
```


### Properties entry

All other possible command-line arguments supported by the executable are defined as entries in a properties file. This is to support other implementation of Sass CLI (see [`Dart Sass CLI`](https://sass-lang.com/documentation/cli/dart-sass) documentation).

For flags that require a value:

```properties
--style=compressed
```

And for single flags that doesn't require a value:

```properties
--no-source-map=
```


### Defined task

The `sassCompile` task handles both the download and compilation process. It is a type of [SourceTask](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.SourceTask.html)
in which output and source input must be defined manually, and source could also be configured with an `include` and `exclude` pattern set to filter files within the directory:

Groovy DSL configuration:

```gradle
sassCompile {
    output = file("build/sass")
    source = fileTree("src/main/sass")
    include("**/*.sass", "**/*.scss")
    exclude("**/_*.sass", "**/_*.scss")
}
```

Kotlin DSL configuration:

```gradle
import com.meiuwa.gradle.sass.SassTask
//...

tasks.named<SassTask>("sassCompile") {
    output = file("build/sass")
    source = fileTree("src/main/sass")
    include("**/*.sass", "**/*.scss")
    exclude("**/_*.sass", "**/_*.scss")
}
```

## Example

See [this](https://github.com/meiuwa/javafx-template) JavaFX template for an example usage.
