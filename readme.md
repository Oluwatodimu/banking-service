## Solution To the Backend Code Test

This repo contains the solution to question for the backend code test, where I created simple banking application with spring boot
and Java.

My submission is in 3 parts:
1. the code repo and the `readme` file for setting up the project
2. the [discussion.md](https:test.com) file where I discuss what I did in the project
3. the postman collection that can be accessed [here](postman.com)

This readme file will show you how to do setup this project, start the application and run the required tests.

### Setting up the project

- Clone the repo
- Ensure Java 17 is installed on your machine.
- MySql server for the database should be installed
- Ensure you also have maven installed
- navigate to the project directory

### Add dependencies to the project
Before your run the application, navigate the `application.yml` file and update the
`username` and `password` fields for the db configurations as shown below.

```yaml
  datasource:
    url: jdbc:mysql://localhost:3306/banking_service_backend?createDatabaseIfNotExist=true
    username: <USERNAME_HERE>
    password: <PASSWORD_HERE>
```
After doing this you can run the application.

### Running the application
- ensure you are in the project directory of the application
- to build the application, run the command `mvn clean package`.
- to run the unit tests in the project, run the command `mvn test`.
- to start the spring boot application, run the command `mvn spring-boot:run`


### Testing the application APIs
After you have successfully started the application, you can use this [link](postman.com) to
take you to the postman collection for testing the APIs.