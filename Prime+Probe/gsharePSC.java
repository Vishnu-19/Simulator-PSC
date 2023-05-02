import java.io.*;
import java.util.*;

class gsharePSC
{
    void print(ArrayList<String> actual_pred,int n,int correct_pred,int m,File filename,HashMap<Integer, Integer> counter,int attack_pred, int total)
    {
        int total_pred=actual_pred.size();
        int miss_pred = total_pred - correct_pred;
        System.out.println("COMMAND");
        System.out.println("java sim gsharePSC "+ m +" "+n +" " + filename);
        System.out.println("OUTPUT");
        System.out.println("number of predictions:         " + total_pred);
        System.out.println("number of mispredictions:      " + miss_pred);
        System.out.println("misprediction rate:            " + String.format("%.2f", miss_pred*100.00/total_pred) + "%");
        System.out.println("Attack Success Rate = "+100*(float)attack_pred/total);     
    }

    //Apply Bit masking for register updation
    int genMask(int n , int value)
    {
        StringBuffer buffer=new StringBuffer();

       if(value==1)
       {

            buffer.append('1');
            //initialize all bits in global branch history register to 0
            for(int i=1;i<n;i++)
            {
                buffer.append('0');
                
            }
            
            return Integer.valueOf(buffer.toString(),2);
       }
       else
       {
            buffer.append('0');
            for(int i=1;i<n;i++)
            {
                buffer.append('1');
            }
            return Integer.valueOf(buffer.toString(),2);
       }
    }

    //position to be taken
    int getPosition(String PC,int size)
    {
        StringBuffer buffer=new StringBuffer();
        int address= Integer.valueOf(PC,16);
        address = address >> 2;
        for(int i=size;i>0;i--)
        {
            buffer.append((address&1));
            address=address>>1;
        }
        return Integer.valueOf(buffer.reverse().toString(),2);
    }    

    void startPrediction(int m,int n,ArrayList<String> actual_pred,ArrayList<String> PC,File filename, float prob)
    {
        //m variable - represents the length of prection table. Eg if m=6, then there are 2^6 = 64 entries in prediction table. 
        //n variable - no. of bits in global history branch register
        //actual_pred ArrayList - actual branch outcomes
        //PC ArrayList - branch address
        //filename - validation file name
        
        char pred;
        int register=0;
        int correct_pred=0;
        int pos_count=0;
        int count=0;
        HashMap<Integer, Integer> counter = new HashMap<>();

        //Initialize counter to 4
        for(int i = 0; i < Math.pow(2, m); i++) 
        {
            counter.put(i, 4);
        }
      
        //Generate Bitmasks 
        int bitmask1=genMask(n, 1);
        int bitmask2=genMask(n, 2);

     
        for(int i=0;i<actual_pred.size();i++)
        {
            int temp=0;
            char attacker;
            
            int pos=getPosition(PC.get(i),m);
            pos = pos ^ register;
            if(i%1000==0)
            {
                pos_count++;
                counter.put(pos,7);
            
            }
			pred = counter.get(pos) >= 4 ? 't': 'n';
            // count no of correct predictions

            correct_pred= actual_pred.get(i).charAt(0) == pred ? correct_pred+1 : correct_pred;
              
            //left shift register
            register = register >> 1;
            
            //each counter is set to 3-bit by default. So, counter saturates between 0 and 7
			if(actual_pred.get(i).charAt(0) == 't') 
            {
                //OR
				register = register | bitmask1;

                if(Math.random()<prob)
                {
                    counter.put(pos,Math.min(7,counter.get(pos)+1));
                }
			}
			else 
            {
                //AND
				register = register & bitmask2;

              
                if(Math.random()<prob)
                {
                    counter.put(pos,Math.max(0,counter.get(pos)-1));
                }
			}
             if(i%1000==0){
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
        print(actual_pred,n, correct_pred, m, filename,counter,count, pos_count);
    }
}