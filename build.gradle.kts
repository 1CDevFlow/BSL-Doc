plugins {
    java
    application
    id("io.freefair.lombok") version "6.6.1"
    id("maven-publish")
    id("org.springframework.boot") version "2.7.10"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "ru.alkoleft.bsl.doc"
version = "0.1.0-SNAPSHOT"

val JUINT_VERSION = "5.8.2"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

java.sourceSets["main"].java {
    srcDir("src/main/java")
}

dependencies {
    implementation("info.picocli", "picocli", "4.7.5")

    implementation("com.github.1c-syntax", "mdclasses", "develop-SNAPSHOT")
    implementation("com.github.1c-syntax", "utils", "0.5.1")
    implementation("io.github.1c-syntax", "supportconf", "0.12.1")
    implementation("com.github.1c-syntax", "bsl-parser", "develop-SNAPSHOT") {
        exclude("com.tunnelvisionlabs", "antlr4-annotations")
        exclude("com.ibm.icu", "*")
        exclude("org.antlr", "ST4")
        exclude("org.abego.treelayout", "org.abego.treelayout.core")
        exclude("org.antlr", "antlr-runtime")
        exclude("org.glassfish", "javax.json")
    }

    implementation("commons-io", "commons-io", "2.14.0")

    // template engine
    implementation("com.github.jknack:handlebars:4.3.1")

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