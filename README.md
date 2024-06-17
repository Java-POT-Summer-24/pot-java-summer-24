# pot-java-summer-24

List of Technologies and Principles for Developing an Insurance Application

ALL TOOLS MUST BE THE LATEST STABLE VERSION!

### Languages and Platforms:
1.	Java 21
 
### Frameworks and Libraries:
1.	Spring Boot
2.	Spring Web (Spring MVC)
3.	Spring Data JPA
4.	Spring Security
5.	Hibernate ORM
6.	Lombok

### Tools and Environment:
1.	Gradle
2.	PostgreSQL/H2
3.	Liquibase
4.	IntelliJ IDEA
5.	Postman
6.	Junit
7.	Mockito
8.	Google java code format

### Other:
1.	Monolith Architecture
2.	SOLID, DRY, KISS
3.	RESTful API

#### Code Style Configuration:
The code style settings should be automatically detected if you open the most recent version of the project.
To verify that the appropriate code style is used you can do the following:

Go to File -> Settings ->

Navigate to Editor -> Code Style > Java. ->

Ensure GoogleStyle is selected in the Scheme dropdown.

#### Manual to run project locally:
Step 1: Set up the development environment
1. Install JDK 8 or higher
2. Install IntelliJ IDEA
3. Install Gradle
4. Install PostgreSQL
5. Install Postman
6. Install Git

Step 2: Configure application
1. Modify application.yml in the src/main/resources directory.
```
# For PostgreSQL
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
  server:
    port: 8080
```
2. Modify application.yml for liquibase configuration
```
  liquibase:
      change-log: classpath:db/changelog/db.changelog-master.xml
```
3. Add to dependencies in build.gradle

```
        implementation 'org.springframework.boot:spring-boot-starter'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-security'
	implementation 'org.postgresql:postgresql:42.5.0'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
	implementation 'org.liquibase:liquibase-core'
	implementation 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
```
4. Ensure Lombok is enabled in IntelliJ IDEA
5. Run InsuranceApplication.java
6. Go to http://localhost:8080
