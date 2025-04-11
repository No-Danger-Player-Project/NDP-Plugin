plugins {
    id("java")
}

group = "top.alazeprt.ndps"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.3")
}