# Performance Testing with Gatling

This project demonstrates performance testing using Gatling, an open-source load testing tool. The test script simulates various HTTP requests and applies load to an API. The guidelines for this project include using IntelliJ as the IDE, writing your own test script (without using Gatling recorder), feeding data inputs from an external CSV file, using proper assertions for validation, and chaining scenarios.

## Prerequisites

- IntelliJ IDE
- Maven

## Getting Started

1. Clone the repository or create a new Maven project in IntelliJ.
2. Import the project into IntelliJ and set up the Gatling plugin.
3. Create an external CSV file to provide data inputs for the requests. The CSV file should contain the necessary data fields for each request.
4. Modify the test script (`ApiPerform.scala`) to customize the API endpoints, request headers, and request bodies based on your requirements.
5. Configure the load simulation parameters in the test script. Make sure the load values (e.g., `atOnceUserCount`, `constantUserCount`, `rampUserCount`) are configurable through the terminal.
6. Execute the performance tests using the Maven command: 
7. After the test execution completes, Gatling will generate an HTML report with detailed performance metrics.

## Test Script Overview

The test script (`ApiPerform.scala`) contains the following key components:

- `atOnceUserCount`, `constantUserCount`, and `rampUserCount`: These variables define the number of users for different injection profiles.
- `commonHeaders`: This map contains the common headers to be used in HTTP requests.
- `commonRequest`: This chain sets a random value in the session for each request.
- `chainedScenario`: This scenario chains multiple requests together with pauses between each request.
- Load Simulation Configuration:
- `setUp`: This method configures the load simulation by defining the injection profiles for different user counts and durations.
- `protocols`: This method sets the base URL for the HTTP requests.
- `maxDuration`: This sets the maximum duration of the load simulation.
- `assertions`: These assertions define the performance thresholds to be checked during the load test.

## Report Analysis

After executing the performance tests, review the generated HTML report to analyze the performance metrics. The report provides insights into response times, request counts, and other key performance indicators. Refer to the Gatling documentation for detailed explanations of the report metrics.

## Gitignore

The project includes a `.gitignore` file to exclude irrelevant files from the repository. It already contains entries to ignore IntelliJ IDEA configuration files and Maven's target directory. Make sure not to check in any unnecessary files to the repository.