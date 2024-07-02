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


#### H2 Database connection:
Open your web browser and navigate to http://localhost:8080/h2-console. Use the following settings to connect:

JDBC URL: jdbc:h2:mem:testdb

### Deactivate wildcard imports:
Open settings and then select Editor | Code Style | Java.

Enabled "Use single class import" option

In the "Class count to use import with '*'" and "Names count to use static import with '*'" fields write 999.

#### Manual to run project locally:
Step 1: Set up the development environment
1. Install JDK 8 or higher
2. Install IntelliJ IDEA
3. Install Gradle
4. Install PostgreSQL
5. Install Postman
6. Install Git

Step 2: Run application

1. Ensure Lombok is enabled in IntelliJ IDEA
2. Run InsuranceApplication.java
3. Go to http://localhost:8080

poehali.lt
