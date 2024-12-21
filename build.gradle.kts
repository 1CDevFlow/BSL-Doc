plugins {
    java
    application
    id("me.qoomon.git-versioning") version "6.4.3"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("io.freefair.lombok") version "8.4"
    id("maven-publish")
    id("org.springframework.boot") version "2.7.10"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "ru.alkoleft.bsl.doc"
gitVersioning.apply {
    refs {
        considerTagsOnBranches = true
        tag("v(?<tagVersion>[0-9].*)") {
            version = "\${ref.tagVersion}\${dirty}"
        }
        branch(".+") {
            version = "\${ref}-\${commit.short}\${dirty}"
        }
    }

    rev {
        version = "\${commit.short}\${dirty}"
    }
}

val JUINT_VERSION = "5.8.2"

val JACKSON_VERSION = "2.15.2"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    // CLI
    implementation("info.picocli", "picocli", "4.7.5")

    // 1c-syntax
    implementation("io.github.1c-syntax", "mdclasses", "develop-SNAPSHOT")
    implementation("com.github.1c-syntax", "utils", "0.5.2")
    implementation("io.github.1c-syntax", "bsl-common-library", "0.5.0")
    implementation("io.github.1c-syntax", "bsl-parser-core", "0.1.0")
    implementation("io.github.1c-syntax", "bsl-parser", "0.24.0") {
        exclude("com.tunnelvisionlabs", "antlr4-annotations")
        exclude("commons-beanutils", "commons-beanutils")
        exclude("com.ibm.icu", "*")
        exclude("org.antlr", "ST4")
        exclude("org.abego.treelayout", "org.abego.treelayout.core")
        exclude("org.antlr", "antlr-runtime")
        exclude("org.glassfish", "javax.json")
    }
    // logging
    implementation("org.slf4j", "slf4j-api", "1.7.30")

    // config load
    implementation("com.fasterxml.jackson.core:jackson-core:$JACKSON_VERSION")
    implementation("com.fasterxml.jackson.core:jackson-databind:$JACKSON_VERSION")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$JACKSON_VERSION")

    // template engine
    implementation("com.github.jknack:handlebars:4.3.1")

    implementation("commons-io", "commons-io", "2.15.1")

    // tests
    testImplementation("org.slf4j", "slf4j-log4j12", "1.7.30")
    testImplementation("org.junit.jupiter:junit-jupiter:$JUINT_VERSION")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$JUINT_VERSION")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$JUINT_VERSION")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed", "standard_error")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:unchecked")
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
}

tasks.bootJar {
    archiveClassifier.set("exec")
}

publishing {
    repositories {
        maven {
            name = "bsldoc"
            url = uri("https://maven.pkg.github.com/alkoleft/bsldoc")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
