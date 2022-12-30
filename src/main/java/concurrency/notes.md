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

