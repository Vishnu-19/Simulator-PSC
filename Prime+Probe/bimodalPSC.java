import java.io.*;
import java.util.*;

class bimodalPSC 
{
    void print(ArrayList<String> actual_pred,int correct_pred,int m,File filename,HashMap<Integer, Integer> counter, int attack_pred, int total)
    {
        int total_pred=actual_pred.size();
        int miss_pred = total_pred - correct_pred;
        System.out.println("COMMAND");
        System.out.println("java sim bimodalPSC " + m + " " + filename);
        System.out.println("OUTPUT");
        System.out.println("number of predictions:         " + total_pred);
        System.out.println("number of mispredictions:      " + miss_pred);
        System.out.println("misprediction rate:            " + String.format("%.2f", miss_pred*100.00/total_pred) + "%");
        System.out.println("Attack Success Rate = "+100*(float)attack_pred/total);     
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

    void startPrediction(int m,ArrayList<String> actual_pred,ArrayList<String> PC,File filename, float prob)
    {
        //m variable - represents the length of prection table. Eg if m=6, then there are 2^6 = 64 entries in prediction table. 
        //actual_pred ArrayList - actual branch outcomes
        //PC ArrayList - branch address
        //filename - validation file name

        int correct_pred=0;
        char pred;
        int count=0;
        int pos_count=0;     

        HashMap<Integer, Integer> counter = new HashMap<>();
        
        for(int i = 0; i < Math.pow(2, m); i++) 
        {
            counter.put(i, 4);
        }              

        for(int i=0;i<actual_pred.size();i++)
        {
            int temp=0;
            char attacker;
            int pos=getPosition(PC.get(i),m);
            if(i%1000==0)
            {
                pos_count++;
                counter.put(pos,7);
            
            }
            pred = counter.get(pos) >= 4 ? 't': 'n';
            // count no of correct predictions
            correct_pred= actual_pred.get(i).charAt(0) == pred ? correct_pred+1 : correct_pred;

            //each counter is set to 3-bit by default. So, counter saturates between 0 and 7
            
            if(actual_pred.get(i).charAt(0)=='t')
            { 
                
                if(Math.random()<prob)
                {
                    counter.put(pos,Math.min(7,counter.get(pos)+1));
                }
            }
            else
            {
                   if(Math.random()<prob)
                {
                    counter.put(pos,Math.max(0,counter.get(pos)-1));
                }
            }
            if(i%1000==0)
            {
                temp=0;
                while(counter.get(pos)!=0){
                    if(counter.get(pos)>=4){
                        temp++;
                    }
                counter.put(pos,counter.get(pos)-1);
                }
                if(temp==4){
                    attacker='t';
                }
                else{
                    attacker='n';
                }
                if(attacker==actual_pred.get(i).charAt(0)){
                    count++;
                }            
            }
        }   
        print(actual_pred, correct_pred, m, filename,counter,count,pos_count);

    }
    
}