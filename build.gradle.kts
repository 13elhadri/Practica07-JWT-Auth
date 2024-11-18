plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	id ("jacoco")
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.h2database:h2") // base de datos a usar, puede ser otra

	implementation("org.springframework.boot:spring-boot-starter-cache")

	implementation("org.springframework.boot:spring-boot-starter-validation")

	//developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Para usar con jackson el controlador las fechas: LocalDate, LocalDateTime, etc
	// Lo podemos usar en el test o en el controlador, si hiciese falta, por eso está aquí
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

	implementation("org.springframework.boot:spring-boot-starter-websocket")

	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")


	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
	//H2
	implementation("com.h2database:h2")

	// Spring Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	implementation("com.auth0:java-jwt:4.4.0")
}


tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.outputLocation.set(file("build/jacoco"))
	}
}

tasks.check {
	dependsOn(tasks.jacocoTestReport)
}
