package concurrency;

public class PrimeNumberUtil {
    public static int calculatePrime(int n){
        int number;
        int numberOfPrimesFound;
        int i;
        number = 1;
        numberOfPrimesFound = 0;
        while(numberOfPrimesFound < n){
            number++;
            for (i = 2; i <= number && number % i != 0; i++) {
                //we dont do anything in the for loop
                //this loop is just to reach to a prime number
                //this loop will exit if the number is equal to i - which means it is equal to itself
                //or this loop will exit if the number is divisble by any other number
            }
            if(i == number){//if the number is equal to i - which means it is divisible only by itself, we call it a prime
                numberOfPrimesFound++;
            }

        }
        return number;
    }

}
