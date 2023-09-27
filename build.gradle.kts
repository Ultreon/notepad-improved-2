@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.7.10"
    id("java")
    id("org.panteleyev.jpackageplugin") version "1.5.0"
}

val projectVersion = property("project_version")

group = "com.ultreon"
version = "${projectVersion}+${if (System.getenv("GITHUB_BUILD_NUMBER") == null) "local" else System.getenv("GITHUB_BUILD_NUMBER")}"

val packageVersion = (version as String).replace("+local", ".0").replace("+", ".")

val buildDate = ZonedDateTime.now()

repositories {
    mavenCentral()
}

configurations {
    implementation {
        isCanBeResolved = true
    }
}

dependencies {
//    implementation("com.github.weisj:darklaf-core:3.0.0")
    implementation("com.formdev:flatlaf:3.0")
    implementation("com.formdev:flatlaf-intellij-themes:3.0")
    implementation("com.formdev:flatlaf-extras:3.0")
    implementation("com.formdev:flatlaf-swingx:3.0")
    implementation("commons-lang:commons-lang:2.6")
    implementation("org.drjekyll:fontchooser:2.4")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.miglayout:miglayout-swing:11.0")
    implementation("org.springframework:spring-core:5.3.23")
    testImplementation(kotlin("test"))
    implementation("org.bidib.org.oxbow:swingbits:1.2.2")
}

tasks.jar {
    exclude("META-INF/*.RSA", "META-INF/*.DSA", "META-INF/*.SF")

    //noinspection GroovyAssignabilityCheck
    manifest {
        //noinspection GroovyAssignabilityCheck
        attributes(mapOf(
            Pair("Implementation-Title", "QBubbles"),
            Pair("Implementation-Vendor", "QTech Community"),
            Pair("Implementation-Version", "1.0-indev1"),
            Pair("Main-Class", "com.ultreon.notepadimproved.MainKt"),
            Pair("Multi-Release", "true")
        ))
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.processResources {
    inputs.dir("src/main/resources")

    inputs.property("version", project.version)
    inputs.property("builddate", buildDate.format(DateTimeFormatter.RFC_1123_DATE_TIME))

    filesMatching("docs/**.html") {
        expand("version" to project.version, "builddate" to buildDate.format(DateTimeFormatter.RFC_1123_DATE_TIME))
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.compileJava {
    targetCompatibility = "17"
    sourceCompatibility = "17"
}

task("copyDependencies", Copy::class) {
    from(configurations.runtimeClasspath).into("$buildDir/jars")
}

task("copyJar", Copy::class) {
    from(tasks.jar).into("$buildDir/jars")
}

tasks.jpackage {
    dependsOn("build", "copyDependencies", "copyJar")

    input  = "$buildDir/jars"
    destination = "$buildDir/dist"

    appName = "Notepad Improved 2"
    appVersion = project.version.toString()
    vendor = "Ultreon Team"
    copyright = "Copyright (c) 2022 Ultreon Team"
    runtimeImage = System.getProperty("java.home")

    mainJar = tasks.jar.get().archiveFileName.get()
    mainClass = "com.ultreon.notepadimproved.MainKt"

    destination = "$buildDir/dist"

    licenseFile = "$projectDir/package/LICENSE.txt"
    aboutUrl = "https://github.com/Ultreon/notepad-improved-2"

    javaOptions = listOf("-Dfile.encoding=UTF-8")

    mac {
        icon = "icons/icons.icns"
        macPackageIdentifier = "com.ultreon.notepadimproved"
        macPackageName = "notepad-improved"
        appVersion = packageVersion.replace(Regex("(\\d+\\.\\d+\\.\\d+).*"), "$1")
    }

    linux {
        icon = "icons/icons.png"
        linuxPackageName = "notepad-improved"
        linuxDebMaintainer = "Ultreon Team"
        linuxRpmLicenseType = "Ultreon API License v1.1"
        linuxAppRelease = "2"
        linuxShortcut = true
        appVersion = project.version.toString()
    }

    windows {
        icon = "icons/icons.ico"
        winMenu = true
        winDirChooser = true
        winConsole = false
        winPerUserInstall = true
        winShortcutPrompt = true
        winShortcut = false
        winUpgradeUuid = "0dd76e9b-dd95-495d-876e-9da69c86329c"
        winMenuGroup = "Ultreon Team"
        appVersion = (version as String).replace("+local", ".0").replace("+", ".")
    }
}
