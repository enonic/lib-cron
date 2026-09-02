import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    java
    jacoco
    `maven-publish`
    id("com.enonic.xp.base")
    alias(libs.plugins.enonic.defaults)
    alias(libs.plugins.node.gradle)
}

xp {
    scriptEngines = listOf("Nashorn", "GraalJS")
}

repositories {
    mavenLocal()
    mavenCentral()
    xp.enonicRepo()
}

val includeLib = configurations.create("includeLib") { isTransitive = false }

dependencies {
    compileOnly(xplibs.api.script)

    implementation(libs.cron.utils)
    includeLib(libs.cron.utils)

    testImplementation(platform(libs.junit.bom))
    testImplementation(platform(libs.mockito.bom))
    testImplementation(xplibs.testing) {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.slf4j.log4j12)
    testImplementation(libs.felix.framework)
    testImplementation(libs.tinybundles)
    testRuntimeOnly(libs.junit.platform.launcher)
}

node {
    download = true
    version = "24.19.0"
    pnpmVersion = "11.23.0"
}

val environmentShort = if (providers.gradleProperty("env").orNull == "dev") "dev" else "prod"
val nodeEnvironment = if (environmentShort == "dev") "development" else "production"

fun pnpmCheck(taskName: String, script: String) =
    tasks.register<PnpmTask>(taskName) {
        dependsOn(tasks.named("pnpmInstall"))
        args = listOf("run", script)
        environment = mapOf("FORCE_COLOR" to "true")
    }

pnpmCheck("checkTypes", "check:types")
pnpmCheck("checkLint", "check:lint")

val esbuildOutput = layout.buildDirectory.dir("esbuild")

val pnpmBuild = tasks.register<PnpmTask>("pnpmBuild") {
    dependsOn(tasks.named("pnpmInstall"))
    args = listOf("run", "build:$environmentShort")
    environment = mapOf("FORCE_COLOR" to "true", "NODE_ENV" to nodeEnvironment)
    inputs.dir("src/main/resources")
    inputs.file("esbuild.config.js")
    inputs.file("package.json")
    inputs.file("pnpm-lock.yaml")
    inputs.file("tsconfig.json")
    outputs.dir(esbuildOutput)
    // esbuild never prunes outdir, so a removed entry point would keep shipping until the next clean
    val staleOutput = esbuildOutput.get().asFile
    doFirst { staleOutput.deleteRecursively() }
}

// cron-utils ships inside the jar, unpacked alongside the library's own classes
val copyLibFiles = tasks.register<Copy>("copyLibFiles") {
    from(zipTree(includeLib.elements.map { it.single().asFile })) {
        include("**/*.**")
        exclude("META-INF/**")
    }
    into(layout.buildDirectory.dir("includeLib"))
}

tasks.named<ProcessResources>("processResources") {
    exclude("**/*.ts")
    includeEmptyDirs = false
    from(pnpmBuild)
}

tasks.named<Jar>("jar") {
    from(copyLibFiles)
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.named("check") {
    dependsOn("checkTypes", "checkLint", tasks.named("jacocoTestReport"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
