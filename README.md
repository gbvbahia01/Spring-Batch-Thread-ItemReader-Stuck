# Spring-Batch-Threads-Monitor
A project that shows the impact of a customized ItemReader can have on Spring Batch Job execution.

##### Spring Batch [Reference Documentation](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html)

### What This Project is About
I made this project in order to show, in an easy way, a problem that I had when I created my own ItemReader.  
The problem that I named: **Thread ItemReader Stuck**.   
 
### What This Project is NOT About
This project is NOT trying to prove any type of bug or problem with Spring or Spring Batch.     
The framework works as expected and this project is trying to make you to understand how expected it is.

**This is NOT a tool that you plug in and see your Spring Batch threads processing.**

### The Big Picture
In order to you understand this project is necessary to understand my needs.   
In my work we have an endpoint that receives a request and process some information.
This process requires some steps and some requirements:
   1. Get more information about the product in another microservice.
   2. Define the type of product when get the information.
   3. Send to a MQ.
   * Needs to run concurrently. More than one pod at same time reading and changing the same database.
   * This flow must be made up 2 minutes after receive the request and the load capacity has to be 200 per minute.

Summing up: each microservice pod running needs to be capable to send 200 products information to MQ per minute.

### Running This Project
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
Wait up 1 minute to a Batch Job starts.   
What you will see:
![TEST](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor/blob/main/src/main/resources/docs/threads_not_stuck.png)
> Environment Mode

Behind the scenes, in fake package, I created a request process simulator that defines the period and amount of request received on the endpoint process request.
Is the endpoint responsible for populating the table that Spring Batch will read.
   1. TEST, TestAmountEnvironment.class, sends 60 requests each 10 seconds.
   2. PROD, ProdAmountEnvironment.class, the amount and period are random, but if it gets the maximum amount, that are 3, and the minimal period between requests, that is 0.5 second, will be the same amount per minute as TEST.

> Job Reader Mode

In the _threads.monitor.batch_ package is where I created all Batch classes. The ItemReader, _ProcessorItemReader_, has a List of _ItemReaderMode_.    
Change this menu will make the Job to restart and then ItemReader will have the selected behavior.
   1. RETURN_NULL: when an ItemReader does NOT found a process in the _processor_ table will return NULL.
   2. NEVER_NULL: does NOT matter if ItemReader found a process or not, it will *never* return NULL, but an Optional empty.
   3. COUNTER_TO_NULL: a static _AtomicInteger_ will count the amount of returns and, when get the limit, all threads will return NULL on the ItemReader.

> Threads Pool Info

See real-time performance of _ThreadPoolTaskExecutor_ on the class _CfgProcessorJob_.
   1. Yml Threads: amount of threads defined on application.yml (app.batch.threads.amount)
   2. Max Pool Size and Pool Size is defined as fields on _ThreadPoolTaskExecutor_ instance.
   3. Active Count: Amount of threads working at the moment on _ThreadPoolTaskExecutor_ 

> Process Status

This chart represents the queue to process on database. 
   1. Red is waiting to process.
   2. Orange is executing the processing now.
   3. Green is process finished.

> Job Execution Status

Is  the last Jobs execution. Status column is very important. 
   1. Started mens that the Job is running now.
   2. Finished means that the last Job is finished and a new one is not started yet.

> Processing Time

On the top you see the time took to process the last item processed. Cannot take more than 120 seconds. 

