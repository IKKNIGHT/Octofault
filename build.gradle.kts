plugins {
	id("dev.frozenmilk.android-library") version "10.1.1-0.1.3"
	id("dev.frozenmilk.publish") version "0.0.4"
	id("dev.frozenmilk.doc") version "0.0.4"
}

// TODO: modify
android.namespace = "com.ikknight.octofault"

// Most FTC libraries will want the following
ftc {

	sdk {
		RobotCore
		FtcCommon {
			configurationNames += "testImplementation"
		}

	}
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
			// TODO: modify
			groupId = "com.ikknight"
			// TODO: modify
			artifactId = "octofault"

			artifact(dairyDoc.dokkaHtmlJar)
			artifact(dairyDoc.dokkaJavadocJar)

			afterEvaluate {
				from(components["release"])
			}
		}
	}
}
