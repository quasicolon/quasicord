/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    `java-library`
    `maven-publish`
	kotlin("jvm")
}

repositories {
	mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
	mavenLocal()
}

dependencies {
    api(libs.jda) {
		exclude(module="opus-java")
	}
	api(libs.jda.ktx)
    api(libs.configurate.yaml)
    api(libs.mongodb.coroutines)
	api(libs.mongodb.kotlinx)
    api(libs.reactor.core)
    api(libs.ulid)
    api(libs.cldr.plural.rules)
    api(libs.snakeyaml)
    api(libs.slf4j)
	api(libs.kotlinx.coroutines.core)
	api(libs.kotlinx.coroutines.reactor)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.logback)
    api(libs.jetbrains.annotations)
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
}

group = "dev.qixils.quasicolon"
version = "1.0.0-SNAPSHOT"
description = "quasicord"

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF8"
}

kotlin {
	jvmToolchain(21)
}