### The Main Concern
When you read the documentation provided by Spring about the [ItemReader](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html#item-reader) you have:   
_When the ItemReader has exhausted the items it can provide, it indicates this by returning null._      
What this impact in the Job process?      
In my case, because I don't take into account the impact of this snippet on the Job's lifecycle, a **roll back in production** was made.   

### My Idea To Deal With The Big Picture
   1. Create an endpoint to save the all requests coming up in a table.
   2. Create a Spring Batch Job to read from this table, process the product information and send to the MQ. 
   3. (**Here is the catch**)-> To meet the requirement number 4 on [The Big Picture](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor#the-big-picture) I will need to implement my own the ItemReader.  

Why should I use Spring Batch? 
   1. Spring Batch is easy to deal with concurrency situation.
   2. The ItemReader, CompositeProcessor and ItemWriter makes easy to split the work in small classes with one responsibility.
   3. Easy to increase the threads amount in order to send the goal of 200 information per minute. 
   4. And everything else described in the Spring Batch [documentation](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html#springBatchUsageScenarios).

I describe [Some Technical Information](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor#some-technical-information) about deal with pods concurrency by the end of this file. 

### TEST Environment 
After all implementation is time to test. I pushed up my project to TEST environment and start to send requests.   
Testing multiples scenarios, with 1, 2 and 3 pods. In all cases we did NOT have any problem.   
The goal of 200 information per minute was achieved easily.   

This application starts trying to simulate the same scenario I had in TEST:
![TEST](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor/blob/main/src/main/resources/docs/threads_not_stuck.png)

#### This image shows that:
> Environment Mode   

The simulation request it is TEST mode.

> Job Reader Mode

The ItemReader when does not have anything to process return NULL.   

#### Impact each of _ItemReaderMode_ has on the Job
Keeping this in mind: to a Job finish is necessary that all threads running return NULL on ItemReader
   1. RETURN_NULL When a ItemReader returns NULL Spring Batch will not replace that thread. Basically this means that if the maximum amount of thread starts with 10, now is 9.  
   2. NEVER_NULL If ItemReader never returns NULL the Job will never end. Spring Batch will create a new Thread to replace the finished thread forever.   
   3. COUNTER_TO_NULL Spring Batch will create a new thread to replace the finished until the ItemReader returns NULL.

Here we are back to the [The Main Concern](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor#the-main-concern):
_When the ItemReader has exhausted the items it can provide, it indicates this by returning null._

As we can see in TEST environment we have 60 request each 10 seconds, the batch process the 60 and start to return null. Giving time to the Job finish.   
The problem was in PROD environment. The microservice clients does not know about send each 10 seconds, they send any time they want.   
Imagine that the clients stop a while sending Jobs, enough time to 9 ItemReader return null, and start to send a lot of requests again.   
Because of that if the Job started with the limit of 10 threads, now is limited to 1. Spring Batch will not replace that 9 threads finished and the Job has 90% less processing power.   
Causing the Thread Stuck Job:
![PROD](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor/blob/main/src/main/resources/docs/thread_stuck.png)

The impact is easily seeing in this image:
   1. Time Processing is 66 seconds and will increase forever.
   2. The amount of threads is one. Is not enough to deal with all request waiting. It is getting in more than getting out.
   3. The current started Job, Id 13, will run forever because the left thread will never return null and the Spring Batch will replace te last finished thread always until the last ItemReader return NULL. 
   4. The red amount, waiting status, on the chart is a lot. 

##### The Roll Back
When I got this problem on production, some hours after deploy, I did not realize this situation that I wrote here.
I tried to run 3 pods at same time and that could NOT deal with this problematic scenario.
A roll back to stop working with the new microservice was made. And I started to dig into the microservice trying to understand what was happening.
After one week I could realize this scenario.

##### Two Solution
###### NEVER_NULL
This first one did not pleasant to me. I do not know the impact of *never* end a Job. As the reader will never return NULL the Job will never end.
I did not want to have a new problem using this option, and I do not recommend.

###### COUNTER_TO_NULL
This was the second and last that I found. Is easy to control, as I did here in this demo application. I am satisfied because the Job life cycle completes.
I defined 10 threads and the limit is working with the amount of waiting process. So after process the amount defined all ItemReader will return null and the Job will and.
Is important say here that this not mean that each thread will deal with the same amount.
In fact, I believe that after ItemWriter the thread is killed and a new one is created.
Is the reason you se the *Threads Pool Info* varies even with type of Job is NEVER_NULL. 

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

### H2 Database (In memory)

* driver class: org.h2.Driver
* jdbc url: jdbc:h2:mem:monitor
* username: sa
* password: password

> Console:   http://127.0.0.1:8080/h2-console

