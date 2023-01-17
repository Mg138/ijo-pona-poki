pluginManagement {
    this.repositories {
        this.maven {
            this.name = "Quilt"
            this.url = uri("https://maven.quiltmc.org/repository/release")
        }
        // Currently needed for Intermediary and other temporary dependencies
        this.maven {
            this.name = "Fabric"
            this.url = uri("https://maven.fabricmc.net/")
        }

        this.gradlePluginPortal()
        this.mavenCentral()
    }
}

rootProject.name = "ijo-pona-poki"