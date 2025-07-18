plugins {
	id("dev.frozenmilk.android-library") version "10.1.1-0.1.3"
	id("dev.frozenmilk.publish") version "0.0.4"
	id("dev.frozenmilk.doc") version "0.0.4"
}

android {
	namespace = "com.ikknight.octofault"
}

ftc {
	kotlin
	sdk {
		RobotCore
		FtcCommon {
			configurationNames += "testImplementation"
		}
	}
}
val dokkaJar = tasks.register<Jar>("dokkaJar") {
	dependsOn(tasks.named("dokkaGenerate"))
	from(dokka.basePublicationsDirectory.dir("javadoc"))
	archiveClassifier.set("javadoc")
}
dependencies {
	compileOnly("com.acmerobotics.slothboard:dashboard:0.2.3+0.4.16")
	compileOnly("dev.frozenmilk.sinister:Sloth:0.2.3")
}

repositories {
	maven {
		name = "dairyReleases"
		url = uri("https://repo.dairy.foundation/releases")
	}
}



publishing {
	publications {
		register<MavenPublication>("release") {
			groupId = "com.ikknight"
			artifactId = "octofault"

			// Add documentation artifacts
			artifact(dairyDoc.dokkaHtmlJar)
			artifact(dairyDoc.dokkaJavadocJar)
			// Add sources JAR for better IDE support


			afterEvaluate {
				from(components["release"])
			}

			// Add POM metadata (recommended)
			pom {
				name.set("OctoFault")
				description.set("A simple sanitization library")
				url.set("https://github.com/IKKNIGHT/Octofault")
				licenses {
					license {
						name.set("The Apache License, Version 2.0")
						url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
					}
				}
				developers {
					developer {
						id.set("IKKNIGHT")
						name.set("isaaq")
						email.set("IK_Knight@outlook.com")
					}
				}
			}
		}
	}
}