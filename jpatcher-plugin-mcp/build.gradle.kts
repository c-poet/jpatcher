repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/central")
    }
    mavenLocal()
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.10.4"
}

group = "cn.cpoet.jpatcher"
version = "M2026.5.0"

dependencies {
    implementation(project(":core")) {
        exclude("*", "*")
    }
    implementation(project(":impl223")) {
        exclude("*", "*")
    }
    implementation(project(":impl243")) {
        exclude("*", "*")
    }

    intellijPlatform {
        intellijIdea("2025.3")

        bundledPlugin("com.intellij.database")
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.spring")
        plugin("com.intellij.mcpServer:253.29346.142")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("253")
        untilBuild.set("261.*")
        changeNotes.set(providers.provider {
            rootProject.file("changes.html").readText()
        })
    }

    signPlugin {
        certificateChain.set(System.getenv("IDEA_PLUGIN_CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("IDEA_PLUGIN_PRIVATE_KEY"))
        password.set(System.getenv("IDEA_PLUGIN_PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("IDEA_PLUGIN_PUBLISH_TOKEN"))
    }
}

