plugins {
    id("java")
    alias(libs.plugins.fabric.loom)
}

group = "com.filkond.pigtagger"

java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16
java.toolchain.languageVersion = JavaLanguageVersion.of(16)

repositories {
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft.mc1204)
    mappings(loom.officialMojangMappings())

    compileOnly(libs.jetbrains.annotations)
    implementation(libs.gson)
    implementation(libs.slf4j)
}