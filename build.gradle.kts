/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    `java-library`
    `maven-publish`
	id("io.freefair.lombok") version "8.11"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.net.dv8tion.jda) {
		exclude(module="opus-java")
	}
    api(libs.org.spongepowered.configurate.yaml)
    api(libs.org.mongodb.mongodb.driver.reactivestreams)
    api(libs.io.projectreactor.reactor.core)
    api(libs.de.huxhorn.sulky.de.huxhorn.sulky.ulid)
    api(libs.net.xyzsd.plurals.cldr.plural.rules)
    api(libs.org.yaml.snakeyaml)
    api(libs.org.reflections.reflections)
    api(libs.org.slf4j.slf4j.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testImplementation(libs.ch.qos.logback.logback.classic)
    compileOnly(libs.org.jetbrains.annotations)
}

group = "dev.qixils.quasicolon"
version = "1.0.0-SNAPSHOT"
description = "quasicord"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

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