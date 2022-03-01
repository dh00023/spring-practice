import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springCloudVersion = "2021.0.0"
val kotestVersion = "4.4.3"

// pugins
plugins {
    val kotlinVersion: String = "1.6.10"

    // apply false : 서브 프로젝트에만 적용하기 위해 변경
    id("org.springframework.boot") version "2.6.4" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0" apply false
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false
    kotlin("plugin.allopen") version kotlinVersion
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "kotlin-jpa")
    apply(plugin = "kotlin-spring")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "kotlin-allopen")

    group = "dh00023.example"
    version = "0.0.1"

    dependencies {
        implementation(kotlin("reflect"))
        implementation(kotlin("stdlib"))
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// mvc-tutorial 프로젝트 관련 설정
project(":mvc-tutorial") {
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
        implementation("org.springframework.cloud:spring-cloud-starter-config")
        implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
//        runtimeOnly("org.postgresql:postgresql")

        runtimeOnly("com.h2database:h2")
        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(module = "junit")
            exclude(module = "mockito-core")
        }
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testImplementation("com.ninja-squad:springmockk:3.0.1")
    }

    // kotlin에서는 final클래스가 기본이며, 아래 annotation은 allOpen으로 open을 추가해줘야한다.
    // open 키워드가 없으면 Proxy 기반으로 Lazy 로딩을 할 수 없음.
    allOpen {
        annotation("javax.persistence.Entity")
        annotation("javax.persistence.Embeddable")
        annotation("javax.persistence.MappedSuperclass")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        }
    }
}

project(":service-config") {
    dependencies {
        implementation("org.springframework.cloud:spring-cloud-config-server")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        }
    }
}
