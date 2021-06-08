
/**
 *
 * Nov. 2019
 */

//imports are used only for file reading and IO 
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;


public class PlagiarismDetector {
    
    //class used as data type for dictionaries constructed in the program
    public static class dictionaryData {
        //holds the word stored at this index of the dictionary
        String data;
        //holds the count of the number of uses of the word in the material
        int count = 0;
    }
    //M is used as the size of the dictionary
    //Large prime number for a spacious dictionary to avoid collisions while hashing 
    private static final int M = 2689;
    
    
    //method used for reading the file and returning the file contents in array format where each entry in the array is a word
    //argument is the path to the file desired to be read
    public static String[] readFile(String fileName) throws IOException {
        //declare strings to hold words retrieved from file
        String[] words;
        //initialize readers used to read file contents
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        
        //delare input data to hold the buffer of the entire input read from the file
        String inputData="";
        //input holds the current line being read by the BufferedReader
        String input;
        
        //while the line read is not null
        while((input = br.readLine()) != null){
            //format the line to remove non UTF-8 characters present in the input, which is required as many of the input files given for this assignment are NOT encoded in UTF-8 for some reason
            input = input.replaceAll("[^\\x00-\\x7F]", "");   
            //remove control characters from the input. newline, tab, etc.
            input = input.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
            //remove non - printable characters from the input
            input = input.replaceAll("\\p{C}", "");
            //replace punctuation not including spaces as only character [a - z] and numbers need to be considered in this context
            input = input.replaceAll(",|\\.|\"|\\(|\\)|\\{|\\}", "");
            //add the formatted line of data to the total input
            //input is formatted to be lower case now so that later in the process case is not an issue
            inputData = inputData +" "+ input.toLowerCase();
        }
        //split inputData on spaces, so each entry in the resultant array will be a word
        words = inputData.split(" ");
        
        return words;
    }
    
    //string hashing function returning a key to insert elements into a dictionary based on 'folding' the string every five characters
    private static int hash(String s){
        //sum holds the total value accumlated in hashing process
        long sum = 0;
        //number of 5 character substrings
        int intLength = s.length() / 5;
        
        //iterate through the string for the number of 5 character substrings
        for (int i = 0; i < intLength; i++) {
            
            //get current char array from substring
            char c[] = s.substring(i*5, (i*5)+5).toCharArray();
            //multiplication factor for each character in the substring
            long mult = 1;
            //iterate over substring
            for (int k = 0; k < c.length; k++){
                //add ascii value of current character multiplied by current multiplication factor
                sum+= c[k] * mult;
                //multply the factor by 256 for each character, 256 is chosen as it is the number of values that can be stored in a byte
                mult *= 256;
            }
        }
        
        //create substring for any characters at the end of the string that remained when dividing by 5
        char c[] = s.substring(intLength * 5).toCharArray();
        //multiplication factor for each character in the substring
        long mult = 1;
        //iterate over remaining characters
        for (int k =0; k<c.length; k++){
            //add ascii value of current character multiplied by current multiplication facto
            sum+= c[k] * mult;
            //multply the factor by 256 for each character, 256 is chosen as it is the number of values that can be stored in a byte
            mult *= 256;
        }
        //retun the key from hashing as the sum modulo the length of the dictionary 
        return (int) (Math.abs(sum) % M);
    }
    
    //creating a dictionary data structure for the input words, which in this case just a long array 
    public static dictionaryData[] createDictionary(String[] words){
        //create array of dictionary data type
        //two values data, the word stored at that index and count, the number of times the word is used
        dictionaryData[] dictionary = new dictionaryData[M];
        
        //for each word in the source material
        for (String word : words) {
            //calculate the hash key of the word
            int index = hash(word);
            //check if there is data stored at that index
            if (dictionary[index] != null) {
                //check if the word stored at that index is a different word
                if (!word.equals(dictionary[index].data)) {
                    //k is the next index in the array modulo M to avoid out of bound errors if at the max index
                    int k = (index + 1)%M;
                    //cycle through the array until k points to a free slot
                    while (dictionary[k] != null) {
                        //if the same word is found, break out of the loop
                        if (word.equals(dictionary[k].data)) {
                            break;
                        }
                        //increment k to the next index in the array
                        k = (k + 1)% M;
                    }
                    //once the loop is escaped either at free slot or duplicate word so set data equal to the current word and increment the counter
                    if (dictionary[k] == null) {
                        dictionary[k] = new dictionaryData();
                        dictionary[k].data = word;
                        dictionary[k].count ++;
                    }
                    else {
                        dictionary[k].count ++;
                    }
                    
                    
                } else {
                    //if the data is the same word increment the count for this word
                    dictionary[index].count ++;
                }
            //if there is no data stored at that index store the word there
            } else {
                dictionary[index] = new dictionaryData();
                dictionary[index].data = word;
                //increment count of the number of times the word is used
                dictionary[index].count ++;
            }
        }
        //return the completed dictionary
        return dictionary;
    }
    
