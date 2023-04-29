plugins {
    java
    application
    id("io.freefair.lombok") version "6.6.1"
}

group = "org.example"
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
    withSourcesJar()
    withJavadocJar()
}

java.sourceSets["main"].java {
    srcDir("src/main/java")
}

dependencies {
    implementation("com.github.1c-syntax", "bsl-language-server", "0.20.0")

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
    implementation("org.apache.velocity:velocity-engine-core:2.3")
    implementation("org.apache.velocity:velocity-tools:2.0")

    // cli
    implementation("info.picocli:picocli:4.7.3")

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
