plugins {
    `java-library`
}

applyPlatformAndCoreConfiguration()
applyShadowConfiguration()

repositories {
    mavenCentral()
}

dependencies {
    "api"(project(":worldedit-core"))
    "api"(project(":worldedit-libs:powernukkit"))
    "implementation"("org.powernukkit:powernukkit:1.4.0.0-PN-SNAPSHOT")
}
