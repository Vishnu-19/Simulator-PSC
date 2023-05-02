import java.io.*;
import java.util.*;

class bimodal 
{
    void print(ArrayList<String> actual_pred,int correct_pred,int m,File filename,HashMap<Integer, Integer> counter)
    {
        int total_pred=actual_pred.size();
        int miss_pred = total_pred - correct_pred;
        System.out.println("COMMAND");
        System.out.println("java sim bimodal " + m + " " + filename);
        System.out.println("OUTPUT");
        System.out.println("number of predictions:         " + total_pred);
        System.out.println("number of mispredictions:      " + miss_pred);
        System.out.println("misprediction rate:            " + String.format("%.2f", miss_pred*100.00/total_pred) + "%");
        System.out.println("FINAL BIMODAL CONTENTS");
        for(Object k:counter.keySet())
        {
            System.out.println(k+"	    "+counter.get(k));
        }
    }

    //position to be taken
    int getPosition(String PC,int size)
    {
        int position=0;
        StringBuffer buffer=new StringBuffer();
        int address= Integer.valueOf(PC,16); 
        address = address >> 2;
        for(int i=size;i>0;i--)
        {
            buffer.append((address&1));
            address=address>>1;
        }
        position=Integer.valueOf(buffer.reverse().toString(),2);
        return position;
    }    

    void startPrediction(int m,ArrayList<String> actual_pred,ArrayList<String> PC,File filename)
    {
        //m variable - represents the length of prection table. Eg if m=6, then there are 2^6 = 64 entries in prediction table. 
        //actual_pred ArrayList - actual branch outcomes
        //PC ArrayList - branch address
        //filename - validation file name

        int correct_pred=0;
        char pred;

        HashMap<Integer, Integer> counter = new HashMap<>();
        
        for(int i = 0; i < Math.pow(2, m); i++) 
        {
            counter.put(i, 4);
        }              

        for(int i=0;i<actual_pred.size();i++)
        {

            int pos=getPosition(PC.get(i),m);

            pred = counter.get(pos) >= 4 ? 't': 'n';
            // count no of correct predictions
            correct_pred= actual_pred.get(i).charAt(0) == pred ? correct_pred+1 : correct_pred;

            //each counter is set to 3-bit by default. So, counter saturates between 0 and 7
            
            if(actual_pred.get(i).charAt(0)=='t')
            { 
                counter.put(pos,Math.min(7,counter.get(pos)+1));
            }
            else
            {    
                counter.put(pos,Math.max(0,counter.get(pos)-1));

            }

        }   

        print(actual_pred, correct_pred, m, filename,counter);

    }
    
}