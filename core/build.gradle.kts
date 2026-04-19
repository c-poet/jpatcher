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

dependencies {
    intellijPlatform {
        intellijIdea("2022.3")
        bundledPlugin("com.intellij.database")
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.spring")
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
