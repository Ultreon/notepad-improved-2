@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.7.10"
    id("java")
}

group = "com.ultreon"
version = properties["version"] as String

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
    implementation("com.formdev:flatlaf:2.4")
    implementation("com.formdev:flatlaf-intellij-themes:2.4")
    implementation("com.formdev:flatlaf-extras:2.4")
    implementation("com.formdev:flatlaf-swingx:2.4")
    implementation("commons-lang:commons-lang:2.6")
//    implementation("com.formdev:flatlaf-jide-oss:2.4")
    implementation("org.drjekyll:fontchooser:2.4")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.miglayout:miglayout-swing:11.0")
    implementation("org.springframework:spring-core:5.3.23")
    testImplementation(kotlin("test"))
    implementation("org.bidib.org.oxbow:swingbits:1.2.2")
}

tasks.jar {
    for (it in configurations.implementation.get().files) {
        if (!it.path.startsWith(projectDir.path)) {
            if (it.isDirectory) {
                from(it)
            } else {
                from(zipTree(it))
            }
        }
    }

    exclude("META-INF/*.RSA", "META-INF/*.DSA", "META-INF/*.SF")

    //noinspection GroovyAssignabilityCheck
    manifest {
        //noinspection GroovyAssignabilityCheck
        attributes(mapOf(
            Pair("Implementation-Title", "QBubbles"),
            Pair("Implementation-Vendor", "QTech Community"),
            Pair("Implementation-Version", "1.0-indev1"),
            Pair("Main-Class", "me.qboi.texteditor.MainKt"),
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
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileJava {
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
}