plugins {
    `java-library`
    `maven-publish`
    signing
    id("com.diffplug.spotless") version "6.8.0"
}

group = "com.deepl.api"
version = "1.3.0"

val sharedManifest = the<JavaPluginConvention>().manifest {
    attributes (
        "Implementation-Title" to "Gradle",
        "Implementation-Version" to version
    )
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")

//    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.google.code.gson:gson:2.9.0")
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat("1.7")
        removeUnusedImports()
    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
    manifest = project.the<JavaPluginConvention>().manifest {
        from(sharedManifest)
    }
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
    manifest = project.the<JavaPluginConvention>().manifest {
        from(sharedManifest)
    }
}

publishing {
    repositories {
        maven {
            val mavenUploadUsername: String? by project
            val mavenUploadPassword: String? by project
            name = "MavenCentral"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = mavenUploadUsername
                password = mavenUploadPassword
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set("deepl-java")
                description.set("DeepL API Java Client Library")
                url.set("https://www.github.com/DeepLcom/deepl-java")
                properties.set(mapOf(
                    "java.version" to "1.8",
                    "project.build.sourceEncoding" to "UTF-8",
                    "project.reporting.outputEncoding" to "UTF-8"
                ))
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("deepl")
                        name.set("DeepL SE")
                        email.set("open-source@deepl.com")
                    }
                }
                organization {
                    name.set("DeepL SE")
                    url.set("https://www.deepl.com")
                }
                scm {
                    connection.set("scm:git:git://github.com/DeepLcom/deepl-java.git")
                    developerConnection.set("scm:git:ssh://github.com/DeepLcom/deepl-java.git")
                    url.set("https://www.github.com/DeepLcom/deepl-java")
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

