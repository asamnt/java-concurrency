### Notes from **Javabrains** concurrency course

#### Threads
- single sequential flow of control
- a smaller unit of work than a program
- allows program to split into simultaneous running tasks

#### Process
- execution of programs is a process
- multiple programs running means multiple processes running
- binary instr loaded into memory
- gets access to resources like memory
- its own stack, heap, registers
- resources are protected from other processes
- does it own thing
- they are independent
- can be started/stopped without affecting others
- cannot talk to each other unless mechanisms are coded into the process

#### Diff between process and threads
- default execution mode of process is concurrent
- thread is a unit of execution in a process
- thread does the work of a process
- thread has shared resources like memory, heap storage
- processes can be single threaded or multithreaded

#### Java - threads
- A single process - JVM
- consists of various threads
- Application thread - main method
- other threads are doing garbage collection etc


```java
//Runnable is a functional interface
public class MyRunnable implements Runnable{
    public void run(){
        System.out.println("I am running");
    }
}

Runnable r = new MyRunnable();
r.run();//this will not create a new thread
```
#### Steps to follow to spin off a new thread
- Identify the code that you want to run in a separate thread
- put into a runnable - that is implement a Runnable interface as shown above
- put your code into the run method
- create a new thread from the runnable as shown below

```java
Thread t = new Thread(r);//this will create a new thread
t.start();
```

#### How this works
- jvn hands over the thread to the OS threading APIs and then the thread is keeping track of how the OS is running the thread
- thread is a like a metadata object
- when does a thread end
  - when the run method returns
  - an exception is thrown
  - interrupts
  
#### Syntax alternatives
```java
//inline class
Runnable r = new Runnable(){
    public void run(){
        System.out.println("I am running");
    }
}

//using a lambda
Runnable r = () -> System.out.println("Running");

//
new Thread(() -> System.out.println("Running")).start();

//you can extend a thread itself instead of implementing a Runnable
//not usually recommended
class MyThread extends Thread{
    public void run(){
        System.out.println("I am running");
    }
}
new MyThread.start()
```

#### Daemon threads
- when we spawn threads, the application ends only when the last thread ends
- what if we want to end the application without worrying about any threads that are still running?
- you create a daemon thread - this doesn't stop the app from ending/closing
- we typically use daemon threads when you want a background thread that needs to continuously run
- something like a monitoring thread - that needs to be run till the application is running - and not needed after that

#### Thread lifecycle
- Intuitively thinking - just created, running, blocked, terminated
- Actual States
  - New - creation of thread
  - Runnable - when the thread starts
  - Blocked
  - Waiting
  - Timed Waiting
  - Terminated
- Why is the state called Runnable, and not Running? - running _not controlled by JVM but by the OS_. depending on the cores available, scheduler of the OS schedules the thread
- Thread.State enum will tell the current state of the thread

```mermaid
graph LR
A[New]-->B[Runnable]
B --> C[Blocked]
C --> B
B --> D[Waiting]
D --> B
B --> E[Timed Waiting]
E --> B

B --> F[Terminated]
```

#### Joining Threads
- Joins a certain thread with another running thread
- we do this when we do not want to close the main program till all the threads it has spawned have stopped
- join takes the thread to 'Waiting'

#### Interrupt threads
```java
t.interrupt();
```

#### Parallelism vs Concurrency
- Assume a dual core CPU, and we spin off 3 threads , each taking a long time, does that mean the third thread will not run at all till we see one of the previous threads on either of cores ends?
- That's not how it works
- OS has a scheduler whose responsibility is to schedule all the threads 
- A thread might get unscheduled before it completes
- Scheduler
  - Tries to be fair
  - Honors priorities
  - Non deterministic
- Parallelism - Many tasks _**running**_ at the same time
  - needs multi core cpu
- Concurrency - Many tasks _**in progress**_ at the same time but not running at the same time
  - can be done with multi-core or single core cpu


#### Need for synchronization
- No communication, no data shared - these are simple tasks in concurrent programming
- We want to do multithreading because we want to make efficient use of the cores available
  ![image](./processor.png)
- Each core comes with its own memory called processor cache
- Memory that you think of your memory - RAM different from processor cache
- Processor cache - L1(specific to cores), L2(specific to cores) and L3 (shared cache)
- Memory to cache communication happens through system bus
- Processor is makin a copy in the internal cache and then copy it over back after processing to the system memory
- When multiple threads are working on same value of i - we may have a problem
- Threads that just read the data - no issues

