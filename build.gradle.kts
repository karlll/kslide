plugins {
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    id("org.springframework.boot") version "3.5.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.ninjacontrol"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// Individual module configurations are now in their respective build.gradle.kts files
