import java.io.*;
import java.util.*;

public class sim 
{
    static void getValues(ArrayList<String> actual_pred,ArrayList<String> PC,File filename) throws Exception //can throw a file not found exception
    {

        Scanner value=new Scanner(filename);

        while(value.hasNext()){
            String instr=value.next();
            if(instr.length()==1){
                actual_pred.add(instr);    
            }
            else{
                PC.add(instr);  
            }
        } 

        value.close(); // releases system resources
    }

    public static void main(String args[]) throws Exception //can throw io exception
    {
        String predictor= args[0];
        int m, n, k, n_bits,m_bimodal,m_gshare; 
        //m,m_bimodal,m_gshare variable - represents the length of prection table. Eg if m=6, then there are 2^6 = 64 entries in prediction table. 
        //n variable - no. of bits in global history branch register
        //k - represents the length of chooser table. i.e. there are 2^k entries in chooser table 
        //n_bits - represents counter bits in smith
                
        ArrayList<String> actual_pred = new ArrayList<>();
        ArrayList<String> PC = new ArrayList<>();
        
        File filename=new File(args[args.length-1]);
        
        getValues(actual_pred,PC,filename);
        
        switch(predictor)
        {

            case "smithPSC":

                        n_bits = Integer.parseInt(args[1]);

                        smithPSC spscobj=new smithPSC();
                        spscobj.startPrediction(n_bits,actual_pred, filename);
                        break;

            
            case "bimodalPSC":
                        
                        m=Integer.parseInt(args[1]);
                        bimodalPSC bpscobj=new bimodalPSC();
                        bpscobj.startPrediction(m, actual_pred, PC, filename);
                        break;

            case "gsharePSC":

                        m=Integer.parseInt(args[1]);
                        n=Integer.parseInt(args[2]);
            
                        gsharePSC gpscobj=new gsharePSC();
                        gpscobj.startPrediction(m, n, actual_pred, PC, filename);
                        break;            

            case "hybridPSC":
                        
                        k=Integer.parseInt(args[1]);
                        m_gshare=Integer.parseInt(args[2]);
                        n=Integer.parseInt(args[3]);
                        m_bimodal=Integer.parseInt(args[4]);

                        hybridPSC hpscobj=new hybridPSC();
                        hpscobj.startPrediction(k,m_gshare,n,m_bimodal,actual_pred,PC,filename);
                        break;                                    
        }
    }
}