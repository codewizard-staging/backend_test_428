### Spring Security Application Documentation

This documentation will guide you through setting up and using a Spring Security application. Follow the steps below to configure the security, add necessary roles, and manage users.

#### 1. Host Information

Before proceeding with user registration and role management, you need to add host information.

- **Add Host Info**

  ```http
  POST {{host}}/app/hosts
  Content-Type: application/json

  {
    "ip": "34.238.241.129",
    "port": 8084,
    "scheme": "http",
    "serviceName": "generated_app"
  }
  ```

#### 2. User Registration

- **Signup**

  ```http
  POST {{host}}/app/signup
  Content-Type: application/json

  {
    "userName": "userx",
    "password": "password",
    "email": "userx@gmail.com",
    "country": "India"
  }
  ```

- **Verify User**

  ```http
  PUT {{host}}/app/users/verify?userId=15
  ```

- **Login**

  ```http
  POST {{host}}/app/login
  Content-Type: application/json

  {
    "userName": "sachin",
    "password": "password"
  }
  ```

  Upon successful login, you will receive a token in the response, which you will use to authenticate subsequent requests.

#### 3. Role Management

- **Add Role**

  ```http
  POST {{host}}/app/role/create
  Content-Type: application/json

  {
    "name": "GetVisits",
    "api": "/generated_app/Visits",
    "apiAccess": "GET"
  }
  ```

- **Get All Roles**

  ```http
  GET {{host}}/app/role/list
  ```

- **Assign Role to User**

  ```http
  PUT {{host}}/app/role/addUserRole/
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "userId": 16,
    "roleId": 15
  }
  ```

#### 4. API Endpoints

- **Get User By Id**

  ```http
  GET {{host}}/app/users/15
  ```

- **List All Hosts**

  ```http
  GET {{host}}/app/hosts
  ```

- **Get Host Info By Id**

  ```http
  GET {{host}}/app/hosts/17
  ```

- **Delete Host Info By Id**

  ```http
  DELETE {{host}}/app/hosts/15
  ```

### Workflow Overview

1. **Add Host Information**: Begin by adding the host information for your service.
2. **User Signup**: Users register by providing their details.
3. **Admin Verification**: Admin verifies the registered users.
4. **User Login**: Verified users can log in to receive their authentication tokens.
5. **Add Roles**: Create roles defining access to specific APIs.
6. **Assign Roles**: Assign created roles to users.
7. **API Interaction**: Users can interact with the APIs using their authentication tokens.

### Postman Collection

To make the integration and testing process smoother, we provide a Postman collection. This collection includes all the necessary endpoints and example requests to help you get started quickly. You can import this collection into Postman and use it to test your API endpoints.

**Postman Collection Link**: spring_security/src/main/resources/spring-security.json

### Security Deployment

For deploying the security-enabled application, ensure you configure the IP and port settings in your deployment environment to match the host information added in the initial step. This ensures seamless communication and security enforcement throughout your application.

# odata_v4
OData V4 implementation of test service
This is a base spring boot project containing sap jpa processor v4 support. This project is build by taking reference from this doc. https://github.com/SAP/olingo-jpa-processor-v4/blob/master/jpa-tutorial/QuickStart/QuickStart.md

# To build the entire project
docker-compose build

# To build a specific service/application in the project (in the below command "app" is a service)
docker-compose build app

##############
Step to run this application
docker-compose up -d

Step to stop the application
docker-compose stop app

Step to shutdown the application
docker-compose down app
################


To run Testcases:
mvn verify

To compile
mvn clean install -DnoTest=true
