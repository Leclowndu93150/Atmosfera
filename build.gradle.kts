plugins {
    id("dev.prism")
}

group = "com.leclowndu93150"
version = "1.0.0"

prism {
    metadata {
        modId = "atmosfera"
        name = "Atmosfera"
        description = "An ambient sound system with customizable, atmospheric sound resources."
        license = "MIT"
        author("Leclowndu93150")
    }

    curseMaven()

    version("1.20.1") {
        common {
            modImplementation("curse.maven:yacl-667299:6336646")
        }
        forge {
            loaderVersion = "47.4.18"
            loaderVersionRange = "[47,)"
            dependencies {
                modImplementation("curse.maven:yacl-667299:6336646")
            }
        }
    }

    version("1.21.1") {
        common {
            modImplementation("curse.maven:yacl-667299:7437845")
        }
        neoforge {
            loaderVersion = "21.1.222"
            loaderVersionRange = "[4,)"
            dependencies {
                modImplementation("curse.maven:yacl-667299:7437845")
            }
        }
    }

    version("26.1") {
        minecraftVersions("26.1", "26.1.1")
        common {
            modImplementation("curse.maven:yacl-667299:7851608")
        }
        neoforge {
            loaderVersion = "26.1.1.0-beta"
            loaderVersionRange = "[4,)"
            dependencies {
                modImplementation("curse.maven:yacl-667299:7851608")
            }
        }
    }

    publishing {
        type = STABLE
        curseforge {
            accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
            projectId = "1499605"
        }
//        modrinth {
//            accessToken = providers.environmentVariable("MODRINTH_TOKEN")
//            projectId = ""
//        }
    }
}
