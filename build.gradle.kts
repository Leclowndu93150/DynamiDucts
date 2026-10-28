plugins {
    id("dev.prism")
}

group = "com.leclowndu93150"
version = "1.2.0"

prism {
    curseMaven()
    maven("BlameJared", "https://maven.blamejared.com/")

    metadata {
        modId = "dynamiducts"
        name = "DynamiDucts"
        license = "MIT"
        author("Leclowndu93150")
        author("CoFH")
    }

    version("1.21.1") {
        parchmentMinecraftVersion = "1.21.3"
        parchmentMappingsVersion = "2024.12.07"

        neoforge {
            loaderVersion = "21.1.221"

            dependencies {
                implementation("curse.maven:mekanism-268560:7621971")
                implementation("curse.maven:jade-324717:7545219")
                implementation("mezz.jei:jei-1.21.1-neoforge:19.21.0.247")
                compileOnly("curse.maven:immersive-engineering-231951:6733669")
            }

            rawProject {
                tasks.register("datagenThenClient") {
                    group = "modular networks"
                    description = "Runs datagen then launches client"
                    dependsOn("runData")
                    finalizedBy("runClient")
                }
            }
        }
    }

    version("26.1.2") {
        neoforge {
            loaderVersion = "26.1.2.124"

            dependencies {
                implementation("curse.maven:jade-324717:9156414")
                implementation("mezz.jei:jei-26.1.2-neoforge:29.47.0.110")
            }

            rawProject {
                tasks.register("datagenThenClient") {
                    group = "modular networks"
                    description = "Runs datagen then launches client"
                    dependsOn("runClientData")
                    finalizedBy("runClient")
                }
            }
        }
    }
}
