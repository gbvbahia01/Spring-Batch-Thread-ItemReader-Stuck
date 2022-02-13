# Spring-Batch-Threads-Monitor
A project that shows the threads life cycle in Spring Batch Job

### H2 Database (In memory)

* driver class: org.h2.Driver
* jdbc url: jdbc:h2:mem:monitor
* username: sa
* password: password

> Console:   http://127.0.0.1:8080/h2-console


### Processor Reques Example:

> POST: http://localhost:8080/api/v1/step/threads/monitor/processor
```json
{
    "name": "John Connor",
    "dataToProcess": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
    "urlToCall": "http://some.url.com/some/path"
}
```