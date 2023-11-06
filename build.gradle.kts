import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
    kotlin("plugin.allopen") version "1.8.22"
    kotlin("kapt") version "1.8.22"
}

group = "com.tommy"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("commons-validator:commons-validator:1.7")
    implementation("org.redisson:redisson-spring-boot-starter:3.24.3")

    implementation("com.h2database:h2:2.2.224")

    kapt ("com.querydsl:querydsl-apt:5.0.0:jakarta")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
    testImplementation("it.ozimov:embedded-redis:0.7.3") {
        exclude("org.slf4j", "slf4j-simple")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/sources/annotationProcessor/java/main")
        }
    }
}
