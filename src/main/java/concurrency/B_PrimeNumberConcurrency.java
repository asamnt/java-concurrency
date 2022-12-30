package concurrency;

import java.util.Scanner;

public class B_PrimeNumberConcurrency {

    public static void main(String[] args) {
        int number;

        while(true){
            Scanner sc = new Scanner(System.in);
            System.out.println("\n I can tell you the nth prime number. Enter n: " );
            int n = sc.nextInt();
            if(n == 0) break;

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
            t.start();//we start the thread
        }
    }
}
