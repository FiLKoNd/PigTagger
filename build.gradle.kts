plugins {
    id("java")
    alias(libs.plugins.fabric.loom)
}

group = "com.filkond.pigtagger"

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
java.toolchain.languageVersion = JavaLanguageVersion.of(17)

repositories {
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft.mc1204)
    mappings(loom.officialMojangMappings())

    compileOnly(libs.jetbrains.annotations)
    implementation(libs.gson)
}