#### Common Race conditions
- Check-then-act
  - between the check and the act, the value of the data may change
- Read-modify-write
  - you read one value, then it gets updated by some other thread, but you are not aware, and continue to work with the old value

#### Solving Race Conditions
- we co-ordinate when a certain thread gets access to a thread
- make sure only one thread can "pick up" a data element
- Lock and key model
  - No two threads can access the same piece of code at the same time
  - Synchronization of threads
  - control data, not code
  - provided by JVM

#### How Synchronization works
- JVM creates a "virtual" lock from the data element
- Thread tries to "acquire" a lock
- If it acquires it, it can execute the synchronized code
- Thread finishes executing block and releases lock
- All other threads that need to execute the same block will have to wait
- Monitor Lock
  - two different synchronized blocks can be synchronized with the same object ref
  - and that would mean as below two diff pieces of code sync with the obj1 ref
  - so two threads even if they are running two different methods, cannot run the synchronized block at the same time, as they are locked by the same obj ref
```java
public void methodA(){
    synchronized(obj1){
        
    }
}

public void methodB(){
    synchronized(obj1){

    }
}
```
- Locking using this ref - instance locking
  - when you do this you are locking on the instance, so two threads working on different instances of the same class can run the same synchronized blocks of code at the same time

```java
public void methodA(){
    synchronized(this){
        
    }
}

public void methodB(){
    synchronized(this){

    }
}
```
- Synchronized can be used for the complete method
```java
public synchronized increment(){

}
```

#### What does synchronization achieve?
- Mutual Exclusion - also commonly known as mutex
- Visibility
  - Value is read from the memory before block execution
  - Value is written to memory after block execution
- Structured Lock
  - Block structure using synchronized
  - Acquiring and releasing locks is implicit - we don't do it actually - we just mention where we need a lock
  - if exception occurs in the sync block, the JVM releases the lock - and we don't have to worry about it

#### Problems with mutual exclusion
- How to achieve concurrency issues?
  - Don't have shared state - no concurrency issues
  - Share only immutable values - we again don't have a problem
  - Use synchronization
- Is something thread safe - which means will it avoid race conditions?
- Disadvantages of using synchronized blocks
  - Performance
    - we need to apply it carefully
    - we chose the right object for locking
    - synchronize the bare min code necessary
  - Extreme Synchronization
  - Choosing the wrong lock - if we want thread executing method A to be able to execute it without any issues, we must acquire a lock on the methodB synchronized block and that means the object ref must be same(below it is different and could be a problem)
```java
public void methodA(){
    synchronized(obj1){
        //some other code
        methodB()
        
    }
}

public void methodB(){
    synchronized(obj2){

    }
}
```

#### Liveness - what does it mean?
- State of general activity and motion
- Requires a system to make progress
- Not stuck
- Something good will eventually occur
- Liveness issues with concurrency
  - Deadlock
    - Multiple threads are waiting for other threads - Thread A waiting for Thread B and vice versa
    ```java
    synchronized(obj1){
        synchronized(obj2){
    
        }
    }
    
    synchronized(obj2){
        synchronized(obj1){
    
        }
    }

    ```
    - Two threads invoking join on each other
  - Livelock - a smarter deadlock - we are trying to avoid deadlock using a perpetual "corrective" action
    - Thread 1 tries to get lock A
    - Thread 1 then tries to get lock B
    - if lock B not acquired in x ms, release lock A
    - Try again after some time
    - Imagine Thread 2 also doing the same thing - here we don't have a deadlock, but even then the application is not progressing because the threads are doing the same thing simultaneously
  - Starvation
    - Thread is ready to run but never given a chance
    - Low priority thread not scheduled by executor - Indefinite Postponement
    
#### How to avoid Liveness issues?
- No Java/JVM feature to avoid them
- Careful use of locks
- Avoid using more than one lock

#### Volatile keyword
- Marks a variable as "Do not cache"
- Every read/write is from memory
- Thread 1 write variable(to memory)
- Thread 2 reads variable(from memory)
- If the above operations are sequential then there is a guarantee that Thread 2 will read the value that Thread 1 has written
- This guarantee is because of the "volatile" keyword
- If no volatile keyword then it is possible that Thread 1 has written only to processor cache and not yet flushed to memory and Thread 2 is reading it
- This applies to other variables in the thread too





