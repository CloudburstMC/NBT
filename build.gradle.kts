@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/IDEA-262280

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    alias(libs.plugins.checkerframework)
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

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "maven-deploy"
            url = uri(System.getenv("MAVEN_DEPLOY_URL") ?: "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_DEPLOY_USERNAME") ?: "username"
                password = System.getenv("MAVEN_DEPLOY_PASSWORD") ?: "password"
            }
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

signing {
    if (System.getenv("PGP_SECRET") != null && System.getenv("PGP_PASSPHRASE") != null) {
        useInMemoryPgpKeys(System.getenv("PGP_SECRET"), System.getenv("PGP_PASSPHRASE"))
        sign(publishing.publications["maven"])
    }
}