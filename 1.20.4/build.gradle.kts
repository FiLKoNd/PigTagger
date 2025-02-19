plugins {
    alias(libs.plugins.fabric.loom)
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
java.toolchain.languageVersion = JavaLanguageVersion.of(17)
group = "com.filkond.pigtagger"
base.archivesName = "PigTagger-1.20.4"

repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

loom {
    runs.named("client") {
        vmArgs(
            "-XX:+IgnoreUnrecognizedVMOptions",
            "-Xmx2G",
            "-XX:+AllowEnhancedClassRedefinition",
            "-XX:HotswapAgent=fatjar",
            "-javaagent:\"C:\\Users\\filyt\\.gradle\\caches\\modules-2\\files-2.1\\org.spongepowered\\mixin\\0.8.7\\8ab114ac385e6dbdad5efafe28aba4df8120915f\\mixin-0.8.7.jar\"",
            "-Dfabric.debug.disableClassPathIsolation=true"
        )
    }
}

dependencies {
    minecraft(libs.minecraft.mc1204)
    mappings(loom.officialMojangMappings())

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.mc1204)

    compileOnly(libs.mixin)

    compileOnly(rootProject)
}

tasks.withType<JavaCompile> {
    source(rootProject.sourceSets.main.get().java)
    options.encoding = "UTF-8"
    options.release = 17
}

tasks.withType<ProcessResources> {
    from(rootProject.sourceSets.main.get().resources)
    inputs.property("version", project.version)
    inputs.property("minecraft_version", libs.versions.minecraft.mc1204.get())
    inputs.property("loader_version", libs.versions.fabric.loader.get())

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to libs.versions.minecraft.mc1204.get(),
            "loader_version" to libs.versions.fabric.loader.get()
        )
    }
}