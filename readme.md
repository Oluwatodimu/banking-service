## Solution To the Backend Code Test

This repo contains the solution to problem for the backend code test, where I created simple banking application with spring boot
and Java.

My submission is in 3 parts:
1. the code repo and the `readme` file for setting up the project
2. the [discussion.md](https://github.com/Oluwatodimu/banking-service/blob/main/discussion.md) file where I discuss what I did in the project
3. the postman collection that can be accessed [here](https://www.postman.com/lively-firefly-891824/workspace/my-public-workspace/request/18629385-07dbd1e2-92e5-4493-9b0f-e073a06ce8a7)

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
After you have successfully started the application, you can use this [link](https://www.postman.com/lively-firefly-891824/workspace/my-public-workspace/request/18629385-07dbd1e2-92e5-4493-9b0f-e073a06ce8a7) to
take you to the postman collection for testing the APIs.

**Note:**: The APIs in postman are protected against CSRF attacks so you will need a CSRF token to
access the POST, PUT endpoints. Not to worry I have put the fields as headers in the requests.
All you need to do is to go the root folder in the collection and look for the 
`actuator` request.

Once you have located it, you can run the request. There is some underlying script that gets the CSRF token
from that GET request and adds it to the other request headers.

In case the request doesn't succeed, you can run the `actuator` request again (I'd advice to run it before making 
a request).