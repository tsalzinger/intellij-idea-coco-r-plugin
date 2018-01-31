import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.grammarkit.GrammarKitPluginExtension
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.PublishTask
import org.jetbrains.kotlin.backend.common.onlyIf
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
    dependencies {
        classpath("com.github.hurricup:gradle-grammar-kit-plugin:2017.1.1")
    }
}

plugins {
    idea
    java
    kotlin("jvm") version "1.2.20"
    id("org.jetbrains.intellij") version "0.2.18"
    id("de.undercouch.download") version "3.3.0"
}

group = "io.scheinecker.intellij"
version = "1.0.0"

apply {
    plugin("org.jetbrains.grammarkit")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

java.sourceSets {
    getByName("main").java.srcDirs("$buildDir/gen")
}

idea {
    module {
        generatedSourceDirs.add(file("$buildDir/gen"))
    }
}

configure<GrammarKitPluginExtension> {
    jflexRelease = "1.7.0"
    grammarKitRelease = "2017.1.1"
}

val downloadCocoJar = task<Download>("downloadCocoJar") {
    val cocoVersion = "2017-02-02"
    val cocoLib = file("$buildDir/external-libs/Coco-$cocoVersion.jar")
    src("http://ssw.jku.at/coco/Java/Coco.jar")
    dest(cocoLib)
    onlyIf { !cocoLib.exists() }
}

val generateCocoParser = task<GenerateParser>("generateCocoParser") {
    source = "src/main/resources/at/scheinecker/intellij/coco/Coco.bnf"
    targetRoot = "$buildDir/gen"
    pathToParser = "/at/scheinecker/intellij/coco/parser/CocoParser.java"
    pathToPsiRoot = "/at/scheinecker/intellij/coco/psi"
}

val generateCocoLexer = task<GenerateLexer>("generateCocoLexer") {
    source = "src/main/kotlin/at/scheinecker/intellij/coco/Coco.flex"
    targetDir = "$buildDir/gen/at/scheinecker/intellij/coco/"
    targetClass = "CocoLexer"
    dependsOn(generateCocoParser)
}

repositories {
    mavenCentral()
}

dependencies {
    compile(fileTree("$buildDir/external-libs"))
    testCompile("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.2"
        apiVersion = "1.2"
    }
}
intellij {
    version = "IC-2017.3"
    downloadSources = true
}

tasks.withType<PatchPluginXmlTask> {
    version(project.version)
}

tasks.withType<JavaCompile> {
    dependsOn(
            downloadCocoJar, generateCocoLexer
    )
}

tasks.withType<KotlinCompile> {
    dependsOn(
            downloadCocoJar, generateCocoLexer
    )
}

tasks.withType<PublishTask> {
    username(project.findProperty("publishUsername"))
    password(project.findProperty("publishPassword"))
}