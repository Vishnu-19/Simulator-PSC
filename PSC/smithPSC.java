import java.io.*;
import java.util.*;

public class smithPSC 
{    
    void print(ArrayList<String> actual_pred,int correct_pred,int n,File filename, int counter)
    {

        int total_pred=actual_pred.size();
        int miss_pred = total_pred - correct_pred; // miss_predictions = total_predictions-correct_predictions
        System.out.println("COMMAND");
        System.out.println("java sim smithPSC "+ n +" " + filename);
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

        double probability;
        double p=0.6; //p represents the probability threshold for a branch to be taken
        double q=1-p; // p+q = 1

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
                probability = (double)counter/(st_pos+1); //probability of taken = Counter_current_state/total states

                if(Math.random()>((p*probability)+q))
                {
                    counter = Math.min(st_pos,counter+1); 
                }                
            }
            else
            {
                probability = (double)((st_pos+1)-counter)/(st_pos+1); //probability of Not taken = 1- probability of taken

                if(Math.random()>(p*probability*q))
                {
                    counter = Math.max(0,counter-1);
                }
            }
        }
        
        print(actual_pred,correct_pred,n,filename,counter);      
    }
    
} 