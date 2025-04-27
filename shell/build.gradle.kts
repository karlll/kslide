plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val springShellVersion = "3.4.0"

dependencies {
    implementation(project(":core"))
    implementation(project(":persistence"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.shell:spring-shell-starter")
    implementation("org.apache.poi:poi:5.4.0")
    implementation("org.apache.poi:poi-ooxml:5.4.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.shell:spring-shell-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.shell:spring-shell-dependencies:$springShellVersion")
    }
}
