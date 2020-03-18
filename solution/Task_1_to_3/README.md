## Solution

Designed the Application as a standard Spring Boot application with MongoDB as a database engine.

Used two types of persisting - In Memory (for task 4) and in DB (for tasks 1,2,3).

Task 2 is implemented with two approaches for dealing with potentially big amounth of data:
1. Client Side Pagination configuration (standard Spring implementation)
2. Reactive Controller with Reactive Mongo Driver. Please note that this is not testable via `Postman` as the app is working only with blocking calls.
The `curl` command can be copied from Swagger UI. It is necessary to add `text:event-stream` in the request headers.
The result will be the data popping one by one in the response instead of the blocking call with huge latency.

The application can be started by the provided `bat` files in the repository root.
For development the MongoDB container can be started by executing `docker-compose up` command from folder `mongo`.

The two different solutions for Tasks 1-3 and Task 4 can be started by the provided docker-compose commands in folder `docker`.
