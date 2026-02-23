import org.jreleaser.model.Active

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.checkerframework)
    alias(libs.plugins.jreleaser)
}

group = "org.cloudburstmc"
description = "A library for reading and writing NBT data."

repositories {
    mavenCentral()
}

dependencies {
    checkerFramework(libs.checker)

    compileOnly(libs.checker.qual)

    testCompileOnly(libs.checker.qual)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.jmh.core)
    testAnnotationProcessor(libs.jmh.generator.annprocess)
}

checkerFramework {
    version = libs.versions.checkerframework
}

java {
    withJavadocJar()
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

jreleaser {
    signing {
        pgp {
            active = Active.ALWAYS
            armored = true
        }
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}

publishing {
    repositories {
        maven {
            uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("NBT")
                packaging = "jar"
                description.set("A library for reading and writing NBT data.")
                url.set("https://github.com/CloudburstMC/NBT")

                scm {
                    connection.set("scm:git:git://github.com/CloudburstMC/NBT.git")
                    developerConnection.set("scm:git:ssh://github.com/CloudburstMC/NBT.git")
                    url.set("https://github.com/CloudburstMC/NBT")
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        name.set("CloudburstMC Team")
                        organization.set("CloudburstMC")
                        organizationUrl.set("https://github.com/CloudburstMC")
                    }
                }
            }
        }
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name();
    }
    test {
        useJUnitPlatform()
    }
    jar {
        manifest {
            attributes("Automatic-Module-Name" to "org.cloudburstmc.nbt")
        }
    }
}
