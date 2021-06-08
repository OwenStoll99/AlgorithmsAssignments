import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author 100661873
 */

public class Main {
 
    public static void main(String[] args) throws IOException {
        
        Scanner fscan = new Scanner(System.in);
        //counter for limit on number checks 
        int N = 0;
        //while less than 10000 checks have been done
        while (fscan.hasNext() && N <= 10000){
            //get the line to check
            String line = fscan.next();
            //set up stack (java built in stack will work fine since the same logic applies to a homemade stack
            Stack stack  = new Stack ();
            //sift through the line character by character
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                //always push for a '('
                if (c == '(') {
                    stack.push(c);
                }
                if (c == ')') {
                    if (!stack.isEmpty()) { //if stack is not empty take a peek
                        if ('(' == (char) stack.peek())//if top is a '(' pop it    
                            stack.pop();
                    }
                    else //if stack is not empty there is an imbalance so push ')'
                        stack.push(c);
                }//characters other than ( and ) are irrelevant
            }
            //for correct balance stack will be empty after sifting through the string
            if (stack.isEmpty()) 
                System.out.println("correct");
            else
                System.out.println("incorrect");
            N++;
        }
    }
}