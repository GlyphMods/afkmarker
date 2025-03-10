import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.+"
    id("idea")

    kotlin("jvm") version "2.0.0"
}

tasks.named<Wrapper>("wrapper").configure {
    // Define wrapper values here so as to not have to always do so when updating gradlew.properties.
    // Switching this to Wrapper.DistributionType.ALL will download the full gradle sources that comes with
    // documentation attached on cursor hover of gradle classes and methods. However, this comes with increased
    // file size for Gradle. If you do switch this to ALL, run the Gradle wrapper task twice afterwards.
    // (Verify by checking gradle/wrapper/gradle-wrapper.properties to see if distributionUrl now points to `-all`)
    distributionType = Wrapper.DistributionType.BIN
}

val modId = property("mod_id") as String

version = property("mod_version") as String
group = property("mod_group_id") as String

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

base {
    archivesName.set(modId)
}

// Mojang ships Java 21 to end users starting in 1.20.5, so mods should target Java 21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

neoForge {
    // Specify the version of NeoForge to use.
    version = property("neo_version") as String

    parchment {
        mappingsVersion = property("parchment_mappings_version") as String
        minecraftVersion = property("parchment_minecraft_version") as String
    }

    // This line is optional. Access Transformers are automatically detected
    // accessTransformers = project.files("src/main/resources/META-INF/accesstransformer.cfg")

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        create("client") {
            client()

            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("data") {
            clientData()

            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath,
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        // define mod <-> source bindings
        // these are used to tell the game which sources are for which mod
        // multi mod projects should define one per mod
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Include resources generated by data generators.
sourceSets.main.get().resources { srcDir("src/generated/resources") }

// Sets up a dependency configuration called "localRuntime".
// This configuration should be used instead of "runtimeOnly" to declare
// a dependency that will be present for runtime testing but that is
// "optional", meaning it will not be pulled by dependents of this mod.
configurations {
    create("localRuntime")
    runtimeClasspath.extendsFrom(named("localRuntime"))
}

dependencies {
    implementation("thedarkcolour:kotlinforforge-neoforge:${property("loader_version")}")
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = listOf(
        "minecraft_version", "minecraft_version_range",
        "neo_version", "neo_version_range",
        "loader_version_range",
        "mod_id", "mod_name", "mod_license", "mod_version",
        "mod_authors", "mod_description",
    ).associateWith { (project.properties[it] as String) }

    inputs.properties(replaceProperties)
    expand(replaceProperties)
        .from("src/main/templates")
        .into("build/generated/sources/modMetadata")
}

sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}