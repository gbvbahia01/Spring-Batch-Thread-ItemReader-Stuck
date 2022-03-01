# Spring-Batch-Thread ItemReader Stuck
A project that shows the impact of a customized ItemReader can have on Spring Batch Job execution.

##### Spring Batch [Reference Documentation](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html)

### What This Project is About
I made this project in order to show, in an easy way, a problem that I had when I created my own ItemReader.  
The problem that I named: **Thread ItemReader Stuck**.   
 
### What This Project is NOT About
This project is NOT trying to prove any type of bug or problem with Spring or Spring Batch.     
The framework works as expected, and this project is trying to make you understand how expected it is.

**This is NOT a tool that you plug in and see all your threads working.**

### The Big Picture
In order for you understand this project, is necessary to understand my needs.   
At work, we have an endpoint that receives a request and processes some information.
This process requires some steps and some requirements:
   1. Get more information about the product in another microservice.
   2. It will define the type of product when it gets the information.
   3. Send the product type to a message queue (MQ).
   * It needs to run concurrently. More than one pod at a time reads and changes the same database.
   * This flow must be made up within 2 minutes after receiving the request, and the load capacity has to be 200 per minute.

Summing up, each microservice pod running needs to be capable of sending 200 products' information to MQ per minute.

### Running This Project
I invite you to download and run this project and continue reading this text to understand the simulation.

##### What You Need To Run
   1. Java JDK 11
   2. Maven  

This project uses H2, in memory database. No database set up is necessary.   
The port used is 8080. If you need to change, you must do in _application.yml_ in resources folder.   
After downloading, run the command:
```
mvn spring-boot:run
```   
Open a browser and go to: http://localhost:8080/      
Wait up to 1 minute for a Batch Job to start.   
What you will see:
![TEST](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor/blob/main/src/main/resources/docs/threads_not_stuck.png)
> Environment Mode

I created a request process simulator in the fake package that defines the period and amount of requests received on the endpoint process request.
This is the endpoint responsible for populating the table that Spring Batch will read.
   1. TEST, TestAmountEnvironment.class. It sends 60 requests every 10 seconds.
   2. PROD, ProdAmountEnvironment.class. The amount and period are random, but if it gets the maximum amount,  which is 3, and the minimal period between requests, which is 0.5 seconds, it will be the same amount per minute as TEST.

> Job Reader Mode

In the _threads.monitor.batch_ package is where I created all the batch classes. The ItemReader, _ProcessorItemReader_, has a list of _ItemReaderMode_.    
If you change this menu, the job will restart and ItemReader will have the selected behavior.
   1. RETURN_NULL: if an ItemReader cannot find a process in the _processor_ table, it returns NULL.
   2. NEVER_NULL: it does not matter if ItemReader finds a process or not; it will *never* return NULL, but an Optional empty.
   3. COUNTER_TO_NULL: a limit of returns is defined and a static _AtomicInteger_ will count the number of returns and, when reached, all threads will return NULL on the ItemReader.

> Threads Pool Info

See the real-time performance of _ThreadPoolTaskExecutor_ in the class _CfgProcessorJob_.
   1. Yml Threads: the number of threads defined in application.yml (app.batch.threads.amount).
   2. Max Pool Size and Pool Size are defined as fields on _ThreadPoolTaskExecutor_ instance.
   3. Active Count: Tte number of threads working at the moment on _ThreadPoolTaskExecutor_ 

> Process Status

This chart represents the queue to be processed on the database.   
   1. Red is waiting to be processed.
   2. Orange is executing the process now.
   3. Green means the process is finished.

> Job Execution Status

It is the last Job execution. The status column is very important. 
   1. Started: means that the job is currently running.
   2. Finished: this means that the last Job is finished and a new one has not started yet.

> Processing Time

At the top, you see the time it took to process the last item processed. It can not take more than 120 seconds. 

### The Main Concern
When you read the documentation provided by Spring about the [ItemReader](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html#item-reader), you have:   
_When the ItemReader has exhausted the items it can provide, it indicates this by returning null._      
How does this impact the job process?   
In my case, because I didn't take into account the impact of this snippet on the Job's lifecycle, a **roll back in production** was made.   

### My Idea To Deal With The Big Picture
   1. Create an endpoint to save the all requests coming up in a table.
   2. Create a Spring Batch Job to read from this table, process the product information, and send it to the MQ. 
   3. (**Here is the catch**)-> To meet requirement number 4 on [The Big Picture](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor#the-big-picture), I will need to implement my own ItemReader.  

Why should I use Spring Batch? 
   1. Concurrency situations are easy to deal with in Spring Batch. I can say it's transparent.
   2. The ItemReader, CompositeProcessor, and ItemWriter make it easy to split the work into small classes with one responsibility.
   3. It is easy to increase the number of threads in order to increase the power of processing.
   4. And everything else described in the Spring Batch [documentation](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/index-single.html#springBatchUsageScenarios).

I described [Some Technical Information](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor#some-technical-information) about dealing with pod concurrency at the end of this file.

### TEST Environment 
After all the implementation, it is time to test. I pushed my project to the TEST environment and started to send requests.     
Testing multiples scenarios, with 1, 2 and 3 pods. In all cases, we did not have any problems.   
The goal of sending 200 messages per minute to queue information was easily achieved.

This application starts trying to simulate the same scenario I had in TEST:
![TEST](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor/blob/main/src/main/resources/docs/threads_not_stuck.png)

#### This image shows that:
> Environment Mode   

The simulation request is in TEST mode.

> Job Reader Mode

The ItemReader returns NULL when it does not have anything to process.   

#### Each ItemReaderMode has an effect on the job.
Keeping this in mind, to finish a job, it is necessary that all threads running return NULL on ItemReader
   1. RETURN_NULL when an ItemReader returns NULL, Spring Batch will not replace that thread. Basically, this means that if the maximum number of threads started with 10, now it is 9.  
   2. NEVER_NULL if ItemReader never returns NULL the Job will never end. The Spring Batch will create a new thread to replace the finished thread forever.   
   3. COUNTER_TO_NULL Spring Batch will create a new thread to replace the completed one until the ItemReader starts returning NULL.

Here we are back to the [The Main Concern](https://github.com/gbvbahia01/Spring-Batch-Threads-Monitor#the-main-concern):
_When the ItemReader has exhausted the items it can provide, it indicates this by returning null._

As we can see in the TEST environment, we have 60 requests per 10 seconds. The batch process the 60 and starts to return null. Giving time to finish the Job.
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

#### The Roll Back
When I got this problem on production, some hours after deploy, I did not realize this situation that I wrote here.
I tried to run 3 pods at same time and that could NOT deal with this problematic scenario.
A roll back to stop working with the new microservice was made. And I started to dig into the microservice trying to understand what was happening.
After one week I could realize this scenario.

#### Two Solutions
###### NEVER_NULL
This first one did not pleasant to me. I do not know the impact of *never* end a Job. As the ItemReader will never return NULL the Job will never end.
I did not want to have a new problem using this option, and I do not recommend.

### The Solution That I Chose
###### COUNTER_TO_NULL
This was the second and last that I found. Is easy to control, as I did here in this demo application. I am satisfied because the Job life cycle completes.
I defined 10 threads and the limit is working with the amount of waiting process. So after process the amount defined all ItemReader will return null and the Job will finish.   
Is important to say here that this not mean that each thread will deal with the same amount.   
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

