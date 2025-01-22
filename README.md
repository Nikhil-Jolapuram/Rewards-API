### Overview

The Rewards API is a Spring Boot application designed to calculate reward points for customers based on their transactions. The reward points are calculated as follows:

2 points for every dollar spent over $100 in a single transaction.

1 point for every dollar spent between $50 and $100 in a single transaction.

Transactions below $50 do not earn any points.

The application provides a RESTful API to calculate rewards for a given customer over a specified time frame.


### Features

## Reward Points Calculation:

Calculates rewards for a specific customer over a customizable time frame.

## Dynamic and Scalable API:

Allows users to provide a time frame dynamically for reward calculations.

## Detailed Responses:

API responses include customer information and transaction details.

### Testing:

JUnit test cases cover multiple scenarios.


### API Endpoints

Calculate Rewards for a Customer

### Endpoint: /api/rewards

### Method: POST

### Request Body:

{
  "customerId": 123,
  "startDate": "2023-10-01",
  "endDate": "2023-12-31"
}

### Response:

{
  "customer": {
    "id": 123,
    "name": "John Doe",
    "email": "john.doe@example.com"
  },
  "monthlyRewards": {
    "October": 150,
    "November": 200,
    "December": 100
  },
  "totalRewards": 450
}

### Setup Instructions

Clone the repository from GitHub:

git clone <repository_url>

### Build the project:

cd Rewards-API-backend

./gradlew build

### Run the application:

./gradlew bootRun

### Test endpoints using a tool like Postman.

### Testing

## Run unit tests using:

./gradlew test

. There is also a UI developed which will test REST API endpoint you need to provide details such as customer ID and the date range

. Validation is also performed for these fields.

## Database

![Alt text](./Rewards-API-backend/screenshots/db-image1.jpg)
![Alt text](./Rewards-API-backend/screenshots/db-image2.jpg)
![Alt text](./Rewards-API-backend/screenshots/db-image3.jpg)