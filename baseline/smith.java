import java.io.*;
import java.util.*;

public class smith 
{    
    void print(ArrayList<String> actual_pred,int correct_pred,int n,File filename, int counter)
    {

        int total_pred=actual_pred.size();
        int miss_pred = total_pred - correct_pred; // miss_predictions = total_predictions-correct_predictions
        System.out.println("COMMAND");
        System.out.println("java sim smith "+ n +" " + filename);
        System.out.println("OUTPUT");
        System.out.println("number of predictions:         " + total_pred);
        System.out.println("number of mispredictions:      " + miss_pred);
        System.out.println("misprediction rate:            " + String.format("%.2f", miss_pred*100.00/total_pred) + "%");
        System.out.println("FINAL COUNTER CONTENT: "+ counter);    
    }

    void startPrediction(int n,ArrayList<String> actual_pred,File filename)
    {
        //n variable - represents number of bits in counter predictor. Eg if n=2, then there are 2^2 = 4 total states. So, counter saturates between 0 and 3. 
        //actual_pred ArrayList - actual branch outcomes
        //filename - validation file name

        int correct_pred=0;
        int counter=0;
        char predicted_value;

        int st_pos = (int)(Math.pow(2,n))-1; //to get the highest saturation point starting from 0
        
        //initialize counter to 1, 2, 4 and 8 when the number of bits is 1, 2, 3 and 4 respectively.

        counter = (int)(Math.pow(2,n))/2;

        int threshold = (int)(Math.pow(2,n))/2; //counter equal to or greater than threshold value is considered taken 

        for(int i=0;i<actual_pred.size();i++)
        {
            predicted_value = counter>=threshold ? 't' : 'n';
            correct_pred= actual_pred.get(i).charAt(0) == predicted_value ? correct_pred+1 : correct_pred;
            if(actual_pred.get(i).charAt(0)=='t')
            {
                counter = Math.min(st_pos,counter+1);
            }
            else
            {
                counter = Math.max(0,counter-1);
            }
        }
        
        print(actual_pred,correct_pred,n,filename,counter);      
    }
    
} 