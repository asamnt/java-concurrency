package concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class D_PrimeNumberConcurrencyWithJoining {

    public static void main(String[] args) {

        List<Thread> threads = new ArrayList<>();

        Runnable statusReporter = () -> {
            try{
                while (true){
                    Thread.sleep(5000);
                    printThreads(threads);
                }
            }catch (InterruptedException e){
                System.out.println("Status report thread interrupted. Ending status updates");
            }
        };
        Thread reporterThread = new Thread(statusReporter);
        reporterThread.setDaemon(true);//good example of a daemon thread - does not have any value on its own and should get killed if the main app shuts down
        reporterThread.start();

        int number;

        while(true){
            Scanner sc = new Scanner(System.in);
            System.out.println("\n I can tell you the nth prime number. Enter n: " );
            int n = sc.nextInt();
            if(n == 0) {
                //this means we want to end the program
                reporterThread.interrupt();//we interrupt the reporter thread that is printing the statuses instead of making it a daemon thread
                System.out.println("Waiting for all threads to finish...");
                try {
                    waitForThreads(threads);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Got interrupted while waiting for threads");
                }
                System.out.println("Done with the application  "+ threads.size() + "prime numbers calculated");
                break;
            }

            //we create a runnable instance using inline class
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    int number = PrimeNumberUtil.calculatePrime(n);
                    System.out.println("\n Result: ");
                    System.out.println("\n Value of " + n + "th prime : " + number);
                }
            };
            //we create a new Thread instance by passing the runnable instance
            Thread t = new Thread(r);
            threads.add(t);
//            t.setDaemon(true);
            t.start();//we start the thread
        }
    }

    private static void printThreads(List<Thread> threads) {
        threads.forEach((thread) -> System.out.println(thread.getState()));
    }

    private static void waitForThreads(List<Thread> threads) throws InterruptedException{
        for(Thread thread: threads){
            thread.join();
        }
    }
}
