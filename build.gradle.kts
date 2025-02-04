// This is to suppress false warnings generated by a bug in IntelliJ
@file:Suppress("DSL_SCOPE_VIOLATION", "MISSING_DEPENDENCY_CLASS", "FUNCTION_CALL_EXPECTED", "PropertyName", "implicitThis")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`

    alias(libs.plugins.kotlin)
    alias(libs.plugins.quilt.loom)
}

val archives_base_name: String by project
base.archivesName.set(archives_base_name)

val javaVersion = 17

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    repositories {
        maven {
            name = "Modmaven"
            url = uri("https://modmaven.dev/")

            // For Gradle 5.1 and above, limit it to just AE2
            content {
                includeGroup("appeng")
            }
        }
    }
    maven {
        url = uri("https://maven.shedaniel.me/")

        content {
            includeGroup("me.shedaniel")
            includeGroup("me.shedaniel.cloth")
            includeGroup("dev.architectury")
        }
    }
    maven {
        url = uri("https://maven.bai.lol")
        content {
            includeGroup("mcp.mobius.waila")
        }
    }
    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }

    maven { url = uri("https://maven.bai.lol") }

    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
}

// All the dependencies are declared at gradle/libs.version.toml and referenced with "libs.<id>"
// See https://docs.gradle.org/current/userguide/platforms.html for information on how version catalogs work.
dependencies {
    minecraft(libs.minecraft)
    mappings(
        variantOf(libs.quilt.mappings) {
            classifier("intermediary-v2")
        }
    )

    modImplementation(libs.quilt.loader)


    // QSL is not a complete API; You will need Quilted Fabric API to fill in the gaps.
    // Quilted Fabric API will automatically pull in the correct QSL version.
    modImplementation(libs.qfapi)
    // modImplementation(libs.bundles.qfapi) // If you wish to use the deprecated Fabric API modules

    modImplementation(libs.qkl)


    modImplementation(libs.ae2) {
        exclude(module = "RoughlyEnoughItems-fabric")
        exclude(group = "net.fabricmc.fabric-api")
    }

    /*
    modImplementation(libs.cloth.config) {
        exclude(group = "net.fabricmc.fabric-api")
    }
     */

    modRuntimeOnly(libs.wthit) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modApi(libs.emi)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
            // languageVersion: A.B of the kotlin plugin version A.B.C
            languageVersion = libs.plugins.kotlin.get().version.requiredVersion.substringBeforeLast('.')
        }
    }

    withType<JavaCompile>.configureEach {
        options.encoding = "UTF-8"
        options.isDeprecation = true
        options.release.set(javaVersion)
    }

    processResources {
        filteringCharset = "UTF-8"
        inputs.property("version", project.version)

        filesMatching("quilt.mod.json") {
            expand(
                mapOf(
                    "version" to project.version
                )
            )
        }
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    // Run `./gradlew wrapper --gradle-version <newVersion>` or `gradle wrapper --gradle-version <newVersion>` to update gradle scripts
    // BIN distribution should be sufficient for the majority of mods
    wrapper {
        distributionType = Wrapper.DistributionType.BIN
    }

    jar {
        from("LICENSE") {
            rename { "LICENSE_${archives_base_name}" }
        }
    }
}

val targetJavaVersion = JavaVersion.toVersion(javaVersion)
if (JavaVersion.current() < targetJavaVersion) {
    kotlin.jvmToolchain(javaVersion)

    java.toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    // If this mod is going to be a library, then it should also generate Javadocs in order to aid with development.
    // Uncomment this line to generate them.
    // withJavadocJar()

    // Still required by IDEs such as Eclipse and VSC
    sourceCompatibility = targetJavaVersion
    targetCompatibility = targetJavaVersion
}

// Configure the maven publication
publishing {
    publications {
        register<MavenPublication>("Maven") {
            from(components.getByName("java"))
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}