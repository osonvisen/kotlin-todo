plugins {
    kotlin("jvm") version "2.4.20"
    application
}

application {
    mainClass = "TodoKt"
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.54.1.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}