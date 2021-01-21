import de.undercouch.gradle.tasks.download.Download
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // IntelliJ IDEA configuration plugin
    idea
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.4.21-2"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "0.6.5"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.0.1"
    // detekt linter - read more: https://detekt.github.io/detekt/kotlindsl.html
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    // gradle-grammar-kit-plugin - read more: https://github.com/JetBrains/gradle-grammar-kit-plugin
    id("org.jetbrains.grammarkit") version "2020.3.2"
    // gradle-download-task - read more: https://github.com/michel-kraemer/gradle-download-task
    id("de.undercouch.download") version "4.1.1"
}

// Import variables from gradle.properties file
val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val pluginVerifierIdeVersions: String by project

val platformType: String by project
val platformVersion: String by project
val platformDownloadSources: String by project
val grammmarKitOutputDirectory = "$buildDir/gen"

group = pluginGroup
version = pluginVersion

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation(fileTree("$buildDir/external-libs"))
    testImplementation("junit:junit:4.12")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.15.0")
}

sourceSets {
    main {
        java {
            // mark the generated files from the grammar kit plugin as sources
            srcDirs(grammmarKitOutputDirectory)
        }
    }
}

idea {
    module {
        // ensure IDEA correctly marks the source folder as generated
        generatedSourceDirs.add(file(grammmarKitOutputDirectory))
    }
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    // see https://github.com/JetBrains/intellij-platform-plugin-template/issues/29 for details regarding "this@Build_gradle"
    pluginName = this@Build_gradle.pluginName.replace("/", "-")
    version = platformVersion
    type = platformType
    downloadSources = platformDownloadSources.toBoolean()
    updateSinceUntilBuild = true

//  Plugin Dependencies:
//  https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html
//
    setPlugins(
        "java",
        // https://plugins.jetbrains.com/plugin/227-psiviewer/versions
        "PsiViewer:203-SNAPSHOT"
    )
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

val downloadCocoJar = task<Download>("downloadCocoJar") {
    val cocoVersion = "2017-02-02"
    val cocoLib = file("$buildDir/external-libs/Coco-$cocoVersion.jar")
    src("https://ssw.jku.at/Research/Projects/Coco/Java/Coco.jar")
    dest(cocoLib)
    onlyIf { !cocoLib.exists() }
}

val generateCocoParser = task<GenerateParser>("generateCocoParser") {
    source = "src/main/resources/me/salzinger/intellij/coco/Coco.bnf"
    targetRoot = grammmarKitOutputDirectory
    pathToParser = "/me/salzinger/intellij/coco/parser/CocoParser.java"
    pathToPsiRoot = "/me/salzinger/intellij/coco/psi"
    purgeOldFiles = true
}

val generateCocoLexer = task<GenerateLexer>("generateCocoLexer") {
    dependsOn(generateCocoParser)

    source = "src/main/kotlin/me/salzinger/intellij/coco/Coco.flex"
    targetDir = "$grammmarKitOutputDirectory/me/salzinger/intellij/coco/"
    targetClass = "CocoLexer"
    purgeOldFiles = true
}

tasks {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        dependsOn(
            downloadCocoJar, generateCocoLexer
        )

        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    listOf("compileKotlin", "compileTestKotlin").forEach {
        getByName<KotlinCompile>(it) {
            dependsOn(
                downloadCocoJar, generateCocoLexer
            )

            kotlinOptions.jvmTarget = "1.8"
        }
    }

    withType<Detekt> {
        jvmTarget = "1.8"
    }

    patchPluginXml {
        version(pluginVersion)
        sinceBuild(pluginSinceBuild)
        untilBuild(pluginUntilBuild)

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription(
            closure {
                File("./README.md").readText().lines().run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"

                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md file:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run { markdownToHTML(this) }
            }
        )

        // Get the latest available change notes from the changelog file
        changeNotes(
            closure {
                changelog.getLatest().toHTML()
            }
        )
    }

    runPluginVerifier {
        ideVersions(pluginVerifierIdeVersions)
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token(System.getenv("PUBLISH_TOKEN"))
        channels(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
    }
}
