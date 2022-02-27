# Spring-Batch-Threads-Monitor
A project that shows the impact of a customized ItemReader can have on Spring Batch Job execution.

##### Spring Batch [Reference Documentation](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html)

### What This Project is About
I made this project in order to show, in an easy way, a problem that I had when I created my own ItemReader.   

### What This Project is NOT About
This project is NOT trying to prove any type of bug or problem with Spring or Spring Batch.     
The framework works as expected and this project is trying to make you to understand how expected it is.

**This is NOT a tool that you plug in and see your Spring Batch threads processing.**

### Invite
I invite you to download and run this project and continue reading this text to understand the simulation made.

##### What You Need To Run
   1. Java JDK 11
   2. Maven  

This project use H2, in memory database. No database set up is necessary.   
The port used is 8080. If you need to change you must do in _application.yml_ in resources folder.   
After download execute: 
```
mvn spring-boot:run
```   
Open a browser and go to: http://localhost:8080/   

### How This Project Can Help You 
Understand how to set up your own ItemReader to avoid get thread reader stuck.

You also can use to simulate a Spring Batch application that you created changing the _CfgProcessorJob (stepExecuteProcessor)_ like yours and simulate your Job reality.   
As example: try to remove the _throttleLimit_ on the Step and see what will happen with the *Threads Pool Info*. 

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
   4. Needs to run concurrently. More than one pod at same time reading and changing the same database.

This flow must be made up 2 minutes after receive the request and the load capacity has to be 200 per minute.   
Summing up: the microservice needs to be capable to send 200 products information to MQ per minute.   

### My Simple Idea
   1. Create an endpoint to save the all requests coming up in a table.
   2. Create a Spring Batch Job to read from this table, process the product information and send to the MQ. 
   3. (**Here is the catch**)-> To meet the requirement number 4 on [The Big Picture](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor#the-big-picture) I will need to control the ItemReader my self.  

Why should I use Spring Batch? 
   1. Spring Batch is easy to deal with concurrency situation.
   2. The ItemReader, CompositeProcessor and ItemWriter makes easy to split the work in small classes with one responsibility.
   3. Easy to increase the threads amount in order to send the goal of 200 information per minute. 
   4. And all [here](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html#springBatchUsageScenarios) on Spring Batch documentation.

### Some Technical Information 
#### How to control the ItemReader in the same application running in multiples pods
###### JPA
Spring with JPA makes this easy. A method annotated with _@Lock(LockModeType.PESSIMISTIC_WRITE)_ can lock a row during a transaction event:
something like Interface ProcessorRepository:
```java
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Processor> findFirstByProcessStatusOrderById(ProcessStatus processStatus);
```
And a service can control the transaction, like ProcessorService
```java
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
In MongoDb the same thing can be achieved by calling on the Spring MongoTemplate method [findAndUpdate](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-template.find-and-upsert)  
Changing the status as presented in _findNextToBeProcessed()_ JPA. 

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

Here we are back to the [The Main Concern](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor#the-main-concern):
_When the ItemReader has exhausted the items it can provide, it indicates this by returning null._

As we can see in TEST environment we have 60 request each 10 seconds, the batch process the ten and start to return null. Giving time to the Job finish.
The problem was in PROD environment. The microservice clients does not know about send each 10 seconds, they send any time they want.
Imagine that the clients stop fo while to send Jobs, enough time to 9 ItemReader return null, and start to send a lot again.
Because of that if the Job started with the limit of 10 threads, now is limited to 5. Spring Batch will not replace that 9 threads finished and the Job has 90% less processing power.
Because of that whe have the Thread Stuck Job:
![PROD](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor/blob/main/src/main/resources/docs/thread_stuck.png)

The impact is easily seeing in this image:
   1. Time Processing is 66 seconds and will increase forever.
   2. The amount of threads is one. Is not enough to deal with all request waiting. It is getting in more than getting out.
   3. The current started Job, Id 13, will run forever because the left thread will never return null and the Spring Batch will replace te last finished thread always until ItemReader return NULL. 
   4. The red amount, waiting, on the chart is a lot. 

##### The Roll Back
When I got this problem on production, some hours after deploy, I did not realize this situation that I wrote here.
I tried to run 3 pods at same time and that could NOT deal with scenario that we had in production.
Roll back to stop to use the new microservice was made. And I started to dig into the microservice trying to understand what was happening.
After one week I could realize this scenario.

##### Two Solution
###### NEVER_NULL
This first one did not pleasant to me. I do not know the impact of *never* end a Job. As the reader will never return NULL the Job will never end.
I did not want to have a new problem using this option, and I do not recommend.

###### COUNTER_TO_NULL
This was the second and last that I found. Is easy to control, as I did here in this application demo, and the Job life cycle completes.
I defined 10 threads and each one must work with 20 process. So after 200 process returned all ItemReader will return null and the Job will and.
Is important say here that this not mean that each thread will deal with 20.
In fact, I believe that after ItemWriter the thread is killed and a new one is created.
Is the reason will se the *Threads Pool Info* varies even with type of Job is NEVER_NULL. 

##### (TEST) Different results in different machines
I ran this application in two different machines. The first one TEST was never stuck.
Testing in another machine sometimes the thread stuck problem happened on TEST as PROD.  
When the team test did all tests in TEST and QA, unfortunately, we never had the PROD situation.

### H2 Database (In memory)

* driver class: org.h2.Driver
* jdbc url: jdbc:h2:mem:monitor
* username: sa
* password: password

> Console:   http://127.0.0.1:8080/h2-console