    //returns the double score of the simularity between the two files
    public static double checkSimularity(String[] reviewWords, dictionaryData[] sourceDict, dictionaryData[] reviewDict){
        //declare score to hold the score from the file
        double score;
        //holds the number of identical words between the review file and source file
        int identicalCount = 0;
        
        //cycle through each word in the review file to see if it is present in the source dictionary
        for (String reviewWord : reviewWords) {
            //get the index pointed to by hashing the word
            int index = hash(reviewWord);
            //if there is no word present at that index in the source file dictionary move on to the next word
            if (sourceDict[index] != null) {
                //if there is a value at that index check if it is equal to the current review work
                //also check if the word count is above 0 for this word
                if (reviewWord.equals(sourceDict[index].data) && sourceDict[index].count>0) {
                        //check if the corresponding word is in the same spot in the review dictionary and has a count above 0
                        if (reviewWord.equals(reviewDict[index].data)) {
                            if (reviewDict[index].count>0){
                                //increment the number of identical words
                                identicalCount++;
                                //lower the uses remaining of the words in both dictionaries
                                reviewDict[index].count--;
                                sourceDict[index].count--;
                            }
                        //if the key leads to a different word in the review dictionary use linear polling to locate it
                        } else {
                            //k is the next index in the dictionary
                            int k = (index + 1)% M;
                            //break once the index of the word is found
                            while (!reviewWord.equals(reviewDict[k].data)) {
                                //increment k until k is the index of the review word
                                k = (k + 1)% M;
                            }
                            //if there are still more than 0 uses left for the word
                            if (reviewDict[k].count>0){
                                //increment the number of identical words
                                identicalCount++;
                                //lower the uses remaining of the words in both dictionaries
                                reviewDict[k].count--;
                                sourceDict[index].count--;                                    
                            }
                        }
                //if the word pointed to by the key is not the reviewword we use linear polling to check if the word is present    
                } else {
                    //have k equal the next index of the dictionary
                    int k = (index + 1)% M;
                    boolean found = false;
                    //if k points to a null value the review word isn't present in the source file
                    while (sourceDict[k] != null) {
                        //if a match is found break out of the loop
                        if (reviewWord.equals(sourceDict[k].data)) { 
                            found = true;
                            break;
                        }
                        //increment k
                        k = (k + 1) % M;
                    }
                    //if k points to a word check if there are remaining uses for this word
                    if (found){
                        if (sourceDict[k].count>0) {
                            //check if the corresponding word is in the same spot in the review dictionary and has a count above 0
                            if (reviewWord.equals(reviewDict[index].data)) {
                                if (reviewDict[index].count>0){
                                    //increment the number of identical words 
                                    identicalCount++;
                                    //lower the uses remaining of the words in both dictionaries
                                    reviewDict[index].count--;
                                    sourceDict[k].count--;
                                }
                            //if the key leads to a different word in the review dictionary use linear polling to locate it  
                            } else {
                                //p is the next index of the dictionary
                                int p = (index + 1)% M;
                                //break once the index of the word is found
                                while (!reviewWord.equals(reviewDict[p].data)) {
                                    //increment p until p is the index of the review word
                                    p = (p + 1)% M;
                                }
                                //if there are still more than 0 uses left for the word
                                if (reviewDict[p].count>0){
                                    //increment the number of identical words
                                    identicalCount++;
                                    //lower the uses remaining of the words in both dictionaries
                                    reviewDict[p].count--;
                                    sourceDict[k].count--;                                    
                                }
                            }
                        }
                    }
                }
            }
        }
        
        //calculate the ammount of words in the review file present in the source
        score = (double)identicalCount / (double)(reviewWords.length );
        return score;
    }
    
    public static void main (String[] args) throws IOException {
        //declare variables to hold file paths
        String sourceFile = "";//may enter directory here to speed up reviewing
        String reviewFile = "";//may enter directory here to speed up reviewing
        
        //read file path information from user
        try (Scanner inScan = new Scanner (System.in)) {
            System.out.println("Enter Source File Path");
            sourceFile += inScan.next();
            System.out.println("Enter review File Path");
            reviewFile += inScan.next();
        }
        //declare array to hold formatted file content retreived using readFile method
        String[] sourceContent = readFile(sourceFile);
        //declare array to hold formatted file content retreived using readFile method
        String[] reviewContent = readFile(reviewFile);
        
        
        //declare source dictionary and fill it using formatted content from sourceContent
        dictionaryData[] sourceDict = createDictionary(sourceContent);
        
        
        //declare source dictionary and fill it using formatted content from reviewContent
        dictionaryData[] reviewDict = createDictionary(reviewContent);
        
        //check the simularity between the source and review files
        double score = checkSimularity(reviewContent, sourceDict, reviewDict);
        //Display simularity score
        System.out.println(score);
        
    }
    
}
