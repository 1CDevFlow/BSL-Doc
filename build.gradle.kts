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
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

java.sourceSets["main"].java {
    srcDir("src/main/java")
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("info.picocli:picocli-spring-boot-starter:4.7.1")

    implementation("io.github.1c-syntax", "bsl-language-server", "0.20.0")

    implementation("com.github.1c-syntax", "mdclasses", "0.10.3")
    implementation("io.github.1c-syntax", "bsl-common-library", "f6714e4e")
    implementation("io.github.1c-syntax", "supportconf", "0.1.1") {
        exclude("io.github.1c-syntax", "bsl-common-library")
    }
    implementation("com.github.1c-syntax", "bsl-parser", "167aaad827322e09ccde4658a71152dad234de4b") {
        exclude("com.tunnelvisionlabs", "antlr4-annotations")
        exclude("com.ibm.icu", "*")
        exclude("org.antlr", "ST4")
        exclude("org.abego.treelayout", "org.abego.treelayout.core")
        exclude("org.antlr", "antlr-runtime")
        exclude("org.glassfish", "javax.json")
    }

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