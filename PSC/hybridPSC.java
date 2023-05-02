import java.io.*;
import java.util.*;

public class hybridPSC
{

    void print(ArrayList<String> actual_pred,int correct_pred,int k,int m_gshare,int n,int m_bimodal,HashMap<Integer, Integer> chooser_table, HashMap<Integer, Integer> gshare_counter,HashMap<Integer, Integer> bimodal_counter,File filename)
    {
        int total_pred=actual_pred.size();
        int miss_pred = total_pred - correct_pred;
        System.out.println("COMMAND");
		System.out.println("java sim hybridPSC "+k+" "+ m_gshare + " " + n + " " + m_bimodal + " " + filename);
		System.out.println("OUTPUT");
        System.out.println("number of predictions:         " + total_pred);
        System.out.println("number of mispredictions:      " + miss_pred);
        System.out.println("misprediction rate:            " + String.format("%.2f", miss_pred*100.00/total_pred) + "%");
        
        System.out.println("FINAL CHOOSER CONTENTS");        
        for(Object key:chooser_table.keySet())
        {
            System.out.println(key+"	    "+chooser_table.get(key));
        }	
        System.out.println("FINAL GSHARE CONTENTS");
        for(Object key:gshare_counter.keySet())
        {
            System.out.println(key+"	    "+gshare_counter.get(key));
        }	
		System.out.println("FINAL BIMODAL CONTENTS");
        for(Object key:bimodal_counter.keySet())
        {
            System.out.println(key+"	    "+bimodal_counter.get(key));
        }
    }

    public HashMap<Integer, Integer> initialize_chooser_table(int size)
    {
        double memory_size= Math.pow(2,size);
        HashMap<Integer, Integer> chooser_table= new HashMap<>();
        int i=0;
        while(i<memory_size){
            chooser_table.put(i,1);
            i=i+1;
        }
        return chooser_table;
    }
    
    //Initialize gshare and bimodal Counters based on the m_gshare and m_bimodal 
    public HashMap<Integer, Integer> initialize_counter(int m,HashMap<Integer, Integer>counter)
    {
        double memory_size= Math.pow(2,m);
        int i=0;
        while(i<memory_size){
            counter.put(i,4);
            i=i+1;
        }
        return counter;
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

    //Apply Bit masking for register updation
    public int genMask(int n , int value)
    {
         StringBuffer buffer=new StringBuffer();
        if(value==1){
        buffer.append('1');
        for(int i=1;i<n;i++)
        {
            buffer.append('0');
            
        }
        return Integer.valueOf(buffer.toString(),2);
        }
        else{
            buffer.append('0');
        for(int i=1;i<n;i++)
        {
            buffer.append('1');
        }
    
        return Integer.valueOf(buffer.toString(),2);

        }        
        
    }    

    void startPrediction(int k,int m_gshare,int n,int m_bimodal,ArrayList<String> actual_pred,ArrayList<String> counter,File filename)
    {

        //m (m_gshare, m_bimodal) variable - no. of bits in counter predictor. Eg if m=6, there are 2^6 = 64 entries in prediction table. 
        //n variable - no. of bits in global history branch register
        //k - represents the length of chooser table. i.e. there are 2^k entries in chooser table 
        //actual_pred ArrayList - actual branch outcomes
        //PC ArrayList - branch address
        //filename - validation file name

        int correct_pred = 0;
    
        char bimodal_pred;
        char bimodal;

        double probability;
        double p=0.6; //p represents the probability threshold for a branch to be taken
        double q=1-p; // p+q = 1

        HashMap<Integer, Integer> bimodal_counter=new HashMap<>();
        bimodal_counter=initialize_counter(m_bimodal,bimodal_counter);

        int register=0;
        HashMap<Integer, Integer> gshare_counter=new HashMap<>();
        gshare_counter=initialize_counter(m_gshare,gshare_counter);        

        //Initialize chooser table
        HashMap<Integer, Integer> chooser_table=initialize_chooser_table(k);

        //create filters
            int bitmask1=genMask(n, 1);
            int bitmask2=genMask(n, 2);

        //read values
        for(int i = 0; i < actual_pred.size(); i++)
        {
            //Bimodal prediction
            int bimodal_pos = getPosition(counter.get(i), m_bimodal);

            bimodal_pred = bimodal_counter.get(bimodal_pos) >= 4 ? 't': 'n';
            
            bimodal =  bimodal_pred==actual_pred.get(i).charAt(0)?'t':'n';
            

            //Gsahre prediction
            char gshare_pred;
            char gshare;

            int gshare_pos = getPosition(counter.get(i), m_gshare);
            gshare_pos=gshare_pos^register;

            register=register>>1;

            gshare_pred= gshare_counter.get(gshare_pos)>=4?'t':'n';
            gshare =  gshare_pred==actual_pred.get(i).charAt(0)?'t':'n';
            
            int chooser_table_label = getPosition(counter.get(i), k);
            int choice=chooser_table.get(chooser_table_label);

            //if choice>1 then consider gshare else bimodal
            if(choice > 1)
            {
                if(actual_pred.get(i).charAt(0) == 't') {
                    register = register | bitmask1;
                    probability = (double)gshare_counter.get(gshare_pos)/8; //probability of taken = Counter_current_state/total states

                    if(Math.random()>((p*probability)+q))
                    {
                        gshare_counter.put(gshare_pos,Math.min(7,gshare_counter.get(gshare_pos)+1));
                    }
                }
                else {
                    register = register & bitmask2;
                    probability = (double)(8-gshare_counter.get(gshare_pos))/8; //probability of Not taken = 1- probability of taken

                    if(Math.random()>(p*probability*q))
                    {
                        gshare_counter.put(gshare_pos,Math.max(0,gshare_counter.get(gshare_pos)-1));
                    }
                }
        
                    correct_pred = gshare == 't' ? correct_pred+1 : correct_pred;
                
            }
            else 
            {
                if(actual_pred.get(i).charAt(0) == 't') {
                    register = register | bitmask1;
                    probability = (double)bimodal_counter.get(bimodal_pos)/8; //probability of taken = Counter_current_state/total states

                    if(Math.random()>((p*probability)+q))
                    {
                        bimodal_counter.put(bimodal_pos,Math.min(7,bimodal_counter.get(bimodal_pos)+1));
                    }
                    
                }
                else { 
                    register = register & bitmask2;
                    probability = (double)(8-bimodal_counter.get(bimodal_pos))/8; //probability of Not taken = 1- probability of taken

                    if(Math.random()>(p*probability*q))
                    {
                        bimodal_counter.put(bimodal_pos,Math.max(0,bimodal_counter.get(bimodal_pos)-1));
                    }
                }
                correct_pred  = bimodal == 't' ? correct_pred+1 : correct_pred ;
            }

            //Chooser table gets incremented when gshare is considered but gets decremented when bimodal gets taken
            //saturates between 0 and 3
            if(gshare=='t' && bimodal =='n'){
                if(Math.random()>(p*probability*q)){
                chooser_table.put(chooser_table_label,Math.min(3,chooser_table.get(chooser_table_label)+1));
                }
            }
            else if( gshare=='n' && bimodal =='t'){
                if(Math.random()>(p*probability*q)){
                chooser_table.put(chooser_table_label,Math.max(0,chooser_table.get(chooser_table_label)-1));
                }
            }
        }

        print(actual_pred, correct_pred,k, m_gshare, n, m_bimodal, chooser_table, gshare_counter, bimodal_counter, filename);
        
    }
}

