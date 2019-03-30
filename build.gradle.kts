
import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.PublishTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    java
    kotlin("jvm") version "1.2.61"
    id("org.jetbrains.intellij") version "0.3.7"
    id("org.jetbrains.grammarkit") version "2018.1.7"
    id("de.undercouch.download") version "3.3.0"
}

group = "io.scheinecker.intellij"
version = "1.2.0-SNAPSHOT"

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
    version = "IC-2019.1"
    downloadSources = true
    setPlugins("PsiViewer:3.28.93")
}

tasks.withType<PatchPluginXmlTask> {
    version(project.version)
    untilBuild("191.*")
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
    val projectVersionString = "${project.version}"
    if (projectVersionString.contains("-")) {
        channels(projectVersionString.substring(projectVersionString.indexOf("-") + 1))
    }
    username(project.findProperty("publishUsername"))
    password(project.findProperty("publishPassword"))
}