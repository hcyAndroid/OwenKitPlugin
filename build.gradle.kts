plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "com.issyzone"
version = "1.0.5-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    // Apache POI 相关依赖
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1") // POI 需要的日志库
    intellijPlatform {
       // create("IC", "2024.2.5")

        create("IC", "2023.2")  // 使用公开版本号 // Meerkat 2024.3.1 的内部构建号
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        //testFramework(TestFrameworkType.Platform)
        // 必须的插件依赖
        bundledPlugin("org.jetbrains.android")  // Android 支持
        bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "231.14494"  // 最低兼容版本
            untilBuild = "243.*"       // 最高兼容版本
        }
        changeNotes = """
         Initial plugin version for Android Studio Meerkat 2024.3.1
    """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
