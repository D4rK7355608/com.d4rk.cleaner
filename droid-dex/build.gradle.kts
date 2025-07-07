import com.vanniktech.maven.publish.AndroidMultiVariantLibrary
import java.net.URI

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)

	alias(libs.plugins.maven.publish)
}


publishing {
	repositories {
		maven {
			name = "Blinkit"
			url = URI("https://maven.pkg.github.com/grofers/droid-dex")
			credentials {
				username = "Blinkit"
				password = System.getenv("READ_ARTIFACTS_TOKEN")
			}
		}
	}
}


mavenPublishing {
	configure(AndroidMultiVariantLibrary(sourcesJar = true, publishJavadocJar = true))
}


kotlin {
	explicitApi()
}


android {
	namespace = "com.blinkit.droiddex"

	compileSdk = libs.versions.sdk.compile.get().toInt()

	defaultConfig {
		minSdk = libs.versions.sdk.min.get().toInt()

		consumerProguardFiles("consumer-rules.pro")
	}

	buildFeatures { buildConfig = true }

	buildTypes { release { isMinifyEnabled = false } }

	compileOptions {
		sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
		targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
	}
	kotlinOptions { jvmTarget = libs.versions.java.get() }
}


dependencies {
	implementation(libs.timber)

	implementation(libs.bundles.core)

	implementation(libs.coroutines.android)

	implementation(libs.bundles.lifecycle)

	implementation(libs.bundles.performance)
}
