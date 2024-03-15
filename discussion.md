## Discussion of what was done in the submission

First I want to certify that I alone worked on the requirements and instructions
to build the solution to the backend test I was given.

I also certify that the main requirements, and the bonus requirements were 
completed in this project.


### Operations supported by the system
1. **Creating a new account for a customer**: in order for customers to create accounts,
we first need to register customers. I wrote the logic for creating users and allowing
them to be able to sign in. Once this is achieved they will be able to create a new account.
Customers can also have more than one account.


2. **Retrieving account details by account number**: This is an authenticated action where only signed 
in users have access to the API. It will return the account details of the account number passed in.


3. **Updating account details**: for this, since we cannot just change the amount in the wallet, I
decided that the only fields that could be changed were account type and account status. Maybe the user
wants to change from a current to a savings account, or the account has been involved in some suspicious 
transactions and has to be suspended. The update account action can only be done by an admin user.


4. **Closing account**: Since it is a bank application, however simple, we don't want user to be able to close
their bank accounts by themselves. Instead this action will be done by an admin user.


5. **Perform deposits and withdrawals on an account**: I made an assumption that the deposits and the withdrawals
involved one party being an external member of the bank application. This is why I made the deposit feature unauthenticated
because the account should be able to receive money from anywhere, as long as the account is active. The withdrawal action was a bit more tricky since I made it an
authenticated resource. Since we know the sender is from our platform, I made it authenticated. I also added an extra layer of
security where the logged in user could only perform the withdrawal transactions on his own accounts. I did this by ensuring the
current logged in user equaled the user associated with the account in the first place; I compared their ids. I did this for
the transfer feature too.


6. **Transfer funds between accounts**: Since the identity of the sender and the receiver are know, I made this an authenticated 
resource where money can be moved between bank accounts in the system application. In addition to what was said regarding the
extra layer of security when transferring, I also added some more checks to ensure that the account was active, and that you could not
transfer money from the same account you were sending from. I also ensured that the accounts concerned with the transaction were the 
same currency (two naira accounts) in order to prevent sending from a naira to a dollar account. 


All these supported action implementations can be seen in the github repo.

### Requirements

In this section, I will be discussing how I achieved all the requirements:

1. I used Java and Spring boot to build the bank application


2. I designed the appropriate data models to ensure that the application worked as expected. I also put the results of theis
requirement in and ERD diagram, for a better view of the models and their relationships. 
In addition to the 3 required models, I created a currency model to handle the denominations of the accounts.


3. The RESTful APIs for the application were built using spring boot. I adhered to the RESTful guidelines for building the 
APIs, including the use of a layered system where each layer interacts with the adjacent layer. I used the repository, service
controller layer, and ensured that no business logic was don in the controller layer. This made it easier to write tests, and 
also focus the testing to the service layer. I also used Jwt stateless tokens for authentication, so no need to manually store
any tokens between requests.


4. Basic input validation was done in the controller layer, and was done for all request data in the application(request bodies,
path variables and request params). Proper exception handling was also done to ensure that exceptions were properly handled when
some of these validations failed.


5. I ensured transactional integrity by annotating all the write db operations with `@Transactional` in order to rollback anything
that had been done to the db before an error occurred. All the other integrity concerns were handled by underlying mechanisms in the
spring boot application.


6. Unit tests were written for all the operations supported by the system, and they can be ran using `mvn test`. The test implementations 
can be seen in the code repo for further reference.


7. For Logging, SLF4J (Simple Logging Facade for Java) is an abstraction layer for various logging frameworks in the Java ecosystem. I used this in
my application, in order to use the logging capabilities without without coupling the code directly to a specific logging framework. I logged all the
controller layer actions in the application.


### Bonus Points
1. I implemented authorization and authentication in the application using spring security. I used a combination of spring security and
Jwt to authenticate users using tokens. I was also able to implement Role Based Access Control (RBAC) in the 
application using spring security, and assigning user roles to customers upon registration. For testing purposes, I created and admin user. This user
is automatically when the application is ran the first time.


2. I used a MySql database for persistent storage. I used this because it was easy to setup and was right for this application.


3. For account and transaction history tracking, I created different endpoints for viewing the amount in an account at any given time. For
the transactions. I sorted them according to the most recent transactions and enabled filtering by transaction type (credit, debit).


4. I implemented rate limiting, and the approach is cap the number of requests within a particular time. The parameters for this
can be tweaked in the `application.yml` file. I also implemented protection against CSRF attacks. Spring boot handles the SQL injection attacks.


5. Postman documentation can be viewed [here](https://www.postman.com/lively-firefly-891824/workspace/my-public-workspace/request/18629385-07dbd1e2-92e5-4493-9b0f-e073a06ce8a7)


### Additional Things I added
1. Proper Exception handling to prevent sensitive error log data from being leaked.

2. Keeping a uniform response fpr all the responses form the application, whether error or positive response.

3. Using some clean code principles.
