# karatepdi

This is a Spring Boot Rest application for automating ETL Testing using Behaviour Driven Development framework Karate. You can use it to execute PDI job/transformation or running Karate feature file to produce cucumber reports.  

# How to run

This application package as jar file. You run it using the ```java -jar``` command. 

* Clone this repository 
* Make sure you are using JDK 1.8 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the service by one of these two methods:
```
        java -jar -Dpdi.plugins.folder=<location of plugins> -Dpdi.reports.folder=<folder to generate test reports> target/karatepdi-1.0.0.jar
or
        mvn spring-boot:run -Dpdi.plugins.folder=<location of plugins> -Dpdi.reports.folder=<folder to generate test reports> 
```
* Check the stdout to make sure no exceptions are thrown

Here are some endpoints you can call:

### Get information about system health, configurations, etc.

```
http://127.0.0.1:9999/login
http://127.0.0.1:9999/report/overview-tags.html
http://127.0.0.1:9999/pdi/execute?filename=./etl/jb-test-query-db.kjb
http://127.0.0.1:9999/karate/report?name=./etl/check-run-status.feature
http://127.0.0.1:9999/karate/reporthtml?name=./etl/check-run-status.feature
http://127.0.0.1:9999/karate/reportxml?name=./etl/check-run-status.feature
```
Please check your feature file or PDI script path

# LICENSE

This project falls under the Apache License 2.0, see the included LICENSE.txt file for more details
