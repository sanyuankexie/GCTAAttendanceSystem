/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    java
    `maven-publish`
    id("org.springframework.boot") version "3.3.2"
    kotlin("jvm")
}


repositories {
    mavenLocal()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.opencsv/opencsv
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("com.opencsv:opencsv:5.9")
    implementation("io.minio:minio:8.5.17")
    implementation("com.alibaba:druid-spring-boot-starter:1.1.17")
    implementation("com.baomidou:mybatis-plus-boot-starter:3.4.2")
    implementation("org.springframework.boot:spring-boot-starter:2.3.4.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-aop:2.3.4.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-web:2.3.4.RELEASE")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.hibernate.validator:hibernate-validator:6.0.13.Final")
    // 如果出现 Bug , see https://mp.weixin.qq.com/s?__biz=MzAwMjk5NTY3Mw==&mid=2247483950&idx=1&sn=47c6c1fed54b134f46f6dedafd34db0c&chksm=9ac0a698adb72f8e769bcfbff5a4fb0450f181bb754a2ad615dc17002f14d7ec039c0e24a1d7&token=395785991&lang=zh_CN#rd
    implementation("com.alibaba:easyexcel:3.3.4")
    implementation("mysql:mysql-connector-java:8.0.18")
    implementation("com.alibaba:fastjson:1.2.47")
    implementation("org.springframework.boot:spring-boot-starter-mail:2.3.4.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.3.4.RELEASE")
    compileOnly("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")

    annotationProcessor("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.3.4.RELEASE")

    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.3.4.RELEASE")
    implementation(kotlin("stdlib-jdk8"))
}

group = "org.sanyuankexie"
version = "1.0.1"
description = "GCTAAttendanceSystem"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    this.archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}
kotlin {
    jvmToolchain(11)
}