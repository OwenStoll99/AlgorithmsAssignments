import java.io.IOException;
import java.util.Scanner;
/**
 *
 * Oct 2019
 */


public class Main {
    
    public static void main (String[] args) throws IOException {
        //hold the fibonacci array from 0 to 60
        long fib [] = new long [61];
        //base case for the sequence
        fib [0] = 0;
        fib [1] = 1;
        //load the array with the rest of the sequence
        for (int i = 2; i < 61; i++) {
            fib[i] = fib [i-1] +fib [i-2];
        }
        //retrieve number of ints to be read
        Scanner s = new Scanner(System.in);
        int T = s.nextInt();
        //repeat T times
        for (int i = 0; i < T; i++) { 
            //retrieve number from user
            int N = s.nextInt();
            //print if number is within boundaries for this fib array
            if (N <= 60 && N >= 0)
                System.out.println ("Fib(" + N + ") = " + fib[N]);
        }
       
    } 
}
