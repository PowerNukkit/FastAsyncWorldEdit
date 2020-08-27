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
    "compile"(":worldedit-adapters:")
    "implementation"("org.powernukkit:powernukkit:1.4.0.0-PN-SNAPSHOT")
    "implementation"("org.apache.logging.log4j:log4j-slf4j-impl:2.8.1")
}
