# Spring-Batch-Threads-Monitor
A project that shows the impact of ItemReader in Spring Batch Job execution.

##### Spring Batch [Reference Documentation](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html)

##### Eclipse [Code Formatter](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml)
> The formatter style is in folder src/main/resources/google_styleguide.xml.   
To import: _preferences/Java/Code Style/Formatter/Import..._
 
### What This Project is About
I made this project in order to show, in an easy way, a problem that I had when I created my own ItemReader.   

### What This Project is NOT About
This project is NOT trying to prove any type of bug or problem with Spring or Spring Batch.     
The framework works as expected and this project is trying to make you to understand how expected it is.

### The Main Concern
When you read the documentation provided by Spring about the [ItemReader](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html#item-reader) you read:   
_When the ItemReader has exhausted the items it can provide, it indicates this by returning null._      
What this impact in the Job process?      
In my case, because I don't take into account the impact of this snippet on the Job's lifecycle, a **roll back in production** was made.   

### The Big Picture
In my work we have an endpoint that receives a request and process some information.
This process requires three steps: 
   1. Get more information about the product in another microservice.
   2. Define the type of product when get the information.
   3. Send to a MQ.   
   4. Needs to run concurrently. More than one pod at same time on same database.

This flow must be made up 2 minutes after receive the request and the load capacity has to be 200 per minute.   
So the microservice needs to be capable to send 200 products information to MQ per minute.   

### My Simple Idea
   1. Create an endpoint to save the request in a table
   2. Create a Spring Batch Job to read from this table, process the product information and send to the MQ. 
   3. (**Here is the catch**)-> To meet the requirement number 4 on **The big picture** I will need to control the ItemReader my self.  

Why not? 
   1. Spring Batch is easy to deal with concurrency situation.
   2. The ItemReader, CompositeProcessor and ItemWriter makes easy to split the work in small classes with one responsibility.
   3. Easy to increase the threads amount in order to send the goal of 200 information per minute. 
   4. And all [here](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html#springBatchUsageScenarios) on Spring Batch documentation.

#### How to control the ItemReader in the same application running in multiples pods
###### JPA
Spring with JPA makes this easy. A method annotated with _@Lock(LockModeType.PESSIMISTIC_WRITE)_ can lock a row during a transaction event:
something like Interface ProcessorRepository:
```
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Processor> findFirstByProcessStatusOrderById(ProcessStatus processStatus);
```
And a service can control the transaction, like ProcessorService
```
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Optional<Processor> findNextToBeProcessed() {

    Optional<Processor> nextOpt =
        processorRepository.findFirstByProcessStatusOrderById(ProcessStatus.WAITING);

    if (nextOpt.isEmpty()) {
      return nextOpt;
    }
    
    Processor toProcess = nextOpt.get();
    toProcess.setProcessStatus(ProcessStatus.PROCESSING);
    toProcess.setStartProcess(LocalDateTime.now());
    processorRepository.save(toProcess);

    return Optional.of(toProcess);
  }
```
When _findNextToBeProcessed()_ method returns the row is changed to PROCESSING status and is not locked anymore.

###### MongoDB
I did the environment pod block in MongoDB calling on the Spring MongoTemplate method [findAndUpdate](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-template.find-and-upsert)  
Changing the status as in _findNextToBeProcessed()_ JPA. 

### TEST Environment 
After all implementation is time to test. I pull up my project in TEST environment and start to send requests.
Testing multiples scenarios, with one pod, 2 and 3 pods. In all cases we do NOT have any problem.
The goal of 200 information per minute was achieved easily.

This application starts with the same scenario I got in TEST:
![TEST](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor/blob/main/src/main/resources/docs/threads_not_stuck.png)

This image shows that:
##### Environment Mode
This is the period and amount of request received on the endpoint.
The endpoint that populate the table that Spring Batch will read.
   1. TEST, TestAmountEnvironment, sends 60 requests each 10 seconds.  
   2. PROD, ProdAmountEnvironment, the amount and period are random, but if it gets the maximum speed of amount, that is 3, and the minimal period between requests, that is 0.5 second, will be the same amount per minute as TEST.    

##### Job Reader Mode
This is **the key of the problem**. Changes here will define what type of _ItemReaderMode_ will be used on _ProcessorItemReader_.
   1. RETURN_NULL When an ItemReader does NOT found a process in the _processor_ table will return NULL.
   2. NEVER_NULL When an ItemReader does NOT matter if a process was found or not, it will *never* return NULL, but an Optional empty.
   3. COUNTER_TO_NULL A static _AtomicInteger_ will count the amount of returns and when get the limit all threads will return NULL on the reader.

###### Impact each of _ItemReaderMode_ has on the Job
Keeping this in mind: to a Job finish is necessary that all threads running return NULL on ItemReader
   1. RETURN_NULL When a Reader returns NULL Spring Batch will not replace that thread. Basically this means that if the maximum amount of thread starts with 10, now is 9.  
   2. NEVER_NULL If Reader never returns NULL the Job will never end. Spring Batch will create a new Thread to replace the finished thread forever.   
   3. COUNTER_TO_NULL Spring Batch will create a new thread to replace the finished until the reader returns NULL.

Here we are back to the [The Main Concern](#The Main Concern):
_When the ItemReader has exhausted the items it can provide, it indicates this by returning null._

As we can see in TEST environment we have 60 request each 10 seconds, the batch process the ten and start to return null. Giving time to the Job finish.
The problem was in PROD environment. The microservice clients does not know about send each 10 seconds, they send any time they want.
Imagine that the clients stop fo while to send Jobs, enough time to 5 ItemReader return null, and start to send a lot again.
Because of that if the Job started with the limit of 10 threads, now is limited to 5. Spring Batch will not replace that 5 threads finished and the Job has 50% less processing power.
Because of that whe have the Thread Stuck Job:
![PROD](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor/blob/main/src/main/resources/docs/threads_stuck.png)

##### Different results in different machines
I ran this application in two different machines. The first one TEST was never stuck.
Testing in another machine sometimes the thread stuck problem happened on TEST as PROD.  
When the team test did all tests in TEST and QA, unfortunately, we never had the PROD situation.

### H2 Database (In memory)

* driver class: org.h2.Driver
* jdbc url: jdbc:h2:mem:monitor
* username: sa
* password: password

> Console:   http://127.0.0.1:8080/h2-console

