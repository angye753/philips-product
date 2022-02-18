WAES/Philips Assignment
Context

This is a REST API responsible for storing, listing, updating and deleting Products.

Technologies

Java 11 (Language)
Spring Boot (Framework)
Swagger
H2 (Database)
JUnit/ Mockito (Unit Tests)
Jacoco (Code Coverage)
Resilience4j (Circuit-breaker)

To run the project, you need to execute PhillipsProductApplication class

Swagger UI

You can check the swagger on the endpoint below.

Swagger
The contract of the api, is on the file swagger.yml

H2 Console

Data base in memory to make more flexible the current implementation but thar layer could be replaced

mvn test
Project Info
I used Sprint boot framework to create the Rest API, following patterns and clean code techniques, I created an application
followed by layers to separated concerns 

The Controller layer - Responsible for receiving the requests and handing the data to the business layer to process it.
The Services layer - Application logic is managed in this layer, which will process the data and respond to the clients. It is also responsible for calling the Persistence layer.
The Persistence layer - All the data stored is handled by this layer, through spring data JPA
The Integration layer - Responsible to propagate local data to the external API
Exceptions - To handler custom exception in the application

I have added a Circuit Breaker to be able to add resilience to the app. If the external API is unavailable a custom message is going to be shown

Decisions Made

H2 Database - Java memory database. If necessary it would be simple to migrate to another database of choice.
Jacoco - Verify covered percentage by unit tests.
Swagger - Documentation

